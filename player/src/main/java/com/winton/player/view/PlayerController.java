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
import com.winton.player.view.listener.IEasyPlayerViewListener;

/**
 * @author: winton
 * @time: 2019/1/15 2:04 PM
 * @desc: 播控基类
 */
public class PlayerController extends FrameLayout {

    private static final String TAG = "EasyPlayerView";

    private Context mContext;
    private WindowManager mWindowManager;
    private Window mWindow;

    private View mController;
    private ImageView mIVSmallPlay;
    private ImageView mIVFullScreen;
    private SeekBar mSbProgress;
    private TextView mTVCurrentTime;
    private TextView mTVTotalView;

    private VideoControl mVideoView;

    private IEasyPlayerViewListener listener;
    private IPlayer mPlayer;

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

            }
        });

        mIVFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public VideoControl getPlayer() {
        return mVideoView;
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//    }


}
