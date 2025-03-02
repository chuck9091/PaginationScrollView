package com.chuck.paginationscrollview.config;

import static com.chuck.paginationscrollview.anim.Interpolators.ACCEL_2;

import android.view.animation.Interpolator;

import com.chuck.paginationscrollview.view.PaginationScrollView;

public class WorkspaceState {
    public static float[] getWorkspaceScaleAndTranslation() {
        return new float[]{1, 0, 0};
    }

    public static boolean hasWorkspacePageBackground = true;

    public static PageAlphaProvider getWorkspacePageAlphaProvider(PaginationScrollView paginationScrollView) {
        final int centerPage = paginationScrollView.getWorkspace().getNextPage();
        return new PageAlphaProvider(ACCEL_2) {
            @Override
            public float getPageAlpha(int pageIndex) {
                return pageIndex != centerPage ? 0 : 1f;
            }
        };
    }

    public static abstract class PageAlphaProvider {

        public final Interpolator interpolator;

        public PageAlphaProvider(Interpolator interpolator) {
            this.interpolator = interpolator;
        }

        public abstract float getPageAlpha(int pageIndex);
    }
}
