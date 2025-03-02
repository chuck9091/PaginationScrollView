package com.chuck.paginationscrollview.util;

public class WorkspaceUtils {
    private static long mMaxScreenId = -1;

    public static long generateNewScreenId() {
        if (mMaxScreenId < 0) {
            throw new RuntimeException("Error: max screen id was not initialized");
        }
        mMaxScreenId += 1;
        return mMaxScreenId;
    }
}
