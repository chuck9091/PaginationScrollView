package com.chuck.paginationscrollview.interfaces;

import android.view.MotionEvent;
import android.view.View;

import com.chuck.paginationscrollview.helper.StylusEventHelper;


/**
 * Simple listener that performs a long click on the view after a stylus button press.
 */
public class SimpleOnStylusPressListener implements StylusEventHelper.StylusButtonListener {
    private View mView;

    public SimpleOnStylusPressListener(View view) {
        mView = view;
    }

    public boolean onPressed(MotionEvent event) {
        return mView.isLongClickable() && mView.performLongClick();
    }

    public boolean onReleased(MotionEvent event) {
        return false;
    }
}