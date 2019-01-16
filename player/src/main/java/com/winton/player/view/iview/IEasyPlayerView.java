package com.winton.player.view.iview;

import com.winton.player.IPlayer;

/**
 * @author: winton
 * @time: 2019/1/16 8:27 PM
 * @desc: EasyPlayerView的方法
 */
public interface IEasyPlayerView extends ISPlayerView {
    /**
     * 设置已播放时长
     * @param text
     */
    void setCurrentTime(String text);

    /**
     * 设置总时长
     * @param text
     */
    void setTotalTime(String text);

    /**
     * 显示控制UI
     * @param show
     */
    void showControlUi(boolean show);

    /**
     * 设置播放进度
     * @param progress
     */
    void setProgress(int progress);

    /**
     * 注册播放器
     * @param player
     */
    void registerPlayer(IPlayer player);

    /**
     * 释放View持有的资源
     */
    void release();

}
