package com.winton.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.winton.player.IPlayer.PLAYER__AndroidMediaPlayer;
import static com.winton.player.IPlayer.PLAYER__IjkExoMediaPlayer;
import static com.winton.player.IPlayer.PLAYER__IjkMediaPlayer;


/**
 * @author: winton
 * @time: 2019/1/13 11:04 AM
 * @desc: 支持的播放器类型 接口
 */
@IntDef({PLAYER__IjkMediaPlayer,PLAYER__AndroidMediaPlayer,PLAYER__IjkExoMediaPlayer})
@Retention(RetentionPolicy.SOURCE)
public @interface Type {
}
