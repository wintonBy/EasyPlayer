package com.winton.player.view.listener;

import android.view.View;

/**
 * @author: winton
 * @time: 2019/1/17 2:03 PM
 * @desc: 播控回调
 */
public interface IEasyPlayerViewListener {
    /**
     * 点击播放
     * @param v
     */
    void onClickPlay(View v);

    /**
     * 点击全屏按钮
     * @param v
     */
    void onClickFullScreen(View v);

    /**
     * 点击播放器空白处
     * @param v
     */
    void onClickPlayerView(View v);


}
