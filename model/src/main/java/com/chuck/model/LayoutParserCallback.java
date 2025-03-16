package com.chuck.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public interface LayoutParserCallback {
    long generateNewItemId();

    long insertAndCheck(SQLiteDatabase db, ContentValues values);
}