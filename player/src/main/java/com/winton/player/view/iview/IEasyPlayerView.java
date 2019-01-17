package com.winton.player.view.iview;


import com.winton.player.IPlayer;
import com.winton.player.view.listener.IEasyPlayerViewListener;

/**
 * @author: winton
 * @time: 2019/1/16 8:27 PM
 * @desc: EasyPlayerView的方法
 */
public interface IEasyPlayerView extends ISPlayerView {


    /**
     * 设置播控回调
     * @param listener
     */
    void setListener(IEasyPlayerViewListener listener);

    /**
     * 装载播放器
     * @param player
     */
    void setupPlayer(IPlayer player);


}
