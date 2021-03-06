package com.winton.player.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.winton.player.EasyPlayer;
import com.winton.player.IPlayer;
import com.winton.player.listener.PlayerListener;
import com.winton.player.listener.PlayerListenerAdapter;
import com.winton.player.model.VideoData;

import tv.danmaku.ijk.media.player.MediaInfo;

import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;

/**
 * @author: winton
 * @time: 2019/1/15 7:41 PM
 * @desc: 最简单的播控，只能播放
 */
public class VideoControlT extends TextureView implements IVideoControl {

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private IPlayer mPlayer;
    private VideoData mVideo;
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    private SurfaceTexture mSurfaceTexture;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private int mSeekWhenPrepared;  // recording the seek position while preparing
    //default video can be pause
    private boolean mCanPause = true;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;
    private int mCurrentBufferPercentage;
    private AudioManager mAudioManager;
    private int mAudioFocusType = AudioManager.AUDIOFOCUS_GAIN; // legacy focus gain
    private AudioAttributes mAudioAttributes;

    private IPlayerController mController;

    private PlayerListener mListener;

    public VideoControlT(Context context) {
        this(context,null);
    }

    public VideoControlT(Context context, AttributeSet attrs) {
        super(context, attrs);
        mVideoWidth = 0;
        mVideoHeight = 0;

        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mAudioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build();

        setSurfaceTextureListener(mSurfaceTextureListener);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = heightSpecSize;
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }

            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                        // too tall, decrease both width and height
                        height = heightSpecSize;
                        width = height * mVideoWidth / mVideoHeight;
                    }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                        // too wide, decrease both width and height
                        width = widthSpecSize;
                        height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public long getDuration() {
        if (isInPlaybackState()) {
            return mPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public void setPlayerController(IPlayerController controller) {
        if (mController != null) {
            mController.hide();
        }
        mController = controller;
    }


    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void setVideoData(VideoData data) {
        mVideo = data;
    }

    private boolean isInPlaybackState() {
        return (mPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    /*
    * release the media player in any state
    */
   private void release(boolean clearTargetState) {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mCurrentState = STATE_IDLE;
            if (clearTargetState) {
                mTargetState  = STATE_IDLE;
            }
            if (mAudioFocusType != AudioManager.AUDIOFOCUS_NONE) {
                  mAudioManager.abandonAudioFocus(null);
            }
        }
   }

    private void openVideo() {
        if (mVideo == null || mSurfaceTexture == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        if (mAudioFocusType != AudioManager.AUDIOFOCUS_NONE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mAudioManager.requestAudioFocus(new AudioFocusRequest.Builder(mAudioFocusType)
                        .setAudioAttributes(mAudioAttributes).build());
            } else {
                mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN_TRANSIENT);
            }
        }
        mCurrentState = STATE_PREPARING;
        mPlayer = EasyPlayer.newInstance(getContext(), IPlayer.PLAYER__IJK);
        mPlayer.setPlayerListener(mPlayerListener);
        mPlayer.videoData(mVideo);
        mPlayer.setDisplay(new Surface(mSurfaceTexture));
        mPlayer.setScreenOnWhilePlaying(true);

    }

    public void setPlayerListener(PlayerListener listener) {
       mListener = listener;
    }

    private SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            mSurfaceTexture = surface;
            openVideo();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            boolean isValidState =  (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == width && mVideoHeight == height);
            if (mPlayer != null && isValidState && hasValidSize) {
                if(mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            release(true);
            mSurfaceTexture = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

   private PlayerListener mPlayerListener = new PlayerListenerAdapter() {

       @Override
       public void onPrepared(IPlayer player) {
           mCurrentState = STATE_PREPARED;
           MediaInfo mediaInfo = player.getMediaInfo();
           mVideoWidth = player.getVideoWidth();
           mVideoHeight = player.getVideoHeight();
           int seekPosition = mSeekWhenPrepared;
           if (seekPosition > 0) {
               seekTo(seekPosition);
           }
           if (mVideoWidth != 0 && mVideoHeight != 0) {
               mSurfaceTexture.setDefaultBufferSize(mVideoWidth, mVideoHeight);
               if (mVideoWidth == mSurfaceWidth && mVideoHeight == mSurfaceHeight) {
                   if (mTargetState == STATE_PLAYING) {
                       start();
                   }
               } else {
                   if (mTargetState == STATE_PLAYING) {
                       start();
                   }
               }
           }
       }

       @Override
       public void onVideoSizeChanged(IPlayer player, int i, int i1, int i2, int i3) {
           mVideoWidth = i;
           mVideoHeight = i1;
           if (mSurfaceTexture != null) {
               mSurfaceTexture.setDefaultBufferSize(mVideoWidth, mVideoHeight);
               requestLayout();
           }
       }

       @Override
       public void onCompletion(IPlayer player) {
           mCurrentState = STATE_PLAYBACK_COMPLETED;
           mTargetState = STATE_PLAYBACK_COMPLETED;
           if (mListener != null) {
               mListener.onCompletion(player);
           }
       }

       @Override
       public boolean onError(IPlayer player, int i, int i1) {
           mCurrentState = STATE_ERROR;
           mTargetState = STATE_ERROR;
           if (mListener != null) {
               if (mListener.onError(player, i, i1)){
                   return true;
               }
           }
           return true;
       }

       @Override
       public boolean onInfo(IPlayer player, int i, int i1) {
           if (mListener != null) {
               mListener.onInfo(player, i, i1);
           }
           return true;
       }

       @Override
       public void onSeekComplete(IPlayer player) {
           if (mListener != null) {
               mListener.onSeekComplete(player);
           }
       }

       @Override
       public void onBufferingUpdate(IPlayer player, int i) {
           mCurrentBufferPercentage = i;
           if (mListener != null) {
               mListener.onBufferingUpdate(player, i);
           }
       }
   };
}
