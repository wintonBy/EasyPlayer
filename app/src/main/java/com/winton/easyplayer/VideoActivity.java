package com.winton.easyplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.winton.player.EasyPlayer;
import com.winton.player.IPlayer;
import com.winton.player.utils.Debuger;

/**
 * @author: winton
 * @time: 2019/1/14 3:44 PM
 * @desc: 描述
 */
public class VideoActivity extends Activity implements SurfaceHolder.Callback {

    String testUrl = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";

    private SurfaceView surfaceView;
    private IPlayer mPlayer;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video);
        surfaceView = findViewById(R.id.surface);
        mPlayer = EasyPlayer.newInstance(this,IPlayer.PLAYER__IjkMediaPlayer);
        mPlayer.url(testUrl);
        Log.d("winton","url 装载");
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("winton","surface 装载");
        mPlayer.setDisplay(holder.getSurface());
        mPlayer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}