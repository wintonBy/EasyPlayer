package com.winton.player;

import android.view.Surface;

import com.winton.player.listener.PlayerListener;
import com.winton.player.model.VideoData;

import java.util.Map;

import tv.danmaku.ijk.media.player.MediaInfo;

/**
 * @author: winton
 * @time: 2019/1/9 8:37 PM
 * @desc: 播放器需要对外提供的方法
 */
public interface IPlayer {
    /**
     * 支持的播放器类型
     */
    int PLAYER__MEDIA   = 1;
    int PLAYER__IJK     = 2;
    int PLAYER__EXO     = 3;
    /**
     * 播放器状态
     */
    int STATUS_ERROR        = -1;
    int STATUS_IDLE         = 0;
    int STATUS_INITIALIZED  = 1;
    int STATUS_PREPARING    = 2;
    int STATUS_PREPARED     = 3;
    int STATUS_STARTED      = 4;
    int STATUS_PAUSED       = 5;
    int STATUS_COMPLETED    = 6;
    int STATUS_STOPPED      = 7;
    int STATUS_END          = 8;


    /**
     * 设置播放器监听器
     * @param listener
     */
    void setPlayerListener(PlayerListener listener);

    /**
     * 设置video数据
     * @param data
     */
    void videoData(VideoData data);

    /**
     * 开始播放
     */
    void start();
    /**
     * 暂停播放
     */
    void pause();

    /**
     * 设置音量
     * @param v
     * @param v1
     */
    void setVolume(float v, float v1);

    /**
     * 获取当前进度
     * @return
     */
    long getCurrentPosition();

    /**
     * 获取视频总长度
     * @return
     */
    long getDuration();

    /**
     * 快进快退
     * @param index
     */
    void seekTo(long index);

    /**
     * 设置播放速度
     * @param mSpeed
     */
    void setSpeed(float mSpeed);

    /**
     * 设置单曲循环
     * @param loop
     */
    void setLoop(boolean loop);

    /**
     * 释放播放器
     */
    void release();

    /**
     * 设置显示区域
     * @param surface
     */
    void setDisplay(Surface surface);

    /**
     * 设置静音
     * @param mMute
     */
    void setMute(boolean mMute);

    /**
     * 获取播放器当前当播放状态
     * @return
     */
    int getStatus();

    boolean isPlaying();

    void setScreenOnWhilePlaying(boolean screenOn);

    /**
     * 获取mediainfo
     * @return
     */
    MediaInfo getMediaInfo();

    int getVideoWidth();

    int getVideoHeight();
}
