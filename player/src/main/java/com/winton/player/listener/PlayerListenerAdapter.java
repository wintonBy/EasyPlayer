package com.winton.player.listener;

import com.winton.player.IPlayer;

/**
 * @author: winton
 * @time: 2019/1/10 6:58 PM
 * @desc: 播放器监听
 */
public class PlayerListenerAdapter implements PlayerListener {

    @Override
    public void onPrepared(IPlayer player) {

    }

    @Override
    public void onCompletion(IPlayer player) {

    }

    @Override
    public boolean onError(IPlayer player, int i, int i1) {
        return false;
    }

    @Override
    public void onSeekComplete(IPlayer player) {

    }

    @Override
    public boolean onInfo(IPlayer player, int i, int i1) {
        return false;
    }

    @Override
    public void onBufferingUpdate(IPlayer player, int i) {

    }

    @Override
    public void onVideoSizeChanged(IPlayer player, int i, int i1, int i2, int i3) {

    }
}
