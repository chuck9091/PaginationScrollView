package com.chuck.paginationscrollview.builder;

import android.graphics.Rect;

public final class PageIndicatorProfile {

    private static PageIndicatorProfile pageIndicatorProfile = new PageIndicatorProfile();

    public Rect workspacePadding;

    public int workspaceCellPaddingXPx;

    public boolean isVerticalBarLayout;

    public PageIndicatorProfile() {
    }

    public PageIndicatorProfile(Rect workspacePadding, int workspaceCellPaddingXPx, boolean isVerticalBarLayout) {
        this.workspacePadding = workspacePadding;
        this.workspaceCellPaddingXPx = workspaceCellPaddingXPx;
        this.isVerticalBarLayout = isVerticalBarLayout;
        pageIndicatorProfile = this;
    }

    public static PageIndicatorProfile getInstance() {
        return pageIndicatorProfile;
    }
}
