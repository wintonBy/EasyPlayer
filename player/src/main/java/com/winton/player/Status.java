package com.winton.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.winton.player.IPlayer.STATUS_INIT;
import static com.winton.player.IPlayer.STATUS_PAUSE;
import static com.winton.player.IPlayer.STATUS_STARTING;
import static com.winton.player.IPlayer.STATUS_STOP;


/**
 * @author: winton
 * @time: 2019/1/13 10:57 AM
 * @desc: 播放器状态
 */
@IntDef({STATUS_INIT,STATUS_STARTING, STATUS_PAUSE,STATUS_STOP})
@Retention(RetentionPolicy.SOURCE)
public @interface Status {


}
