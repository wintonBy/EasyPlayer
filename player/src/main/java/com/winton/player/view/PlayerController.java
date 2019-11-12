package com.winton.player.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.winton.player.IPlayer;
import com.winton.player.R;
import com.winton.player.model.VideoData;
import com.winton.player.view.listener.IEasyPlayerViewListener;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author: winton
 * @time: 2019/1/15 2:04 PM
 * @desc: 播控基类
 */
public class PlayerController extends FrameLayout implements VideoControl{

    private static final String TAG = "EasyPlayerView";

    private Context mContext;

    private View mController;
    private ImageView mIVSmallPlay;
    private ImageView mIVFullScreen;
    private SeekBar mSbProgress;
    private TextView mTVCurrentTime;
    private TextView mTVTotalView;
    
    private boolean mShowing;
    private boolean mDragging;

    private VideoControl mVideoView;

    private IEasyPlayerViewListener listener;
    private IPlayer mPlayer;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    public PlayerController(@NonNull Context context) {
        this(context, null);
    }

    public PlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mController = LayoutInflater.from(mContext).inflate(R.layout.easy_layout_base_player, null);
        mIVSmallPlay = mController.findViewById(R.id.iv_small_play);
        mIVFullScreen = mController.findViewById(R.id.iv_full_screen);
        mSbProgress = mController.findViewById(R.id.sb_progress);
        mTVCurrentTime = mController.findViewById(R.id.tv_current_time);
        mTVTotalView = mController.findViewById(R.id.tv_total_time);

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        mVideoView = new TPlayerView(mContext);
        FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.TOP);
        this.addView((TextureView)mVideoView, videoParams);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        this.addView(mController, params);
        initListener();
    }

    private void initListener() {
        mIVSmallPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying() && mVideoView.canPause()) {
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
                updatePausePlay();
            }
        });

        mIVFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updatePausePlay() {
        if (mIVSmallPlay == null) {
            return;
        }
        if (mVideoView.isPlaying()) {
            mIVSmallPlay.setImageResource(R.drawable.easy_icon_pause);
        } else {
            mIVSmallPlay.setImageResource(R.drawable.easy_icon_play);
        }
    }
    private String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours   = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private long setProgress() {
        if (mVideoView == null || mDragging) {
            return 0;
        }
        long position = getCurrentPosition();
        long duration = getDuration();
        if (mSbProgress != null) {
            if (duration > 0) {
                    // use long to avoid overflow
                    long pos = 1000L * position / duration;
                    mSbProgress.setProgress( (int) pos);
                }
            int percent = getBufferPercentage();
            mSbProgress.setSecondaryProgress(percent * 10);
        }

        if (mTVTotalView != null) {
            mTVTotalView.setText(stringForTime(duration));
        }
        if (mTVCurrentTime != null) {
                mTVCurrentTime.setText(stringForTime(position));
        }
        return position;
    }


    @Override
    public void setVideoData(VideoData data) {
        if (mVideoView == null) {
            return;
        }
        mVideoView.setVideoData(data);
    }

    @Override
    public void start() {
        if (mVideoView == null) {
            return;
        }
        mVideoView.start();
        if (mIVSmallPlay != null) {
            mIVSmallPlay.setImageResource(R.drawable.easy_icon_pause);
        }
    }

    @Override
    public void pause() {
        if (mVideoView == null) {
            return;
        }
        mVideoView.pause();
        mIVSmallPlay.setImageResource(R.drawable.easy_icon_play);
    }

    @Override
    public long getDuration() {
        if (mVideoView == null) {
            return 0;
        }
        return mVideoView.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        if (mVideoView == null) {
            return 0;
        }
        return mVideoView.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        if (mVideoView == null) {
            return;
        }
        mVideoView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        if(mVideoView == null) {
            return false;
        }
        return mVideoView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if(mVideoView == null) {
            return 0;
        }
        return mVideoView.getBufferPercentage();
    }

    @Override
    public boolean canPause() {
        if(mVideoView == null) {
            return true;
        }
        return mVideoView.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        if(mVideoView == null) {
            return false;
        }
        return mVideoView.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        if(mVideoView == null) {
            return false;
        }
        return mVideoView.canSeekBackward();
    }

    @Override
    public int getAudioSessionId() {
        if(mVideoView == null) {
            return -1;
        }
        return mVideoView.getAudioSessionId();
    }

    //    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//    }


}
