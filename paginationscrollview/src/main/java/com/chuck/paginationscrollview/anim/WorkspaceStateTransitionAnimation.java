/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chuck.paginationscrollview.anim;

import static com.chuck.paginationscrollview.anim.AnimatorSetBuilder.ANIM_WORKSPACE_FADE;
import static com.chuck.paginationscrollview.anim.AnimatorSetBuilder.ANIM_WORKSPACE_SCALE;
import static com.chuck.paginationscrollview.anim.Interpolators.LINEAR;
import static com.chuck.paginationscrollview.anim.Interpolators.ZOOM_OUT;
import static com.chuck.paginationscrollview.anim.PropertySetter.NO_ANIM_PROPERTY_SETTER;
import static com.chuck.paginationscrollview.util.LauncherAnimUtils.DRAWABLE_ALPHA;
import static com.chuck.paginationscrollview.util.LauncherAnimUtils.SCALE_PROPERTY;

import android.view.View;
import android.view.animation.Interpolator;

import com.chuck.paginationscrollview.config.AnimationConfig;
import com.chuck.paginationscrollview.config.WorkspaceState;
import com.chuck.paginationscrollview.view.CellLayout;
import com.chuck.paginationscrollview.view.PaginationScrollView;
import com.chuck.paginationscrollview.view.Workspace;

/**
 * Manages the animations between each of the workspace states.
 */
public class WorkspaceStateTransitionAnimation {

    private final PaginationScrollView mPaginationScrollView;
    private final Workspace mWorkspace;

    private float mNewScale;

    public WorkspaceStateTransitionAnimation(PaginationScrollView paginationScrollView, Workspace workspace) {
        mPaginationScrollView = paginationScrollView;
        mWorkspace = workspace;
    }

    public void setStateWithAnimation(AnimatorSetBuilder builder,
                                      AnimationConfig config) {
        setWorkspaceProperty(config.getPropertySetter(builder), builder, config);
    }

    public float getFinalScale() {
        return mNewScale;
    }

    /**
     * Starts a transition animation for the workspace.
     */
    private void setWorkspaceProperty(PropertySetter propertySetter,
                                      AnimatorSetBuilder builder, AnimationConfig config) {
        float[] scaleAndTranslation = WorkspaceState.getWorkspaceScaleAndTranslation();
        mNewScale = scaleAndTranslation[0];
        WorkspaceState.PageAlphaProvider pageAlphaProvider = WorkspaceState.getWorkspacePageAlphaProvider(mPaginationScrollView);
        final int childCount = mWorkspace.getChildCount();
        for (int i = 0; i < childCount; i++) {
            applyChildState((CellLayout) mWorkspace.getChildAt(i), i, pageAlphaProvider,
                    propertySetter, builder, config);
        }

        boolean playAtomicComponent = config.playAtomicComponent();
        if (playAtomicComponent) {
            Interpolator scaleInterpolator = builder.getInterpolator(ANIM_WORKSPACE_SCALE, ZOOM_OUT);
            propertySetter.setFloat(mWorkspace, SCALE_PROPERTY, mNewScale, scaleInterpolator);
        }

        if (!config.playNonAtomicComponent()) {
            // Only the alpha and scale, handled above, are included in the atomic animation.
            return;
        }

        Interpolator translationInterpolator = !playAtomicComponent ? LINEAR : ZOOM_OUT;
        propertySetter.setFloat(mWorkspace, View.TRANSLATION_X,
                scaleAndTranslation[1], translationInterpolator);
        propertySetter.setFloat(mWorkspace, View.TRANSLATION_Y,
                scaleAndTranslation[2], translationInterpolator);
    }

    public void applyChildState(CellLayout cl, int childIndex) {
        applyChildState(cl, childIndex, WorkspaceState.getWorkspacePageAlphaProvider(mPaginationScrollView),
                NO_ANIM_PROPERTY_SETTER, new AnimatorSetBuilder(), new AnimationConfig());
    }

    private void applyChildState(CellLayout cl, int childIndex,
                                 WorkspaceState.PageAlphaProvider pageAlphaProvider, PropertySetter propertySetter,
                                 AnimatorSetBuilder builder, AnimationConfig config) {
        float pageAlpha = pageAlphaProvider.getPageAlpha(childIndex);
        int drawableAlpha = Math.round(pageAlpha * (WorkspaceState.hasWorkspacePageBackground ? 255 : 0));

        if (config.playNonAtomicComponent()) {
            propertySetter.setInt(cl.getScrimBackground(),
                    DRAWABLE_ALPHA, drawableAlpha, ZOOM_OUT);
        }
        if (config.playAtomicComponent()) {
            Interpolator fadeInterpolator = builder.getInterpolator(ANIM_WORKSPACE_FADE,
                    pageAlphaProvider.interpolator);
            propertySetter.setFloat(cl.getShortcutsAndWidgets(), View.ALPHA,
                    pageAlpha, fadeInterpolator);
        }
    }
}