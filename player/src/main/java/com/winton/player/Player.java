package com.winton.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.winton.player.cache.CacheHelper;
import com.winton.player.listener.VideoPlayerListener;
import com.winton.player.model.VideoModel;
import com.winton.player.utils.Debuger;

import java.io.File;
import java.util.Map;

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
class Player implements IPlayer,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnSeekCompleteListener,
        CacheListener {

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
    /**
     * 播放超时
     */
    private int timeOut = 10 * 1000;
    /**
     * 是否需要监听播放超时
     */
    private boolean needTimeOutOther = true;
    /**
     * 上下文环境
     */
    private Context mContext;
    /**
     * 是否需要静音
     */
    private boolean needMute;

    /**
     * 播放速度
     */
    private float speed = 1.0f;

    /**
     * 缓冲比例
     */
    private int bufferPoint;
    /**
     * 是否需要缓存
     */
    private boolean needCache  = true;
    /**
     * 视频的长宽
     */
    private int currentVideoWidth;
    private int currentVideoHeight;
    /**
     * 缓存代理
     */

    private static final int HANDLER_PREPARE = 0;

    private static final int HANDLER_SET_DISPLAY = 1;

    private static final int HANDLER_RELEASE = 2;
    /**
     * 外部超时错误码
     */
    private static final int BUFFER_TIME_OUT_ERROR = -192;

    /**
     * 播放器类型
     */
    private final int playerType ;
    /**
     * 播放器状态
     */
    @Status
    private int status;

    private String currentUrl = "";

    private class PlayerHandler extends Handler{
        PlayerHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_PREPARE:
                    initVideo(msg);
                    break;
                case HANDLER_SET_DISPLAY:
                    Surface holder = (Surface)msg.obj;
                    initDisplay(holder);
                    break;
                case HANDLER_RELEASE:
                    if(mMediaPlayer != null){
                        mMediaPlayer.release();
                    }
                    if(getProxy() != null && needCache){
                        getProxy().unregisterCacheListener(Player.this,currentUrl);
                    }
                    bufferPoint = 0;
                    cancelTimeOutBuffer();
                    break;
                default:break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 设置显示区域
     * @param holder
     */
    private void initDisplay(Surface holder) {
        if(mMediaPlayer != null && holder != null&&holder.isValid()){
            mMediaPlayer.setSurface(holder);
        }
    }

    /**
     * 初始化视频
     */
    private void initVideo(Message msg) {
        try{
            currentVideoWidth = 0;
            currentVideoHeight = 0;
            VideoModel model = (VideoModel) msg.obj;
            currentUrl = model.getUrl();
            mMediaPlayer.setDataSource(mContext,Uri.parse(model.getUrl()),model.getMapHeadData());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
            Log.d("winton","url ready 装载");
        }catch (Exception e){
            Debuger.printfError(TAG,e);
        }
    }

    /**
     * 构造播放器实例
     * @param playerType
     */
    private Player(Context context,int playerType){
        this.playerType = playerType;
        this.mContext = context;
        initPlayer(context,playerType);
        init();
        initListener();
    }

    /**
     * 初始化播放器
     * @param context
     * @param playerType
     */
    private void initPlayer(Context context,int playerType){
        switch (playerType){
            case PLAYER__IjkExoMediaPlayer:
                mMediaPlayer = new IjkExoMediaPlayer(context);
                break;
            case PLAYER__AndroidMediaPlayer:
                mMediaPlayer = new AndroidMediaPlayer();
                break;
            case PLAYER__IjkMediaPlayer:
                IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
                ijkMediaPlayer.setSpeed(speed);
                initIjkPlayer(ijkMediaPlayer);
                mMediaPlayer = ijkMediaPlayer;
                break;
            default:break;
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer = new TextureMediaPlayer(mMediaPlayer);
        status = STATUS_INIT;
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
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, final int percent) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(videoPlayerListener != null){
                    if(percent > bufferPoint){
                        videoPlayerListener.onBufferingUpdate(percent);
                    }else {
                        videoPlayerListener.onBufferingUpdate(bufferPoint);
                    }
                }
            }
        });
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(needTimeOutOther){
                    if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                        startTimeOutBuffer();
                    }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                        cancelTimeOutBuffer();
                    }
                }
                if(videoPlayerListener != null){
                    videoPlayerListener.onInfo(what,extra);
                }
            }
        });
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                if(videoPlayerListener != null){
                    videoPlayerListener.onAutoCompletion();
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                if(videoPlayerListener != null){
                    videoPlayerListener.onError(what,extra);
                }
            }
        });
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
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
                cancelTimeOutBuffer();
                if(videoPlayerListener != null){
                    videoPlayerListener.onSeekComplete();
                }
            }
        });
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, final int width, final int height, final int sarNum, final int sarDen) {
        currentVideoWidth = iMediaPlayer.getVideoWidth();
        currentVideoHeight = iMediaPlayer.getVideoHeight();
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
     * 启动定时器进行 缓存操作
     */
    private void startTimeOutBuffer() {
        // 启动定时
        Debuger.printfError("startTimeOutBuffer");
        mMainThreadHandler.postDelayed(timeoutRunnable, timeOut);

    }

    /**
     * 取消定时器进行 缓存操作
     */
    private void cancelTimeOutBuffer() {
        Debuger.printfError("cancelTimeOutBuffer");
        // 取消定时
        if (needTimeOutOther){
            mMainThreadHandler.removeCallbacks(timeoutRunnable);
        }
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Debuger.printfError("request time out");
            if(videoPlayerListener != null){
                videoPlayerListener.onError(BUFFER_TIME_OUT_ERROR,BUFFER_TIME_OUT_ERROR);
            }
        }
    };

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        bufferPoint = percentsAvailable;
    }

    @Override
    public void setPlayerListener(VideoPlayerListener listener) {
        this.videoPlayerListener = listener;
    }

    private HttpProxyCacheServer getProxy(){
        return CacheHelper.getInstance(mContext).getProxy();
    }

    @Override
    public void url(String url){
        url(url,null);
    }

    @Override
    public void url(String url, boolean needCache) {
        url(url,null,needCache);
    }

    @Override
    public void url(String url, Map<String, String> head){
        url(url,head,true);
    }

    @Override
    public void url(String url, Map<String, String> head, boolean needCache) {
        VideoModel model = new VideoModel();
        this.needCache = needCache;
        if(needCache){
            url = getProxy().getProxyUrl(url,false);
            getProxy().registerCacheListener(this,url);
        }
        currentUrl = url;
        model.setUrl(url);
        if(head != null){
            model.setMapHeadData(head);
        }
        Message msg = mWorkHandler.obtainMessage(HANDLER_PREPARE);
        msg.obj = model;
        mWorkHandler.sendMessage(msg);
        if(needTimeOutOther){
            startTimeOutBuffer();
        }
    }

    @Override
    public void start() {
        if(mMediaPlayer != null && status != STATUS_RELEASE){
            status = STATUS_STARTING;
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if(mMediaPlayer != null && status == STATUS_STARTING){
            status = STATUS_PAUSE;
            mMediaPlayer.pause();
        }
    }

    @Override
    public void resume() {
        if(mMediaPlayer != null && status == STATUS_PAUSE){
            status = STATUS_STARTING;
            mMediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        if(mMediaPlayer != null){
            status = STATUS_STOP;
            mMediaPlayer.stop();
        }
    }

    @Override
    public void setVolume(float v, float v1) {
        if(mMediaPlayer != null){
            mMediaPlayer.setVolume(v,v1);
        }
    }

    @Override
    public long getCurrentPosition() {
        if(mMediaPlayer != null){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return mMediaPlayer == null? 0: mMediaPlayer.getDuration();
    }

    @Override
    public void seekTo(long index) {
        if(index <0){
            index = 0;
        }
        if(index > getDuration()){
            index = getDuration();
        }
        if(mMediaPlayer != null){
            mMediaPlayer.seekTo(index);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if(playerType == PLAYER__IjkMediaPlayer && mMediaPlayer != null){
            ((IjkMediaPlayer)mMediaPlayer).setSpeed(speed);
        }
    }

    @Override
    public void setLoop(boolean loop) {
        if(mMediaPlayer != null){
            mMediaPlayer.setLooping(loop);
        }
    }

    @Override
    public void release() {
        mWorkHandler.sendEmptyMessage(HANDLER_RELEASE);
    }

    @Override
    public void setDisplay(Surface holder) {
        Message msg = mWorkHandler.obtainMessage();
        msg.what = HANDLER_SET_DISPLAY;
        msg.obj = holder;
        mWorkHandler.sendMessage(msg);
    }

    @Override
    public void setNeedMute(boolean needMute) {
        this.needMute = needMute;
        if(mMediaPlayer != null){
            if(needMute){
                mMediaPlayer.setVolume(0,0);
            }else {
                mMediaPlayer.setVolume(1,1);
            }
        }
    }

    /**
     * 获取播放器的示例
     * @param playerType
     * @return
     */
    protected static Player newInstance(@NonNull Context context, @Type int playerType){
        return new Player(context,playerType);
    }

}
