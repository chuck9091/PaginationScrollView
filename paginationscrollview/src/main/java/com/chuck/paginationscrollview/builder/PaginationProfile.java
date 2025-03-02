package com.chuck.paginationscrollview.builder;

import android.graphics.Rect;

public final class PaginationProfile {
    public Rect insets;

    public int edgeMarginPx;

    public boolean isTablet;

    public int iconSizePx;

    public boolean shouldFadeAdjacentWorkspaceScreens;

    public int defaultPageSpacingPx;

    public int cellLayoutPaddingLeftRightPx;

    public int cellLayoutBottomPaddingPx;

    public int heightPx;
    public int widthPx;

    public int numColumns;
    public int numRows;

    public int cellWidthPx;
    public int cellHeightPx;

    public int iconTextSizePx;
    public int iconDrawablePaddingPx;

    public Rect workspacePadding;
    public int workspaceCellPaddingXPx;

    public boolean isVerticalBarLayout;

    private static PaginationProfile paginationProfile;

    public Rect getInsets() {
        return insets;
    }

    public int getEdgeMarginPx() {
        return edgeMarginPx;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public int getIconSizePx() {
        return iconSizePx;
    }

    public int getHeightPx() {
        return heightPx;
    }

    public int getWidthPx() {
        return widthPx;
    }

    public boolean shouldFadeAdjacentWorkspaceScreens() {
        return shouldFadeAdjacentWorkspaceScreens;
    }

    public int getDefaultPageSpacingPx() {
        return defaultPageSpacingPx;
    }

    public int getCellLayoutPaddingLeftRightPx() {
        return cellLayoutPaddingLeftRightPx;
    }

    public int getCellLayoutBottomPaddingPx() {
        return cellLayoutBottomPaddingPx;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getCellWidthPx() {
        return cellWidthPx;
    }

    public int getCellHeightPx() {
        return cellHeightPx;
    }

    public int getIconTextSizePx() {
        return iconTextSizePx;
    }

    public int getIconDrawablePaddingPx() {
        return iconDrawablePaddingPx;
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

    public PaginationProfile(Builder builder) {
        this.workspaceCellPaddingXPx = builder.workspaceCellPaddingXPx;
        this.isVerticalBarLayout = builder.isVerticalBarLayout;

        this.insets = builder.insets;
        this.edgeMarginPx = builder.edgeMarginPx;
        this.isTablet = builder.isTablet;
        this.iconSizePx = builder.iconSizePx;
        this.shouldFadeAdjacentWorkspaceScreens = builder.shouldFadeAdjacentWorkspaceScreens;
        this.defaultPageSpacingPx = builder.defaultPageSpacingPx;
        this.cellLayoutPaddingLeftRightPx = builder.cellLayoutPaddingLeftRightPx;
        this.cellLayoutBottomPaddingPx = builder.cellLayoutBottomPaddingPx;
        this.heightPx = builder.heightPx;
        this.widthPx = builder.widthPx;
        this.numColumns = builder.numColumns;
        this.numRows = builder.numRows;
        this.cellWidthPx = builder.cellWidthPx;
        this.cellHeightPx = builder.cellHeightPx;
        this.iconTextSizePx = builder.iconTextSizePx;
        this.iconDrawablePaddingPx = builder.iconDrawablePaddingPx;
        this.workspacePadding = builder.workspacePadding;
        paginationProfile = this;
    }

    public static PaginationProfile getPaginationProfile() {
        return paginationProfile;
    }

    public static int calculateCellWidth(int width, int countX) {
        return width / countX;
    }

    public static int calculateCellHeight(int height, int countY) {
        return height / countY;
    }

    public static final class Builder {
        private Rect insets = new Rect();
        private int edgeMarginPx = 0;

        private boolean isTablet = false;

        private int iconSizePx;

        private boolean shouldFadeAdjacentWorkspaceScreens;

        private int defaultPageSpacingPx;

        private int cellLayoutPaddingLeftRightPx;

        private int cellLayoutBottomPaddingPx;

        private int heightPx;
        private int widthPx;

        private int numColumns;
        private int numRows;

        private int cellWidthPx;
        private int cellHeightPx;

        private int iconTextSizePx;
        private int iconDrawablePaddingPx;

        public Rect workspacePadding;

        public int workspaceCellPaddingXPx;

        public boolean isVerticalBarLayout;

        public Builder setInsets(Rect insets) {
            this.insets = insets;
            return this;
        }

        public Builder setEdgeMarginPx(int edgeMarginPx) {
            this.edgeMarginPx = edgeMarginPx;
            return this;
        }

        public Builder setIsTablet(boolean isTablet) {
            this.isTablet = isTablet;
            return this;
        }

        public Builder setIconSizePx(int iconSizePx) {
            this.iconSizePx = iconSizePx;
            return this;
        }

        public Builder setShouldFadeAdjacentWorkspaceScreens(boolean shouldFadeAdjacentWorkspaceScreens) {
            this.shouldFadeAdjacentWorkspaceScreens = shouldFadeAdjacentWorkspaceScreens;
            return this;
        }

        public Builder setDefaultPageSpacingPx(int defaultPageSpacingPx) {
            this.defaultPageSpacingPx = defaultPageSpacingPx;
            return this;
        }

        public Builder setCellLayoutPaddingLeftRightPx(int cellLayoutPaddingLeftRightPx) {
            this.cellLayoutPaddingLeftRightPx = cellLayoutPaddingLeftRightPx;
            return this;
        }

        public Builder setCellLayoutBottomPaddingPx(int cellLayoutBottomPaddingPx) {
            this.cellLayoutBottomPaddingPx = cellLayoutBottomPaddingPx;
            return this;
        }

        public Builder setHeightPx(int heightPx) {
            this.heightPx = heightPx;
            return this;
        }

        public Builder setWidthPx(int widthPx) {
            this.widthPx = widthPx;
            return this;
        }

        public Builder setNumColumns(int numColumns) {
            this.numColumns = numColumns;
            return this;
        }

        public Builder setNumRows(int numRows) {
            this.numRows = numRows;
            return this;
        }

        public Builder setCellWidthPx(int cellWidthPx) {
            this.cellWidthPx = cellWidthPx;
            return this;
        }

        public Builder setCellHeightPx(int cellHeightPx) {
            this.cellHeightPx = cellHeightPx;
            return this;
        }

        public Builder setIconTextSizePx(int iconTextSizePx) {
            this.iconTextSizePx = iconTextSizePx;
            return this;
        }

        public Builder setIconDrawablePaddingPx(int iconDrawablePaddingPx) {
            this.iconDrawablePaddingPx = iconDrawablePaddingPx;
            return this;
        }

        public Builder setWorkspacePadding(Rect workspacePadding) {
            this.workspacePadding = workspacePadding;
            return this;
        }

        public Builder setWorkspaceCellPaddingXPx(int workspaceCellPaddingXPx) {
            this.workspaceCellPaddingXPx = workspaceCellPaddingXPx;
            return this;
        }

        public Builder setVerticalBarLayout(boolean verticalBarLayout) {
            isVerticalBarLayout = verticalBarLayout;
            return this;
        }

        public PaginationProfile build() {
            return new PaginationProfile(this);
        }
    }
}
