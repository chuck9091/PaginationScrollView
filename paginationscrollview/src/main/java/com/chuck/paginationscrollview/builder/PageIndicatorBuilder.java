package com.chuck.paginationscrollview.builder;

public final class PageIndicatorBuilder extends BaseBuilder{

    public PageIndicatorProfile build(){
        return new PageIndicatorProfile(workspacePadding,workspaceCellPaddingXPx,isVerticalBarLayout);
    }
}
