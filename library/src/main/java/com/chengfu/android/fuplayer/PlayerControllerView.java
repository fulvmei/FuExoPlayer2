package com.chengfu.android.fuplayer;

public interface PlayerControllerView extends PlayerView {

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input.
     *
     * @return The duration in milliseconds. A non-positive value indicates that the controls will
     * remain visible indefinitely.
     */
    int getShowTimeoutMs();

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     *                      to remain visible indefinitely.
     */
    void setShowTimeoutMs(int showTimeoutMs);

    /**
     * Returns whether the PlayerController is currently showing.
     */
    boolean isShowing();

    /**
     * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
     * be automatically hidden after this duration of time has elapsed without user input.
     */
    void show();

    /**
     * Hides the controller.
     */
    void hide();

}
