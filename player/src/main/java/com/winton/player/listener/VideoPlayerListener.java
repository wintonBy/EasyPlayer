package com.winton.player.listener;

/**
 * @author: winton
 * @time: 2019/1/10 6:58 PM
 * @desc: 播放器监听
 */
public interface VideoPlayerListener {

    void onPrepared();

    void onAutoCompletion();

    void onCompletion();

    void onBufferingUpdate(int percent);

    void onSeekComplete();

    void onError(int what, int extra);

    void onInfo(int what, int extra);

    void onVideoSizeChanged(int width, int height, int sarNun, int sarDen);

    void onBackFullscreen();

    void onVideoPause();

    void onVideoResume();
}
