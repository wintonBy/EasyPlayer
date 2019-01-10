package com.winton.player;

import com.winton.player.listener.VideoPlayerListener;

/**
 * @author: winton
 * @time: 2019/1/9 8:37 PM
 * @desc: 播放器需要对外提供的方法
 */
public interface IPlayer {

    /**
     * 设置播放器监听器
     * @param listener
     */
    void setPlayerListener(VideoPlayerListener listener);

}
