package com.winton.player;

import android.view.Surface;

import com.winton.player.listener.VideoPlayerListener;

import java.util.Map;

/**
 * @author: winton
 * @time: 2019/1/9 8:37 PM
 * @desc: 播放器需要对外提供的方法
 */
public interface IPlayer {
    /**
     * 支持的播放器类型
     */
    int PLAYER__MEDIA = 1;
    int PLAYER__IJK = 2;
    int PLAYER__EXO = 3;
    /**
     * 播放器状态
     */
    int STATUS_ERROR = -1;
    int STATUS_IDLE = 0;
    int STATUS_PREPARING = 1;
    int STATE_PREPARED = 2;
    int STATUS_PLAYING = 3;
    int STATUS_PAUSE = 4;
    int STATUS_COMPLETE = 5;


    /**
     * 设置播放器监听器
     * @param listener
     */
    void addPlayerListener(VideoPlayerListener listener);

    /**
     * 移除播放器监听
     * @param listener
     */
    void removePlayerListener(VideoPlayerListener listener);

    /**
     * 开始播放
     * @param url
     */
    void url(String url);

    /**
     * 设置播放地址
     * @param url
     * @param needCache
     */
    void url(String url,boolean needCache);

    /**
     * 设置播放地址
     * @param url
     * @param head
     */
    void url(String url , Map<String,String> head);

    /**
     * 设置播放地址
     * @param url
     * @param head
     * @param needCache
     */
    void url(String url,Map<String,String> head,boolean needCache);

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
     * @param speed
     */
    void setSpeed(float speed);

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
     * @param needMute
     */
    void setNeedMute(boolean needMute);

    /**
     * 获取播放器当前当播放状态
     * @return
     */
    int getStatus();
}
