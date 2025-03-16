package com.chuck.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class PaginationItems {

    public static final String PAGINATION_ITEMS_DB = "pagination_items.db";

    static interface ChangeLogColumns extends BaseColumns {
        /**
         * The time of the last update to this row.
         * <P>Type: INTEGER</P>
         */
        public static final String MODIFIED = "modified";
    }


    public static final String TABLE_NAME = "pagination_items";

    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" +
            PaginationItemsProvider.AUTHORITY + "/" + TABLE_NAME);

    /**
     * The content:// style URL for a given row, identified by its id.
     *
     * @param id The row id.
     * @return The unique content URL for the specified row.
     */
    public static Uri getContentUri(long id) {
        return Uri.parse("content://" + PaginationItemsProvider.AUTHORITY +
                "/" + TABLE_NAME + "/" + id);
    }

    /**
     * The container holding the favorite
     * <P>Type: INTEGER</P>
     */
    public static final String CONTAINER = "container";

    /**
     * The icon is a resource identified by a package name and an integer id.
     */
    public static final int CONTAINER_DESKTOP = -100;

    static final String containerToString(int container) {
        switch (container) {
            case CONTAINER_DESKTOP:
                return "desktop";
            default:
                return String.valueOf(container);
        }
    }

    /**
     * The screen holding the favorite (if container is CONTAINER_DESKTOP)
     * <P>Type: INTEGER</P>
     */
    public static final String SCREEN = "screen";

    /**
     * The X coordinate of the cell holding the favorite
     * (if container is CONTAINER_HOTSEAT or CONTAINER_HOTSEAT)
     * <P>Type: INTEGER</P>
     */
    public static final String CELLX = "cellX";

    /**
     * The Y coordinate of the cell holding the favorite
     * (if container is CONTAINER_DESKTOP)
     * <P>Type: INTEGER</P>
     */
    public static final String CELLY = "cellY";

    /**
     * The X span of the cell holding the favorite
     * <P>Type: INTEGER</P>
     */
    public static final String SPANX = "spanX";

    /**
     * The Y span of the cell holding the favorite
     * <P>Type: INTEGER</P>
     */
    public static final String SPANY = "spanY";


    public static void addTableToDb(SQLiteDatabase db, long myProfileId, boolean optional) {
        String ifNotExists = optional ? " IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + ifNotExists + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "container INTEGER," +
                "screen INTEGER," +
                "cellX INTEGER," +
                "cellY INTEGER," +
                "spanX INTEGER," +
                "spanY INTEGER," +
                ");");
    }
}
