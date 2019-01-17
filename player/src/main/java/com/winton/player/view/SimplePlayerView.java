package com.winton.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.winton.player.view.iview.ISPlayerView;


/**
 * @author: winton
 * @time: 2019/1/15 7:41 PM
 * @desc: 最简单的播控，只能播放
 */
public class SimplePlayerView extends FrameLayout implements ISPlayerView {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Surface surface;


    public SimplePlayerView(Context context) {
        this(context,null);
    }

    public SimplePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        surfaceView = new SurfaceView(context);
        this.addView(surfaceView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }


    @Override
    public SurfaceView getSurface() {
        return surfaceView;
    }
}
