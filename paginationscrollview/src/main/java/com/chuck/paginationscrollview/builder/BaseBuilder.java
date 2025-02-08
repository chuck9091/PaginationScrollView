package com.chuck.paginationscrollview.builder;

import android.graphics.Rect;

public class BaseBuilder {
    public Rect workspacePadding;

    public int workspaceCellPaddingXPx;

    public boolean isVerticalBarLayout;

    protected BaseBuilder setWorkspacePadding(Rect workspacePadding){
        this.workspacePadding = workspacePadding;
        return this;
    }

    protected BaseBuilder setWorkspaceCellPaddingXPx(int workspaceCellPaddingXPx){
        this.workspaceCellPaddingXPx = workspaceCellPaddingXPx;
        return this;
    }

    protected BaseBuilder setVerticalBarLayout(boolean isVerticalBarLayout){
        this.isVerticalBarLayout = isVerticalBarLayout;
        return this;
    }
}
