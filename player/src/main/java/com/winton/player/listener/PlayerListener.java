package com.winton.player.listener;

import com.winton.player.IPlayer;

public interface PlayerListener {

    void onPrepared(IPlayer player);

    void onCompletion(IPlayer player);

    boolean onError(IPlayer player, int i, int i1);

    void onSeekComplete(IPlayer player);

    boolean onInfo(IPlayer player, int i, int i1);

    void onBufferingUpdate(IPlayer player, int i);

    void onVideoSizeChanged(IPlayer player, int i, int i1, int i2, int i3);
}
