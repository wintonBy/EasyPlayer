package com.winton.player;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.winton.player.listener.VideoPlayerListener;
import com.winton.player.utils.Debuger;


import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;

/**
 * @author: winton
 * @time: 2019/1/9 8:45 PM
 * @desc: 播放器实现类
 */
public class Player implements IPlayer,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnSeekCompleteListener{

    private final String TAG = getClass().getSimpleName();

    /**
     * MediaPlayer
     */
    private IMediaPlayer mMediaPlayer;
    /**
     * 播放器监听
     */
    private VideoPlayerListener videoPlayerListener;
    /**
     * 主线程Handler
     */
    private Handler mMainThreadHandler;
    /**
     * 工作线程
     */
    private PlayerHandler mWorkHandler;
    private HandlerThread mWorkThread;

    private static final int HANDLER_PREPARE = 0;

    private static final int HANDLER_SETDISPLAY = 1;

    private static final int HANDLER_RELEASE = 2;

    /**
     * 播放器类型
     */
    final int playerType ;


    private class PlayerHandler extends Handler{
        PlayerHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_PREPARE:
                    break;
                case HANDLER_SETDISPLAY:
                    break;
                case HANDLER_RELEASE:
                    break;
                default:break;
            }

            super.handleMessage(msg);
        }
    }

    /**
     * 构造播放器实例
     * @param playerType
     */
    private Player(Context context,int playerType){

        this.playerType = playerType;
        switch (playerType){
            case com.winton.player.PlayerType.PLAYER__IjkExoMediaPlayer:
                mMediaPlayer = new IjkExoMediaPlayer(context);
                break;
            case com.winton.player.PlayerType.PLAYER__AndroidMediaPlayer:
                mMediaPlayer = new AndroidMediaPlayer();
                break;
            case com.winton.player.PlayerType.PLAYER__IjkMediaPlayer:
                IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
                initIjkPlayer(ijkMediaPlayer);
                mMediaPlayer = ijkMediaPlayer;
                break;
                default:break;
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer = new TextureMediaPlayer(mMediaPlayer);
        init();
        initListener();
    }

    private void init(){
        mWorkThread = new HandlerThread("player_work_thread");
        mWorkThread.start();
        mWorkHandler = new PlayerHandler(mWorkThread.getLooper());
        mMainThreadHandler = new Handler();
    }

    /**
     * 初始化IJkPlayer
     * @param ijkMediaPlayer
     */
    private void initIjkPlayer(IjkMediaPlayer ijkMediaPlayer) {
        try{
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 0);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", "48000");
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");
        }catch (Exception e){
            Debuger.printfError(TAG,e);
        }

    }

    /**
     * 初始化监听器
     */
    private void initListener(){
        if(mMediaPlayer != null){
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnPreparedListener(this);
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(videoPlayerListener != null){
                    videoPlayerListener.onPrepared();
                }
            }
        });
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(videoPlayerListener != null){
                    videoPlayerListener.onSeekComplete();
                }
            }
        });
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, final int width, final int height, final int sarNum, final int sarDen) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(videoPlayerListener != null){
                    videoPlayerListener.onVideoSizeChanged(width,height,sarNum,sarDen);
                }
            }
        });
    }

    /**
     * 获取播放器的示例
     * @param playerType
     * @return
     */
    public static Player newInstance(@NonNull Context context, @PlayerType.Type int playerType){
        return new Player(context,playerType);
    }
    @Override
    public void setPlayerListener(VideoPlayerListener listener) {
        this.videoPlayerListener = listener;
    }
}
