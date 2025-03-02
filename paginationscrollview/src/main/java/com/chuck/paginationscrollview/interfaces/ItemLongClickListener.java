/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.chuck.paginationscrollview.interfaces;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.view.View.OnLongClickListener;

import com.chuck.paginationscrollview.bean.ItemInfo;
import com.chuck.paginationscrollview.dragndrop.DragController;
import com.chuck.paginationscrollview.dragndrop.DragOptions;
import com.chuck.paginationscrollview.view.CellLayout;
import com.chuck.paginationscrollview.view.PaginationScrollView;

/**
 * Class to handle long-clicks on workspace items and start drag as a result.
 */
public class ItemLongClickListener {

    public static OnLongClickListener INSTANCE_WORKSPACE =
            ItemLongClickListener::onWorkspaceItemLongClick;

    public static OnLongClickListener INSTANCE_ALL_APPS =
            ItemLongClickListener::onAllAppsItemLongClick;

    private static boolean onWorkspaceItemLongClick(View v) {
        PaginationScrollView paginationScrollView = PaginationScrollView.getInstance();
        if (!canStartDrag(paginationScrollView)) return false;
        if (!(v.getTag() instanceof ItemInfo)) return false;

        beginDrag(v, paginationScrollView, (ItemInfo) v.getTag(), new DragOptions());
        return true;
    }

    public static void beginDrag(View v, PaginationScrollView paginationScrollView, ItemInfo info,
            DragOptions dragOptions) {
        CellLayout.CellInfo longClickCellInfo = new CellLayout.CellInfo(v, info);
        paginationScrollView.getWorkspace().startDrag(longClickCellInfo, dragOptions);
    }

    private static boolean onAllAppsItemLongClick(View v) {
        PaginationScrollView paginationScrollView = PaginationScrollView.getInstance();
        if (!canStartDrag(paginationScrollView)) return false;
        // When we have exited all apps or are in transition, disregard long clicks
        if (paginationScrollView.getWorkspace().isSwitchingState()) return false;

        // Start the drag
        final DragController dragController = paginationScrollView.getDragController();
        dragController.addDragListener(new DragController.DragListener() {
            @Override
            public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
                v.setVisibility(INVISIBLE);
            }

            @Override
            public void onDragEnd() {
                v.setVisibility(VISIBLE);
                dragController.removeDragListener(this);
            }
        });

        DragOptions options = new DragOptions();
        options.intrinsicIconScaleFactor = 1;
        paginationScrollView.getWorkspace().beginDragShared(v, null, options);
        return false;
    }

    public static boolean canStartDrag(PaginationScrollView paginationScrollView) {
        if (paginationScrollView == null) {
            return false;
        }
        // Return early if an item is already being dragged (e.g. when long-pressing two shortcuts)
        if (paginationScrollView.getDragController().isDragging()) return false;

        return true;
    }
}
