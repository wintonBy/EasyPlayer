package com.winton.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.winton.player.IPlayer;
import com.winton.player.R;
import com.winton.player.utils.Debuger;
import com.winton.player.view.iview.IEasyPlayerView;
import com.winton.player.view.iview.ISPlayerView;

/**
 * @author: winton
 * @time: 2019/1/15 2:04 PM
 * @desc: 播控基类
 */
public class EasyPlayerView extends FrameLayout implements IEasyPlayerView,View.OnClickListener,View.OnTouchListener,SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "EasyPlayerView";

    private IPlayer player;

    private View playerView;

    private SurfaceView surfaceView;

    private ImageView mIVSmallPlay;

    private ImageView mIVFullScreen;

    private SeekBar mSbProgress;

    private TextView mTVCurrentTime;

    private TextView mTVTotalView;

    public EasyPlayerView(Context context) {
        this(context,null);
    }

    public EasyPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        try{
            playerView = LayoutInflater.from(context).inflate(getPlayerLayoutId(),null);
            surfaceView = new SurfaceView(context);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(surfaceView,layoutParams);
            this.addView(playerView,layoutParams);
            initView();
        }catch (Exception e){
            Debuger.printfError(TAG,e);
        }
    }

    private void initView(){
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
            return;
        }
        if(id == R.id.iv_full_screen){
            return;
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
    @Override
    public void setProgress(int progress) {
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

    @Override
    public void setCurrentTime(String text) {
        if(mTVCurrentTime != null && text != null){
            mTVCurrentTime.setText(text);
        }
    }

    @Override
    public void setTotalTime(String text) {
        if(mTVTotalView != null && text != null){
            mTVTotalView.setText(text);
        }
    }

    @Override
    public void showControlUi(boolean show) {

    }

    @Override
    public SurfaceView getSurface() {
        return surfaceView;
    }

    public int getPlayerLayoutId() {
        return R.layout.easy_layout_base_player;
    }

    @Override
    public void registerPlayer(IPlayer player) {
        this.player = player;
    }

    @Override
    public void release() {
        this.player = null;
    }
}
