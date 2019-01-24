package com.winton.player.view;

import android.view.Surface;

import com.winton.player.IPlayer;
import com.winton.player.listener.VideoPlayerListener;

import java.util.Map;

/**
 * @author: winton
 * @time: 2019/1/17 4:05 PM
 * @desc: 播放器代理
 */
public class PlayerProxy implements IPlayer {

    private IPlayer mPlayer;

    public PlayerProxy(IPlayer player){
        mPlayer = player;
    }

    @Override
    public void addPlayerListener(VideoPlayerListener listener) {
        mPlayer.addPlayerListener(listener);
    }

    @Override
    public void removePlayerListener(VideoPlayerListener listener) {
        mPlayer.removePlayerListener(listener);
    }

    @Override
    public void url(String url) {
        mPlayer.url(url);
    }

    @Override
    public void url(String url, boolean needCache) {
        mPlayer.url(url,needCache);
    }

    @Override
    public void url(String url, Map<String, String> head) {
        mPlayer.url(url,head);
    }

    @Override
    public void url(String url, Map<String, String> head, boolean needCache) {
        mPlayer.url(url,head,needCache);

    }

    @Override
    public void start() {
        mPlayer.start();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void setVolume(float v, float v1) {
        mPlayer.setVolume(v,v1);
    }

    @Override
    public long getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public void seekTo(long index) {
        mPlayer.seekTo(index);
    }

    @Override
    public void setSpeed(float speed) {
        mPlayer.setSpeed(speed);
    }

    @Override
    public void setLoop(boolean loop) {
        mPlayer.setLoop(loop);
    }

    @Override
    public void release() {
        mPlayer.release();
    }

    @Override
    public void setDisplay(Surface surface) {
        mPlayer.setDisplay(surface);
    }

    @Override
    public void setNeedMute(boolean needMute) {
        mPlayer.setNeedMute(needMute);
    }

    @Override
    public int getStatus() {
        return mPlayer.getStatus();
    }
}
