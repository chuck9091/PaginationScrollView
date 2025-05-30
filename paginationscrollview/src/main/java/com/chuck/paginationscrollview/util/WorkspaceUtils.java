package com.chuck.paginationscrollview.util;

public class WorkspaceUtils {
    /**
     * if you drag the item to another workspace, the item will be added to the workspace.
     * And the screen id will be generated by this method.
     */
    private static long mMaxScreenId = 0;

    public static long generateNewScreenId() {
        if (mMaxScreenId < 0) {
            throw new RuntimeException("Error: max screen id was not initialized");
        }
        mMaxScreenId += 1;
        return mMaxScreenId;
    }

    public static void setMaxScreenId(int currentMaxScreenIdIndex) {
        mMaxScreenId = currentMaxScreenIdIndex;
    }
}
