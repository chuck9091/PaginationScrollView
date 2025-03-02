package com.chuck.paginationscrollview.builder;

import android.graphics.Rect;

public class BaseProfile {
    public Rect workspacePadding;

    public int workspaceCellPaddingXPx;

    public boolean isVerticalBarLayout;

    public BaseProfile(BaseBuilder builder) {
        this.workspacePadding = builder.workspacePadding;
        this.workspaceCellPaddingXPx = builder.workspaceCellPaddingXPx;
        this.isVerticalBarLayout = builder.isVerticalBarLayout;
    }

    public Rect getWorkspacePadding() {
        return workspacePadding;
    }

    public int getWorkspaceCellPaddingXPx() {
        return workspaceCellPaddingXPx;
    }

    public boolean isVerticalBarLayout() {
        return isVerticalBarLayout;
    }

    public static class BaseBuilder {
        public Rect workspacePadding;
        public int workspaceCellPaddingXPx;

        public boolean isVerticalBarLayout;

        public BaseBuilder setWorkspacePadding(Rect workspacePadding) {
            this.workspacePadding = workspacePadding;
            return this;
        }

        public BaseBuilder setWorkspaceCellPaddingXPx(int workspaceCellPaddingXPx) {
            this.workspaceCellPaddingXPx = workspaceCellPaddingXPx;
            return this;
        }

        public BaseBuilder setVerticalBarLayout(boolean verticalBarLayout) {
            isVerticalBarLayout = verticalBarLayout;
            return this;
        }

        public BaseProfile build() {
            return new BaseProfile(this);
        }
    }
}
