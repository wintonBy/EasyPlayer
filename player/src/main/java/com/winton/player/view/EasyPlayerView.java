package com.winton.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.winton.player.IPlayer;
import com.winton.player.R;
import com.winton.player.view.iview.IEasyPlayerView;
import com.winton.player.view.listener.IEasyPlayerViewListener;

import static tv.danmaku.ijk.media.player.IMediaPlayer.MEDIA_INFO_BUFFERING_END;
import static tv.danmaku.ijk.media.player.IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;

/**
 * @author: winton
 * @time: 2019/1/15 2:04 PM
 * @desc: 播控基类
 */
public class EasyPlayerView extends FrameLayout implements IEasyPlayerView, View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, SurfaceHolder.Callback {

    private static final String TAG = "EasyPlayerView";

    private View playerView;
    private SurfaceView surfaceView;
    private ImageView mIVSmallPlay;
    private ImageView mIVFullScreen;
    private SeekBar mSbProgress;
    private TextView mTVCurrentTime;
    private TextView mTVTotalView;

    private IEasyPlayerViewListener listener;
    private IPlayer mPlayer;
    public EasyPlayerView(Context context) {
        this(context,null);
    }
    public EasyPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        try{
            playerView = LayoutInflater.from(context).inflate(R.layout.easy_layout_base_player,null);
            surfaceView = new SurfaceView(context);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(surfaceView,layoutParams);
            this.addView(playerView,layoutParams);
            surfaceView.getHolder().addCallback(this);
            initView();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    private void initView(){
        playerView.setOnClickListener(this);
        mIVSmallPlay = playerView.findViewById(R.id.iv_small_play);
        mIVSmallPlay.setOnClickListener(this);
        mIVFullScreen = playerView.findViewById(R.id.iv_full_screen);
        mIVFullScreen.setOnClickListener(this);
        mTVTotalView = playerView.findViewById(R.id.tv_total_time);
        mTVCurrentTime = playerView.findViewById(R.id.tv_current_time);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.iv_small_play){
            clickPlay(v);
            return;
        }
        if(id == R.id.iv_full_screen){
            clickFullScreen(v);
            return;
        }
        if(id == R.id.fl_play_ui){
            clickPlayUi(v);
            return;
        }
    }

    /**
     * 点击播控空白处
     * @param v
     */
    private void clickPlayUi(View v) {
        if(listener != null){
            listener.onClickPlayerView(v);
        }
    }

    /**
     * 点击播放按钮
     * @param v
     */
    private void clickPlay(View v) {
        int statue = mPlayer.getStatus();
        if(statue == IPlayer.STATUS_STARTED){
            mPlayer.pause();
        }else {
            mPlayer.start();
        }
        if(listener != null){
            listener.onClickPlay(v);
        }
    }

    /**
     * 点击全屏按钮
     * @param v
     */
    private void clickFullScreen(View v) {
        if(listener != null){
            listener.onClickFullScreen(v);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    private void setProgress(int progress) {
        if(mSbProgress != null){
            if(progress <0){
                progress = 0;
            }
            if(progress >100){
                progress = 100;
            }
            mSbProgress.setProgress(progress);
        }
    }

    private void setCurrentTime(String text) {
        if(mTVCurrentTime != null && text != null){
            mTVCurrentTime.setText(text);
        }
    }

    private void setTotalTime(String text) {
        if(mTVTotalView != null && text != null){
            mTVTotalView.setText(text);
        }
    }

    private void showControlUi(boolean show) {

    }


    @Override
    public SurfaceView getSurface() {
        return surfaceView;
    }

    @Override
    public void setupPlayer(IPlayer player) {
        mPlayer = player;
        invalidate();
    }

    @Override
    public void setListener(IEasyPlayerViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(holder != null && mPlayer != null){
            mPlayer.setDisplay(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(holder != null && mPlayer != null){
            mPlayer.setDisplay(holder.getSurface());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mPlayer != null){
            mPlayer.setDisplay(null);
        }
    }
}
