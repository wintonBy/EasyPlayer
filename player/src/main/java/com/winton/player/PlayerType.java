package com.winton.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: winton
 * @time: 2019/1/10 3:54 PM
 * @desc: 播放器类型
 */
public class PlayerType {

    public static final int PLAYER__AndroidMediaPlayer = 1;
    public static final int PLAYER__IjkMediaPlayer = 2;
    public static final int PLAYER__IjkExoMediaPlayer = 3;

    @IntDef({PLAYER__IjkMediaPlayer,PLAYER__AndroidMediaPlayer,PLAYER__IjkExoMediaPlayer})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {

    }

}
