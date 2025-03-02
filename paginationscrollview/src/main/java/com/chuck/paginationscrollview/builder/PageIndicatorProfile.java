package com.chuck.paginationscrollview.builder;


public final class PageIndicatorProfile extends BaseProfile {

    private PageIndicatorProfile(Builder builder) {
        super(builder);
    }

    public static final class Builder extends BaseBuilder {
        @Override
        public PageIndicatorProfile build() {
            return new PageIndicatorProfile(this);
        }
    }
}
