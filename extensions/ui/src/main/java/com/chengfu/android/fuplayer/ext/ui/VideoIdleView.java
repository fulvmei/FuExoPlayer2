package com.chengfu.android.fuplayer.ext.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chengfu.android.fuplayer.BaseStateView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

public class VideoIdleView extends BaseStateView {

    protected final ComponentListener componentListener;

    protected OnPlayerClickListener onPlayerClickListener;

    private boolean disable;

    public interface OnPlayerClickListener {
        void onPlayClick(View v);
    }


    public VideoIdleView(@NonNull Context context) {
        this(context, null);
    }

    public VideoIdleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public VideoIdleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = onCreateView(LayoutInflater.from(context), this);

        if (view != null) {
            addView(view);
        }

        componentListener = new ComponentListener();

        updateVisibility();

        View play = findViewById(R.id.play);
        if (play != null) {
            play.setOnClickListener(v -> {
                if (onPlayerClickListener != null) {
                    onPlayerClickListener.onPlayClick(v);
                }
            });
        }
    }

    @Override
    protected void onFullScreenChanged(boolean fullScreen) {

    }


    public void setDisable(boolean disable) {
        if (this.disable == disable) {
            return;
        }
        this.disable = disable;
        updateVisibility();
    }

    public boolean isDisable() {
        return disable;
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.fpu_view_video_state_idel, parent, false);
    }

    protected void updateVisibility() {
        if (isInShowState()) {
            show();
        } else {
            hide();
        }
    }

    protected boolean isInShowState() {
        if (disable) {
            return false;
        }
        if (player == null) {
            return true;
        }
        if (player.getPlaybackState() == Player.STATE_IDLE && player.getPlaybackError() == null) {
            return true;
        }
        return false;
    }

    public OnPlayerClickListener getOnPlayerClickListener() {
        return onPlayerClickListener;
    }

    public void setOnPlayerClickListener(OnPlayerClickListener onPlayerClickListener) {
        this.onPlayerClickListener = onPlayerClickListener;
    }

    @Override
    protected void onAttachedToPlayer(@NonNull ExoPlayer player) {
        updateVisibility();

        player.addListener(componentListener);
    }

    @Override
    protected void onDetachedFromPlayer(@NonNull ExoPlayer player) {
        updateVisibility();
        player.removeListener(componentListener);
    }


    private final class ComponentListener implements Player.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updateVisibility();
        }
    }
}
