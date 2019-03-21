package com.chengfu.android.fuplayer.demo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DodoBelowBehavior extends CoordinatorLayout.Behavior<View> {

    private final static String TAG="DodoBelowBehavior";

    public DodoBelowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG,"DodoBelowBehavior");
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setY(dependency.getY() + dependency.getHeight());
        Log.d(TAG,"onDependentViewChanged ");
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        Log.d(TAG,"layoutDependsOn");
        return dependency instanceof DodoMoveView;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d(TAG,"onStartNestedScroll");
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }
}
