package com.chuck.paginationscrollview.config;

import static com.chuck.paginationscrollview.anim.PropertySetter.NO_ANIM_PROPERTY_SETTER;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

import androidx.annotation.IntDef;

import com.chuck.paginationscrollview.anim.AnimatorPlaybackController;
import com.chuck.paginationscrollview.anim.AnimatorSetBuilder;
import com.chuck.paginationscrollview.anim.PropertySetter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnimationConfig extends AnimatorListenerAdapter {

    @IntDef(flag = true, value = {
            NON_ATOMIC_COMPONENT,
            ATOMIC_COMPONENT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationComponents {}
    public static final int NON_ATOMIC_COMPONENT = 1 << 0;
    public static final int ATOMIC_COMPONENT = 1 << 1;

    public static final int ANIM_ALL = NON_ATOMIC_COMPONENT | ATOMIC_COMPONENT;

    public long duration;
    public boolean userControlled;
    public AnimatorPlaybackController playbackController;
    public @AnimationComponents int animComponents = ANIM_ALL;
    private PropertySetter mPropertySetter;

    private AnimatorSet mCurrentAnimation;

    /**
     * Cancels the current animation and resets config variables.
     */
    public void reset() {
        duration = 0;
        userControlled = false;
        animComponents = ANIM_ALL;
        mPropertySetter = null;

        if (playbackController != null) {
            playbackController.getAnimationPlayer().cancel();
            playbackController.dispatchOnCancel();
        } else if (mCurrentAnimation != null) {
            mCurrentAnimation.setDuration(0);
            mCurrentAnimation.cancel();
        }

        mCurrentAnimation = null;
        playbackController = null;
    }

    public PropertySetter getPropertySetter(AnimatorSetBuilder builder) {
        if (mPropertySetter == null) {
            mPropertySetter = duration == 0 ? NO_ANIM_PROPERTY_SETTER
                    : new PropertySetter.AnimatedPropertySetter(duration, builder);
        }
        return mPropertySetter;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (playbackController != null && playbackController.getTarget() == animation) {
            playbackController = null;
        }
        if (mCurrentAnimation == animation) {
            mCurrentAnimation = null;
        }
    }

    public void setAnimation(AnimatorSet animation) {
        mCurrentAnimation = animation;
        mCurrentAnimation.addListener(this);
    }

    public boolean playAtomicComponent() {
        return (animComponents & ATOMIC_COMPONENT) != 0;
    }

    public boolean playNonAtomicComponent() {
        return (animComponents & NON_ATOMIC_COMPONENT) != 0;
    }
}
