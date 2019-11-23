package com.winton.player.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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
public class PlayerController extends FrameLayout implements IPlayerController{

    private static final String TAG = "EasyPlayerView";

    private static final int mDefaultTimeout = 3000;

    private Context mContext;

    private View mRoot;
    private ImageView mIVSmallPlay;
    private ImageView mIVFullScreen;
    private SeekBar mSbProgress;
    private TextView mTVCurrentTime;
    private TextView mTVTotalView;

    private boolean mShowing;
    private boolean mDragging;
    private boolean mFullScreen;

    private IVideoControl mPlayer;

    private IEasyPlayerViewListener listener;

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

    private void init(@Nullable AttributeSet attrs) {
        mPlayer = new VideoControlS(mContext);
        mPlayer.setPlayerController(this);

        int playerGravity = Gravity.TOP | Gravity.LEFT;
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, playerGravity);

        View player = (View) mPlayer;
        addView(player, frameParams);

        int controllerGravity = Gravity.BOTTOM | Gravity.LEFT;
        FrameLayout.LayoutParams controllerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, controllerGravity);

        View v = makeControllerView();
        addView(v, controllerParams);
    }

    private View makeControllerView() {
        mRoot = LayoutInflater.from(mContext).inflate(R.layout.easy_layout_base_player, null);
        initControllerView(mRoot);
        return mRoot;
    }

    private void initControllerView(View v) {
        mIVSmallPlay = v.findViewById(R.id.iv_small_play);
        if (mIVSmallPlay != null) {
            mIVSmallPlay.requestFocus();
            mIVSmallPlay.setOnClickListener(mPauseListener);
        }

        mIVFullScreen = mRoot.findViewById(R.id.iv_full_screen);
        mSbProgress = mRoot.findViewById(R.id.sb_progress);
        if (mSbProgress != null) {
            mSbProgress.setOnSeekBarChangeListener(mSeekListener);
            mSbProgress.setMax(1000);
        }
        mTVCurrentTime = mRoot.findViewById(R.id.tv_current_time);
        mTVTotalView = mRoot.findViewById(R.id.tv_total_time);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    // This is called whenever mAnchor's layout bound changes
    private final OnLayoutChangeListener mLayoutChangeListener = new OnLayoutChangeListener() {
         @Override
         public void onLayoutChange(View v, int left, int top, int right,
             int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
         }
    };

    private final OnClickListener mPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPlayer.isPlaying() && mPlayer.canPause()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
            updatePausePlay();
            show(mDefaultTimeout);
        }
    };

    private final OnClickListener mFullScreenListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFullScreen) {

            } else {

            }
        }
    };

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            long duration = mPlayer.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mPlayer.seekTo((int)newPosition);
            if (mTVCurrentTime != null) {
                mTVCurrentTime.setText(stringForTime(newPosition));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            show(3600000);
            mDragging = true;
            //when drag, don't update progress
            removeCallbacks(mShowProgress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(mDefaultTimeout);
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            post(mShowProgress);
        }
    };

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            long pos = setProgress();
            if (!mDragging && mShowing && mPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private void updatePausePlay() {
        if (mIVSmallPlay == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
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
        if (mPlayer == null || mDragging) {
            return 0;
        }
        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mSbProgress != null) {
            if (duration > 0) {
                    // use long to avoid overflow
                    long pos = 1000L * position / duration;
                    mSbProgress.setProgress( (int) pos);
                }
            int percent = mPlayer.getBufferPercentage();
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

    public void show(int timeout) {
        if (!mShowing && mRoot != null) {
            setProgress();
            mRoot.setVisibility(VISIBLE);
            mShowing = true;
        }
        updatePausePlay();
        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        post(mShowProgress);

        if (timeout != 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    private Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    public void hide() {
        if (mRoot == null) {
            return;
        }
        if (mShowing) {
            removeCallbacks(mShowProgress);
            mRoot.setVisibility(GONE);
        }
        mShowing = false;
    }

    @Override
    public void show() {
        show(mDefaultTimeout);
    }

    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                show(0);
                break;
            case MotionEvent.ACTION_UP:
                show(mDefaultTimeout);
                break;
            case MotionEvent.ACTION_CANCEL:
                hide();
                break;
        }
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        show(mDefaultTimeout);
        return false;
    }

    public void setVideoData(VideoData data) {
        if (mPlayer != null) {
            mPlayer.setVideoData(data);
        }
    }

    public void start() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    public long getDuration() {
        if (mPlayer != null) {
            mPlayer.getDuration();
        }
        return 0;
    }

    public long getCurrentPosition() {
        if (mPlayer != null) {
            mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int pos) {
        if (mPlayer != null) {
            mPlayer.seekTo(pos);
        }
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

}
