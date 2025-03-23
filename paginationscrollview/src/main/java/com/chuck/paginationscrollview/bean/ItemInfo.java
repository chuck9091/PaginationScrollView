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

package com.chuck.paginationscrollview.bean;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

//import com.chuck.model.database.PaginationItems;
//import com.chuck.model.utils.ContentWriter;

/**
 * Represents an item in the launcher.
 */
public class ItemInfo {

    public static final int NO_ID = -1;

    public long container;

    public int id;

    /**
     * Indicates the screen in which the shortcut appears if the container types is
     */
    public long screenId = -1;

    /**
     * Indicates the X position of the associated cell.
     */
    public int cellX = -1;

    /**
     * Indicates the Y position of the associated cell.
     */
    public int cellY = -1;

    /**
     * Indicates the X cell span.
     */
    public int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    public int spanY = 1;

    /**
     * Indicates the position in an ordered list.
     */
    public int rank = 0;

    /**
     * Title of the item
     */
    public CharSequence title;

    public Drawable iconDrawable;

    public ItemInfo() {
    }

    public void copyFrom(ItemInfo info) {
        cellX = info.cellX;
        cellY = info.cellY;
        spanX = info.spanX;
        spanY = info.spanY;
        rank = info.rank;
        screenId = info.screenId;
        iconDrawable = info.iconDrawable;
    }

    public Intent getIntent() {
        return null;
    }

    public ComponentName getTargetComponent() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getComponent();
        } else {
            return null;
        }
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + dumpProperties() + ")";
    }

    protected String dumpProperties() {
        return " screen=" + screenId
                + " cell(" + cellX + "," + cellY + ")"
                + " span(" + spanX + "," + spanY + ")"
                + " rank=" + rank
                + " title=" + title;
    }

    /**
     * Whether this item is disabled.
     */
    public boolean isDisabled() {
        return false;
    }

//    public void onAddToDatabase(ContentWriter writer) {
//        writeToValues(writer);
//    }
//
//    public void writeToValues(ContentWriter writer) {
//        writer.put(PaginationItems.CONTAINER, container)
//                .put(PaginationItems.SCREEN, screenId)
//                .put(PaginationItems.CELLX, cellX)
//                .put(PaginationItems.CELLY, cellY)
//                .put(PaginationItems.SPANX, spanX)
//                .put(PaginationItems.SPANY, spanY);
//    }
}
