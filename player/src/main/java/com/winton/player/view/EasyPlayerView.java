package com.winton.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.winton.player.R;
import com.winton.player.utils.Debuger;

/**
 * @author: winton
 * @time: 2019/1/15 2:04 PM
 * @desc: 播控基类
 */
public class EasyPlayerView extends FrameLayout implements IEasyPlayerView,View.OnClickListener,View.OnTouchListener,SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "EasyPlayerView";

    private View playerView;

    private SurfaceView surfaceView;

    private ImageView mIVSmallPlay;

    private ImageView mIVFullScreen;

    private SeekBar mSbProgress;

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
        }catch (Exception e){
            Debuger.printfError(TAG,e);
        }
    }


    @Override
    public void onClick(View v) {

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

    public void setBufferProgress(int bufferPercents) {

    }

    @Override
    public SurfaceView getSurface() {
        return null;
    }

    public int getPlayerLayoutId() {
        return R.layout.easy_layout_base_player;
    }
}
