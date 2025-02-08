package com.chuck.paginationscrollview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chuck.paginationscrollview.R;
import com.chuck.paginationscrollview.builder.PageIndicatorProfile;
import com.chuck.paginationscrollview.builder.PaginationProfile;
import com.chuck.paginationscrollview.pageindicators.WorkspacePageIndicator;

public class PaginationScrollView extends FrameLayout {

    private PageIndicatorProfile pageIndicatorProfile;

    private PaginationProfile paginationProfile;

    public PaginationScrollView(@NonNull Context context) {
        super(context);
    }

    public PaginationScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaginationScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public PaginationScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPageIndicatorProfile(PageIndicatorProfile pageIndicatorProfile) {
        this.pageIndicatorProfile = pageIndicatorProfile;
    }

    public PageIndicatorProfile getPageIndicatorProfile() {
        return this.pageIndicatorProfile;
    }

    public void setPaginationProfile(PaginationProfile paginationProfile) {
        this.paginationProfile = paginationProfile;
    }
}
