package com.winton.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.winton.player.IPlayer.STATUS_END;
import static com.winton.player.IPlayer.STATUS_PREPARED;
import static com.winton.player.IPlayer.STATUS_COMPLETED;
import static com.winton.player.IPlayer.STATUS_ERROR;
import static com.winton.player.IPlayer.STATUS_IDLE;
import static com.winton.player.IPlayer.STATUS_INITIALIZED;
import static com.winton.player.IPlayer.STATUS_PAUSED;
import static com.winton.player.IPlayer.STATUS_STARTED;
import static com.winton.player.IPlayer.STATUS_PREPARING;
import static com.winton.player.IPlayer.STATUS_STOPPED;

/**
 * @author: winton
 * @time: 2019/1/13 10:57 AM
 * @desc: 播放器状态
 */
@IntDef({STATUS_IDLE, STATUS_INITIALIZED, STATUS_PREPARING, STATUS_PREPARED, STATUS_STARTED,
        STATUS_PAUSED, STATUS_COMPLETED, STATUS_STOPPED, STATUS_ERROR, STATUS_END})
@Retention(RetentionPolicy.SOURCE)
public @interface Status {


}
