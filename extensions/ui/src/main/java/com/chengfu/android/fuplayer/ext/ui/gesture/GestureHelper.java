package com.chengfu.android.fuplayer.ext.ui.gesture;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class GestureHelper {

    public static final String TAG = "GestureHelper";

    public static final float DEFAULT_SLIP_RATE = 0.8f;//滑动速率

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SLIDE_TYPE_PROGRESS, SLIDE_TYPE_VOLUME, SLIDE_TYPE_BRIGHTNESS})
    @interface SlideType {
    }

    public static final int SLIDE_TYPE_BRIGHTNESS = 1;
    public static final int SLIDE_TYPE_PROGRESS = 2;
    public static final int SLIDE_TYPE_VOLUME = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_TYPE_NONE, SHOW_TYPE_BRIGHTNESS, SHOW_TYPE_PROGRESS, SHOW_TYPE_VOLUME})
    @interface ShowType {
    }

    public static final int SHOW_TYPE_NONE = 1;
    public static final int SHOW_TYPE_BRIGHTNESS = 1 << 1;
    public static final int SHOW_TYPE_PROGRESS = 1 << 2;
    public static final int SHOW_TYPE_VOLUME = 1 << 3;


    private boolean firstTouch;
    private boolean horizontalSlide;
    private boolean rightVerticalSlide;
    private int viewWidth;
    private int viewHeight;
    private boolean scrolling;

    @SlideType
    private int slideType;

    @ShowType
    private int showType = SHOW_TYPE_NONE;

    private OnSlideChangedListener onSlideChangedListener;

    private Activity activity;
    private float oldBrightness;

    private AudioManager audioManager;
    private int maxVolume;
    private int oldVolume;


    public interface OnSlideChangedListener {

        void onStartSlide(@SlideType int slideType);

        void onPercentChanged(@SlideType int slideType, int percent);

        void onStopSlide(@SlideType int slideType);
    }

    public GestureHelper(View view) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            viewWidth = view.getWidth();
            viewHeight = view.getHeight();
        });

        audioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);

        maxVolume = audioManager != null ? audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 10;

        if (view.getContext() instanceof Activity) {
            activity = (Activity) view.getContext();
        }
    }

    private float getSystemBrightness() {
        if (activity == null) {
            return 0.5f;
        }
        try {
            int systemBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            return systemBrightness * 1.0f / 255;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        }
    }

    public void setOnSlideChangedListener(OnSlideChangedListener onSlideChangedListener) {
        this.onSlideChangedListener = onSlideChangedListener;
    }

    public void setShowType(@ShowType int showType) {
        this.showType = showType;
    }

    public void onDown(MotionEvent ev) {
        firstTouch = true;
    }

    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        float mOldX = e1.getX();
        float mOldY = e1.getY();
        float deltaY = mOldY - e2.getY();
        float deltaX = mOldX - e2.getX();

        if (firstTouch) {
            horizontalSlide = Math.abs(distanceX) >= Math.abs(distanceY);
            rightVerticalSlide = mOldX > viewWidth * 0.5f;
            firstTouch = false;
        }

        if ((showType & SHOW_TYPE_NONE) != 0) {
            return;
        }

        if (horizontalSlide) {
            if ((showType & SHOW_TYPE_PROGRESS) != 0) {
                onProgressSlide(-deltaX / viewWidth / DEFAULT_SLIP_RATE);
            }
        } else {
            if (Math.abs(deltaY) > viewHeight)
                return;
            if (rightVerticalSlide) {
                if ((showType & SHOW_TYPE_VOLUME) != 0) {
                    onVolumeSlide(deltaY / viewHeight / DEFAULT_SLIP_RATE);
                }
            } else {
                if ((showType & SHOW_TYPE_BRIGHTNESS) != 0) {
                    onBrightnessSlide(deltaY / viewHeight / DEFAULT_SLIP_RATE);
                }
            }
        }
    }

    public void onUp(MotionEvent ev) {
        if (scrolling) {
            scrolling = false;
            if ((showType & SHOW_TYPE_NONE) != 0) {
                return;
            }
            if (slideType == SLIDE_TYPE_BRIGHTNESS && (showType & SHOW_TYPE_BRIGHTNESS) == 0) {
                return;
            }
            if (slideType == SLIDE_TYPE_PROGRESS && (showType & SHOW_TYPE_PROGRESS) == 0) {
                return;
            }
            if (slideType == SLIDE_TYPE_VOLUME && (showType & SHOW_TYPE_VOLUME) == 0) {
                return;
            }

            if (onSlideChangedListener != null) {
                onSlideChangedListener.onStopSlide(slideType);
            }
        }
    }

    private void onBrightnessSlide(float percent) {
        slideType = SLIDE_TYPE_BRIGHTNESS;

        if (!scrolling) {
            oldBrightness = activity != null ? activity.getWindow().getAttributes().screenBrightness : WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            oldBrightness = oldBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE ? getSystemBrightness() : oldBrightness;
            if (onSlideChangedListener != null) {
                onSlideChangedListener.onStartSlide(slideType);
            }
            scrolling = true;
            return;
        }

        float newBrightness = percent + oldBrightness;
        if (newBrightness > 1.0f) {
            newBrightness = 1.0f;
        } else if (newBrightness < 0.0f) {
            newBrightness = 0.0f;
        }
        if (activity != null) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = newBrightness;
            activity.getWindow().setAttributes(lp);
        }

        int indexPercent = (int) (newBrightness * 100);
        if (onSlideChangedListener != null) {
            onSlideChangedListener.onPercentChanged(slideType, indexPercent);
        }
    }

    private void onProgressSlide(float percent) {
        slideType = SLIDE_TYPE_PROGRESS;
        if (!scrolling) {
            if (onSlideChangedListener != null) {
                onSlideChangedListener.onStartSlide(slideType);
            }
            scrolling = true;
            return;
        }
        if (onSlideChangedListener != null) {
            onSlideChangedListener.onPercentChanged(slideType, (int) (percent * 100));
        }
    }

    private void onVolumeSlide(float percent) {
        slideType = SLIDE_TYPE_VOLUME;
        if (!scrolling) {
            oldVolume = audioManager != null ? audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : 0;
            oldVolume = oldVolume < 0 ? 0 : oldVolume;
            if (onSlideChangedListener != null) {
                onSlideChangedListener.onStartSlide(SLIDE_TYPE_VOLUME);
            }
            scrolling = true;
            return;
        }
        int index = (int) (percent * maxVolume) + oldVolume;
        if (index > maxVolume) {
            index = maxVolume;
        } else if (index < 0) {
            index = 0;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        int indexPercent = (int) (index * 1.0 / maxVolume * 100);
        if (onSlideChangedListener != null) {
            onSlideChangedListener.onPercentChanged(SLIDE_TYPE_VOLUME, indexPercent);
        }
    }

}
