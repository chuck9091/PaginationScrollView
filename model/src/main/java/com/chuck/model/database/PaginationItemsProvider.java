/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chuck.model.database;

import static com.chuck.model.database.PaginationItems.PAGINATION_ITEMS_DB;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.chuck.model.LayoutParserCallback;
import com.chuck.utils.Utilities;

import java.io.File;
import java.util.ArrayList;

public class PaginationItemsProvider extends ContentProvider {
    private static final String TAG = "PaginationItemsProvider";
    private static final boolean LOGD = false;

    private static final String DOWNGRADE_SCHEMA_FILE = "downgrade_schema.json";

    /**
     * Represents the schema of the database. Changes in scheme need not be backwards compatible.
     */
    public static final int SCHEMA_VERSION = 1;

    public static final String AUTHORITY = "com.chuck.paginationitems";

    static final String EMPTY_DATABASE_CREATED = "EMPTY_DATABASE_CREATED";

    private static final String RESTRICTION_PACKAGE_NAME = "workspace.configuration.package.name";
    private Handler mListenerHandler;

    protected DatabaseHelper mOpenHelper;


    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri uri) {
        DatabaseHelper.SqlArguments args = new DatabaseHelper.SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    /**
     * Overridden in tests
     */
    protected synchronized void createDbIfNotExists() {
        if (mOpenHelper == null) {
            mOpenHelper = new DatabaseHelper(getContext(), mListenerHandler);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        createDbIfNotExists();

        DatabaseHelper.SqlArguments args = new DatabaseHelper.SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    static long dbInsertAndCheck(DatabaseHelper helper,
                                 SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (values == null) {
            throw new RuntimeException("Error: attempting to insert null values");
        }
        if (!values.containsKey(PaginationItems.ChangeLogColumns._ID)) {
            throw new RuntimeException("Error: attempting to add item without specifying an id");
        }
        return db.insert(table, nullColumnHack, values);
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        createDbIfNotExists();
        DatabaseHelper.SqlArguments args = new DatabaseHelper.SqlArguments(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        addModifiedTime(initialValues);
        final long rowId = dbInsertAndCheck(mOpenHelper, db, args.table, null, initialValues);
        if (rowId < 0) return null;

        uri = ContentUris.withAppendedId(uri, rowId);
        notifyListeners();
        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        createDbIfNotExists();
        DatabaseHelper.SqlArguments args = new DatabaseHelper.SqlArguments(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try (Utilities.SQLiteTransaction t = new Utilities.SQLiteTransaction(db)) {
            int numValues = values.length;
            for (int i = 0; i < numValues; i++) {
                addModifiedTime(values[i]);
                if (dbInsertAndCheck(mOpenHelper, db, args.table, null, values[i]) < 0) {
                    return 0;
                }
            }
            t.commit();
        }

        notifyListeners();
        return values.length;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        createDbIfNotExists();
        try (Utilities.SQLiteTransaction t = new Utilities.SQLiteTransaction(mOpenHelper.getWritableDatabase())) {
            ContentProviderResult[] result = super.applyBatch(operations);
            t.commit();
            return result;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        createDbIfNotExists();
        DatabaseHelper.SqlArguments args = new DatabaseHelper.SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) {
            notifyListeners();
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        createDbIfNotExists();
        DatabaseHelper.SqlArguments args = new DatabaseHelper.SqlArguments(uri, selection, selectionArgs);

        addModifiedTime(values);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        if (count > 0) notifyListeners();

        return count;
    }

    /**
     * Overridden in tests
     */
    protected void notifyListeners() {
    }

    static void addModifiedTime(ContentValues values) {
        values.put(PaginationItems.ChangeLogColumns.MODIFIED, System.currentTimeMillis());
    }

    /**
     * The class is subclassed in tests to create an in-memory db.
     */
    public static class DatabaseHelper extends NoLocaleSQLiteHelper implements LayoutParserCallback {
        private final Handler mWidgetHostResetHandler;
        private final Context mContext;
        private long mMaxItemId = -1;
        private long mMaxScreenId = -1;

        DatabaseHelper(Context context, Handler widgetHostResetHandler) {
            this(context, widgetHostResetHandler, PAGINATION_ITEMS_DB);
            // Table creation sometimes fails silently, which leads to a crash loop.
            // This way, we will try to create a table every time after crash, so the device
            // would eventually be able to recover.
            if (!tableExists(PaginationItems.TABLE_NAME)) {
                Log.e(TAG, "Tables are missing after onCreate has been called. Trying to recreate");
                // This operation is a no-op if the table already exists.
                addFavoritesTable(getWritableDatabase(), true);
            }

            initIds();
        }

        /**
         * Constructor used in tests and for restore.
         */
        public DatabaseHelper(
                Context context, Handler widgetHostResetHandler, String tableName) {
            super(context, tableName, SCHEMA_VERSION);
            mContext = context;
            mWidgetHostResetHandler = widgetHostResetHandler;
        }

        protected void initIds() {

        }

        private boolean tableExists(String tableName) {
            Cursor c = getReadableDatabase().query(
                    true, "sqlite_master", new String[]{"tbl_name"},
                    "tbl_name = ?", new String[]{tableName},
                    null, null, null, null, null);
            try {
                return c.getCount() > 0;
            } finally {
                c.close();
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (LOGD) Log.d(TAG, "creating new launcher database");

            mMaxItemId = 1;
            mMaxScreenId = 0;

            addFavoritesTable(db, false);

            // Fresh and clean launcher DB.
            onEmptyDbCreated();
        }

        /**
         * Overriden in tests.
         */
        protected void onEmptyDbCreated() {
        }

        public long getDefaultUserSerial() {
            return 0;
        }

        private void addFavoritesTable(SQLiteDatabase db, boolean optional) {
            PaginationItems.addTableToDb(db, getDefaultUserSerial(), optional);
        }

        private void removeOrphanedItems(SQLiteDatabase db) {
            // Delete items directly on the workspace who's screen id doesn't exist
            //  "DELETE FROM favorites WHERE screen NOT IN (SELECT _id FROM workspaceScreens)
            //   AND container = -100"
            String removeOrphanedDesktopItems = "DELETE FROM " + PaginationItems.TABLE_NAME +
                    " WHERE " +
                    PaginationItems.CONTAINER + " = " +
                    PaginationItems.CONTAINER_DESKTOP;
            db.execSQL(removeOrphanedDesktopItems);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

            File schemaFile = mContext.getFileStreamPath(DOWNGRADE_SCHEMA_FILE);
            if (!schemaFile.exists()) {
                handleOneTimeDataUpgrade(db);
            }
        }

        /**
         * One-time data updated before support of onDowngrade was added. This update is backwards
         * compatible and can safely be run multiple times.
         * Note: No new logic should be added here after release, as the new logic might not get
         * executed on an existing device.
         * TODO: Move this to db upgrade path, once the downgrade path is released.
         */
        protected void handleOneTimeDataUpgrade(SQLiteDatabase db) {
            // Remove "profile extra"
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (LOGD) Log.d(TAG, "onUpgrade triggered: " + oldVersion);
            switch (oldVersion) {
            }

            // DB was not upgraded
            Log.w(TAG, "Destroying all old data.");
            createEmptyDB(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {

            } catch (Exception e) {
                Log.d(TAG, "Unable to downgrade from: " + oldVersion + " to " + newVersion +
                        ". Wiping databse.", e);
                createEmptyDB(db);
            }
        }

        /**
         * Clears all the data for a fresh start.
         */
        public void createEmptyDB(SQLiteDatabase db) {
            try (Utilities.SQLiteTransaction t = new Utilities.SQLiteTransaction(db)) {
                db.execSQL("DROP TABLE IF EXISTS " + PaginationItems.TABLE_NAME);
                onCreate(db);
                t.commit();
            }
        }

        @Override
        public long generateNewItemId() {
            return 0;
        }

        @Override
        public long insertAndCheck(SQLiteDatabase db, ContentValues values) {
            return 0;
        }

        static class SqlArguments {
            public final String table;
            public final String where;
            public final String[] args;

            SqlArguments(Uri url, String where, String[] args) {
                if (url.getPathSegments().size() == 1) {
                    this.table = url.getPathSegments().get(0);
                    this.where = where;
                    this.args = args;
                } else if (url.getPathSegments().size() != 2) {
                    throw new IllegalArgumentException("Invalid URI: " + url);
                } else if (!TextUtils.isEmpty(where)) {
                    throw new UnsupportedOperationException("WHERE clause not supported: " + url);
                } else {
                    this.table = url.getPathSegments().get(0);
                    this.where = "_id=" + ContentUris.parseId(url);
                    this.args = null;
                }
            }

            SqlArguments(Uri url) {
                if (url.getPathSegments().size() == 1) {
                    table = url.getPathSegments().get(0);
                    where = null;
                    args = null;
                } else {
                    throw new IllegalArgumentException("Invalid URI: " + url);
                }
            }
        }
    }
}
