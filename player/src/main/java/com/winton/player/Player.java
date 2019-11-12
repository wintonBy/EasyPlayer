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
import android.view.Surface;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.winton.player.cache.CacheHelper;
import com.winton.player.listener.PlayerListener;
import com.winton.player.model.VideoData;
import com.winton.player.model.VideoFile;
import com.winton.player.model.VideoNetwork;
import com.winton.player.model.VideoUri;
import com.winton.player.utils.Debuger;

import java.io.File;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;
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

    private final String TAG = "Player";

    /**
     * MediaPlayer
     */
    private IMediaPlayer mMediaPlayer;
    /**
     * 播放器监听
     */
    private PlayerListener mPlayerListener;
    /**
     * 主线程Handler
     */
    private Handler mMainThreadHandler;
    /**
     * 工作线程
     */
    private PlayerHandler mWorkHandler;
    /**
     * 播放超时
     */
    private int mTimeout = 10 * 1000;
    private boolean mNeedTimeout = false;


    /**
     * 上下文环境
     */
    private Context mContext;
    /**
     * 是否需要静音
     */
    private boolean mMute;
    /**
     * 播放速度
     */
    private float mSpeed = 1.0f;

    /**
     * 缓冲比例
     */
    private int bufferPoint;
    /**
     * 是否需要缓存
     */
    private boolean needCache  = false;
    /**
     * 视频的长宽
     */
    private int currentVideoWidth;
    private int currentVideoHeight;
    /**
     * 缓存代理
     */

    private static final int HANDLER_PREPARE            = 0;
    private static final int HANDLER_SET_DISPLAY        = 1;
    private static final int HANDLER_RELEASE            = 2;
    private static final int HANDLER_PLAY               = 3;
    private static final int HANDLER_PAUSE              = 4;
    /**
     * 外部超时错误码
     */
    private static final int BUFFER_TIME_OUT_ERROR = -192;

    /**
     * 播放器类型
     */
    private final int mPlayerType;
    /**
     * 播放器状态
     */
    @Status
    private int mStatus = STATUS_IDLE;

    private String currentUrl = "";

    private class PlayerHandler extends Handler{
        PlayerHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_PREPARE:
                    mStatus = STATUS_PREPARING;
                    initVideo(msg);
                    break;
                case HANDLER_SET_DISPLAY:
                    Surface holder = (Surface)msg.obj;
                    if(mMediaPlayer != null && holder != null&&holder.isValid()){
                        mMediaPlayer.setSurface(holder);
                    }
                    break;
                case HANDLER_RELEASE:
                    mStatus = STATUS_END;
                    doRelease();
                    break;
                case HANDLER_PLAY:
                    mMediaPlayer.start();
                    break;
                case HANDLER_PAUSE :
                    mMediaPlayer.pause();
                    break;
                default:break;
            }
            super.handleMessage(msg);
        }
    }

    private void doRelease(){
        if(mMediaPlayer != null){
            mMediaPlayer.release();
        }
        if(getProxy() != null && needCache){
            getProxy().unregisterCacheListener(Player.this, currentUrl);
        }
        bufferPoint = 0;
        cancelTimeOutBuffer();
    }
    /**
     * 初始化视频
     */
    private void initVideo(Message msg) {
        try{
            //make sure player resource available
            doRelease();
            mMediaPlayer = buildPlayer(mContext, mPlayerType);
            initListener();
            currentVideoWidth = 0;
            currentVideoHeight = 0;
            VideoData model = (VideoData) msg.obj;
            if (model instanceof VideoNetwork) {
                mMediaPlayer.setDataSource(mContext, Uri.parse(((VideoNetwork)model).getUrl()), ((VideoNetwork)model).getHeaders());
            } else if (model instanceof VideoFile) {
                mMediaPlayer.setDataSource(((VideoFile)model).getData());
            } else if ( model instanceof VideoUri) {
                mMediaPlayer.setDataSource(((VideoUri)model).getContext(), ((VideoUri)model).getUri());
            } else {
                throw new IllegalArgumentException("unsupported video data");
            }

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        }catch (Exception e){
            Debuger.printfError(TAG,e);
        }
    }

    /**
     * 构造播放器实例
     * @param playerType
     */
    private Player(Context context, int playerType) {
        this.mPlayerType = playerType;
        this.mContext = context;
        HandlerThread mWorkThread = new HandlerThread("player_work_thread");
        mWorkThread.start();
        mWorkHandler = new PlayerHandler(mWorkThread.getLooper());
        mMainThreadHandler = new Handler();
    }
    /**
     * 初始化播放器
     * @param context
     * @param playerType
     */
    private IMediaPlayer buildPlayer(Context context, int playerType){
        IMediaPlayer mediaPlayer = null;
        switch (playerType){
            case PLAYER__EXO:
                mediaPlayer = new IjkExoMediaPlayer(context);
                break;
            case PLAYER__MEDIA:
                mediaPlayer = new AndroidMediaPlayer();
                break;
            case PLAYER__IJK:
                 IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
                ijkMediaPlayer.setSpeed(mSpeed);
                initIjkPlayer(ijkMediaPlayer);
                mediaPlayer = ijkMediaPlayer;
                break;
            default:break;
        }
        if(mediaPlayer != null){
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer = new TextureMediaPlayer(mediaPlayer);
        }
        return mediaPlayer;
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
            //跳帧处理,放CPU处理较慢时，进行跳帧处理，保证播放流程，画面和声音同步
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
            //播放前的探测Size
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", 1024*10);
            //探测时间
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
            //最大探测时间
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzemaxduration",100L);
            //重连次数
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect",10);
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
                if (mPlayerListener != null) {
                    if (percent > bufferPoint) {
                        mPlayerListener.onBufferingUpdate(Player.this, percent);
                    }else {
                        mPlayerListener.onBufferingUpdate(Player.this, bufferPoint);
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
                if(mNeedTimeout){
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        startTimeOutBuffer();
                    } else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        cancelTimeOutBuffer();
                    }
                }
                if (mPlayerListener != null) {
                    mPlayerListener.onInfo(Player.this, what, extra);
                }
            }
        });
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mStatus = STATUS_COMPLETED;
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                if(mPlayerListener != null){
                    mPlayerListener.onCompletion(Player.this);
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        mStatus = STATUS_ERROR;
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                if (mPlayerListener != null) {
                    mPlayerListener.onError(Player.this, what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mStatus = STATUS_PREPARED;
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                if(mPlayerListener != null){
                    mPlayerListener.onPrepared(Player.this);
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
                if (mPlayerListener != null) {
                    mPlayerListener.onSeekComplete(Player.this);
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
                if (mPlayerListener != null) {
                    mPlayerListener.onVideoSizeChanged(Player.this, width, height, sarNum, sarDen);
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
        mMainThreadHandler.postDelayed(timeoutRunnable, mTimeout);

    }

    /**
     * 取消定时器进行 缓存操作
     */
    private void cancelTimeOutBuffer() {
        Debuger.printfError("cancelTimeOutBuffer");
        // 取消定时
        mMainThreadHandler.removeCallbacks(timeoutRunnable);
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Debuger.printfError("request time out");
            if (mPlayerListener != null){
                mPlayerListener.onError(Player.this, BUFFER_TIME_OUT_ERROR,BUFFER_TIME_OUT_ERROR);
            }
        }
    };

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        bufferPoint = percentsAvailable;
    }

    @Override
    public void setPlayerListener(PlayerListener listener) {
        mPlayerListener = listener;
    }


    private HttpProxyCacheServer getProxy(){
        return CacheHelper.getInstance(mContext).getProxy();
    }

    @Override
    public void videoData(VideoData data) {
        Message msg = mWorkHandler.obtainMessage(HANDLER_PREPARE);
        msg.obj = data;
        mWorkHandler.sendMessage(msg);
    }

    @Override
    public void start() {
        if (mStatus == STATUS_PREPARED
                || mStatus == STATUS_PAUSED
                || mStatus == STATUS_COMPLETED) {
            mStatus = STATUS_STARTED;
            mWorkHandler.sendEmptyMessageDelayed(HANDLER_PLAY,200);
        } else {
            Debuger.printfWarning("current status is unsupported this method");
        }
    }

    @Override
    public void pause() {
        if (mStatus == STATUS_STARTED) {
            mStatus = STATUS_PAUSED;
            mWorkHandler.sendEmptyMessage(HANDLER_PAUSE);
        } else {
            Debuger.printfWarning("current status is unsupported this method");
        }

    }
    @Override
    public void setVolume(float v, float v1) {
        if (mStatus != STATUS_ERROR && mStatus != STATUS_END) {
            mMediaPlayer.setVolume(v, v1);
        } else {
            Debuger.printfWarning("current status is unsupported this method");
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mStatus != STATUS_ERROR
                && mStatus != STATUS_END) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }

    }

    @Override
    public long getDuration() {
        if (mStatus != STATUS_ERROR
                && mStatus != STATUS_END
                && mStatus != STATUS_IDLE
                && mStatus != STATUS_INITIALIZED) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public void seekTo(long index) {
        if (mStatus == STATUS_PREPARED
                || mStatus == STATUS_STARTED
                || mStatus == STATUS_PAUSED
                || mStatus == STATUS_COMPLETED) {
            if (index <0) {
                index = 0;
            }
            if (index > getDuration()) {
                index = getDuration();
            }
            mMediaPlayer.seekTo(index);
        } else {
            Debuger.printfWarning("current status is unsupported this method");
        }

    }

    @Override
    public void setSpeed(float speed) {
        mSpeed = speed;
        if (mPlayerType == PLAYER__IJK && mMediaPlayer != null) {
            ((IjkMediaPlayer)mMediaPlayer).setSpeed(speed);
        }
    }

    @Override
    public void setLoop(boolean loop) {
        if (mStatus != STATUS_ERROR && mStatus != STATUS_END) {
            mMediaPlayer.setLooping(loop);
        } else {
            Debuger.printfWarning("current status is unsupported this method");
        }
    }

    @Override
    public void release() {
        mWorkHandler.sendEmptyMessage(HANDLER_RELEASE);
    }

    @Override
    public void setDisplay(Surface surface) {
        Message msg = mWorkHandler.obtainMessage();
        msg.what = HANDLER_SET_DISPLAY;
        msg.obj = surface;
        mWorkHandler.sendMessage(msg);
    }

    @Override
    public void setMute(boolean mute) {
        this.mMute = mute;
        if (mMediaPlayer != null) {
            if(mMute){
                mMediaPlayer.setVolume(0,0);
            }else {
                mMediaPlayer.setVolume(1,1);
            }
        }
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public boolean isPlaying() {
        return mStatus == STATUS_STARTED;
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setScreenOnWhilePlaying(screenOn);
        }
    }

    @Override
    public MediaInfo getMediaInfo() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getMediaInfo();
        }
        return null;
    }

    @Override
    public int getVideoWidth() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    /**
     * 获取播放器的示例
     * @param playerType
     * @return
     */
    protected static Player newInstance(@NonNull Context context, @Type int playerType){
        return new Player(context, playerType);
    }

}
