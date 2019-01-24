package com.winton.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.winton.player.IPlayer.PLAYER__MEDIA;
import static com.winton.player.IPlayer.PLAYER__EXO;
import static com.winton.player.IPlayer.PLAYER__IJK;


/**
 * @author: winton
 * @time: 2019/1/13 11:04 AM
 * @desc: 支持的播放器类型 接口
 */
@IntDef({PLAYER__IJK, PLAYER__MEDIA, PLAYER__EXO})
@Retention(RetentionPolicy.SOURCE)
public @interface Type {
}
