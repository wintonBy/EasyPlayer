package com.winton.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.winton.player.IPlayer.STATE_PREPARED;
import static com.winton.player.IPlayer.STATUS_COMPLETE;
import static com.winton.player.IPlayer.STATUS_ERROR;
import static com.winton.player.IPlayer.STATUS_IDLE;
import static com.winton.player.IPlayer.STATUS_PAUSE;
import static com.winton.player.IPlayer.STATUS_PLAYING;
import static com.winton.player.IPlayer.STATUS_PREPARING;

/**
 * @author: winton
 * @time: 2019/1/13 10:57 AM
 * @desc: 播放器状态
 */
@IntDef({STATUS_IDLE,STATUS_PREPARING, STATUS_PLAYING,STATUS_PAUSE,STATUS_COMPLETE,STATUS_ERROR,STATE_PREPARED})
@Retention(RetentionPolicy.SOURCE)
public @interface Status {


}
