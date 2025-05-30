/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.chuck.model;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.chuck.model.database.PaginationItems;
import com.chuck.model.database.PaginationItemsProvider;
import com.chuck.model.utils.ContentWriter;
import com.chuck.paginationscrollview.bean.ItemInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * Class for handling model updates.
 */
public class ModelWriter {

    private static final String TAG = "ModelWriter";

    private final Context mContext;
    private final LauncherModel mModel;
    private final BgDataModel mBgDataModel;
    private final Handler mUiHandler;

    private final Executor mWorkerExecutor;
    private final boolean mHasVerticalHotseat;
    private final boolean mVerifyChanges;

    public ModelWriter(Context context, LauncherModel model, BgDataModel dataModel,
                       boolean hasVerticalHotseat, boolean verifyChanges) {
        mContext = context;
        mModel = model;
        mBgDataModel = dataModel;
        mWorkerExecutor = new LooperExecutor(LauncherModel.getWorkerLooper());
        mHasVerticalHotseat = hasVerticalHotseat;
        mVerifyChanges = verifyChanges;
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    private void updateItemInfoProps(
            ItemInfo item, long container, long screenId, int cellX, int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.screenId = screenId;
    }

    /**
     * Adds an item to the DB if it was not created previously, or move it to a new
     * <container, screen, cellX, cellY>
     */
    public void addOrMoveItemInDatabase(ItemInfo item,
                                        long container, long screenId, int cellX, int cellY) {
        if (item.container == ItemInfo.NO_ID) {
            // From all apps
            addItemToDatabase(item, container, screenId, cellX, cellY);
        } else {
            // From somewhere else
            moveItemInDatabase(item, container, screenId, cellX, cellY);
        }
    }

    private void checkItemInfoLocked(long itemId, ItemInfo item, StackTraceElement[] stackTrace) {
        ItemInfo modelItem = mBgDataModel.itemsIdMap.get(itemId);
        if (modelItem != null && item != modelItem) {
            // check all the data is consistent
            if (modelItem.title.toString().equals(item.title.toString()) &&
                    modelItem.id == item.id &&
                    modelItem.container == item.container &&
                    modelItem.screenId == item.screenId &&
                    modelItem.cellX == item.cellX &&
                    modelItem.cellY == item.cellY &&
                    modelItem.spanX == item.spanX &&
                    modelItem.spanY == item.spanY) {
                // For all intents and purposes, this is the same object
                return;
            }

            // the modelItem needs to match up perfectly with item if our model is
            // to be consistent with the database-- for now, just require
            // modelItem == item or the equality check above
            String msg = "item: " + ((item != null) ? item.toString() : "null") +
                    "modelItem: " +
                    ((modelItem != null) ? modelItem.toString() : "null") +
                    "Error: ItemInfo passed to checkItemInfo doesn't match original";
            RuntimeException e = new RuntimeException(msg);
            if (stackTrace != null) {
                e.setStackTrace(stackTrace);
            }
            throw e;
        }
    }

    /**
     * Move an item in the DB to a new <container, screen, cellX, cellY>
     */
    public void moveItemInDatabase(final ItemInfo item,
                                   long container, long screenId, int cellX, int cellY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);

        final ContentWriter writer = new ContentWriter(mContext)
                .put(PaginationItems.CONTAINER, item.container)
                .put(PaginationItems.CELLX, item.cellX)
                .put(PaginationItems.CELLY, item.cellY)
                .put(PaginationItems.SCREEN, item.screenId);

        mWorkerExecutor.execute(new UpdateItemRunnable(item, writer));
    }

    /**
     * Move items in the DB to a new <container, screen, cellX, cellY>. We assume that the
     * cellX, cellY have already been updated on the ItemInfos.
     */
    public void moveItemsInDatabase(final ArrayList<ItemInfo> items, long container, int screen) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        int count = items.size();

        for (int i = 0; i < count; i++) {
            ItemInfo item = items.get(i);
            updateItemInfoProps(item, container, screen, item.cellX, item.cellY);

            final ContentValues values = new ContentValues();
            values.put(PaginationItems.CONTAINER, item.container);
            values.put(PaginationItems.CELLX, item.cellX);
            values.put(PaginationItems.CELLY, item.cellY);
            values.put(PaginationItems.SCREEN, item.screenId);

            contentValues.add(values);
        }
        mWorkerExecutor.execute(new UpdateItemsRunnable(items, contentValues));
    }

    /**
     * Move and/or resize item in the DB to a new <container, screen, cellX, cellY, spanX, spanY>
     */
    public void modifyItemInDatabase(final ItemInfo item,
                                     long container, long screenId, int cellX, int cellY, int spanX, int spanY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);
        item.spanX = spanX;
        item.spanY = spanY;

        final ContentWriter writer = new ContentWriter(mContext)
                .put(PaginationItems.CONTAINER, item.container)
                .put(PaginationItems.CELLX, item.cellX)
                .put(PaginationItems.CELLY, item.cellY)
                .put(PaginationItems.SPANX, item.spanX)
                .put(PaginationItems.SPANY, item.spanY)
                .put(PaginationItems.SCREEN, item.screenId);

        mWorkerExecutor.execute(new UpdateItemRunnable(item, writer));
    }

    /**
     * Update an item to the database in a specified container.
     */
    public void updateItemInDatabase(ItemInfo item) {
        ContentWriter writer = new ContentWriter(mContext);
        item.onAddToDatabase(writer);
        mWorkerExecutor.execute(new UpdateItemRunnable(item, writer));
    }

    /**
     * Add an item to the database in a specified container. Sets the container, screen, cellX and
     * cellY fields of the item. Also assigns an ID to the item.
     */
    public void addItemToDatabase(final ItemInfo item,
                                  long container, long screenId, int cellX, int cellY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);

        final ContentWriter writer = new ContentWriter(mContext);
        final ContentResolver cr = mContext.getContentResolver();
        item.onAddToDatabase(writer);

        ModelVerifier verifier = new ModelVerifier();

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        mWorkerExecutor.execute(() -> {
            cr.insert(PaginationItems.CONTENT_URI, writer.getValues(mContext));

            synchronized (mBgDataModel) {
                checkItemInfoLocked(item.id, item, stackTrace);
                mBgDataModel.addItem(mContext, item, true);
                verifier.verifyModel();
            }
        });
    }

    /**
     * Removes the specified item from the database
     */
    public void deleteItemFromDatabase(ItemInfo item) {
        deleteItemsFromDatabase(Arrays.asList(item));
    }

    /**
     * Removes all the items from the database matching {@param matcher}.
     */
    public void deleteItemsFromDatabase(ItemInfoMatcher matcher) {
        deleteItemsFromDatabase(matcher.filterItemInfos(mBgDataModel.itemsIdMap));
    }

    /**
     * Removes the specified items from the database
     */
    public void deleteItemsFromDatabase(final Iterable<? extends ItemInfo> items) {
        ModelVerifier verifier = new ModelVerifier();

        mWorkerExecutor.execute(() -> {
            for (ItemInfo item : items) {
                final Uri uri = PaginationItems.getContentUri(item.id);
                mContext.getContentResolver().delete(uri, null, null);

                mBgDataModel.removeItem(mContext, item);
                verifier.verifyModel();
            }
        });
    }

    private class UpdateItemRunnable extends UpdateItemBaseRunnable {
        private final ItemInfo mItem;
        private final ContentWriter mWriter;
        private final long mItemId;

        UpdateItemRunnable(ItemInfo item, ContentWriter writer) {
            mItem = item;
            mWriter = writer;
            mItemId = item.id;
        }

        @Override
        public void run() {
            Uri uri = PaginationItems.getContentUri(mItemId);
            mContext.getContentResolver().update(uri, mWriter.getValues(mContext), null, null);
            updateItemArrays(mItem, mItemId);
        }
    }

    private class UpdateItemsRunnable extends UpdateItemBaseRunnable {
        private final ArrayList<ContentValues> mValues;
        private final ArrayList<ItemInfo> mItems;

        UpdateItemsRunnable(ArrayList<ItemInfo> items, ArrayList<ContentValues> values) {
            mValues = values;
            mItems = items;
        }

        @Override
        public void run() {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            int count = mItems.size();
            for (int i = 0; i < count; i++) {
                ItemInfo item = mItems.get(i);
                final long itemId = item.id;
                final Uri uri = PaginationItems.getContentUri(itemId);
                ContentValues values = mValues.get(i);

                ops.add(ContentProviderOperation.newUpdate(uri).withValues(values).build());
                updateItemArrays(item, itemId);
            }
            try {
                mContext.getContentResolver().applyBatch(PaginationItemsProvider.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private abstract class UpdateItemBaseRunnable implements Runnable {
        private final StackTraceElement[] mStackTrace;
        private final ModelVerifier mVerifier = new ModelVerifier();

        UpdateItemBaseRunnable() {
            mStackTrace = new Throwable().getStackTrace();
        }

        protected void updateItemArrays(ItemInfo item, long itemId) {
            // Lock on mBgLock *after* the db operation
            synchronized (mBgDataModel) {
                checkItemInfoLocked(itemId, item, mStackTrace);

                // Items are added/removed from the corresponding FolderInfo elsewhere, such
                // as in Workspace.onDrop. Here, we just add/remove them from the list of items
                // that are on the desktop, as appropriate
                ItemInfo modelItem = mBgDataModel.itemsIdMap.get(itemId);
                if (modelItem != null &&
                        (modelItem.container == PaginationItems.CONTAINER_DESKTOP)) {
                    if (!mBgDataModel.workspaceItems.contains(modelItem)) {
                        mBgDataModel.workspaceItems.add(modelItem);
                    }
                } else {
                    mBgDataModel.workspaceItems.remove(modelItem);
                }
                mVerifier.verifyModel();
            }
        }
    }

    /**
     * Utility class to verify model updates are propagated properly to the callback.
     */
    public class ModelVerifier {

        final int startId;

        ModelVerifier() {
            startId = mBgDataModel.lastBindId;
        }

        void verifyModel() {
            if (!mVerifyChanges || mModel.getCallback() == null) {
                return;
            }

            int executeId = mBgDataModel.lastBindId;

            mUiHandler.post(() -> {
                int currentId = mBgDataModel.lastBindId;
                if (currentId > executeId) {
                    // Model was already bound after job was executed.
                    return;
                }
                if (executeId == startId) {
                    // Bound model has not changed during the job
                    return;
                }
                // Bound model was changed between submitting the job and executing the job
                Callbacks callbacks = mModel.getCallback();
                if (callbacks != null) {
                    callbacks.rebindModel();
                }
            });
        }
    }
}
