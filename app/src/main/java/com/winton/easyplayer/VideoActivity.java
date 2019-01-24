package com.winton.easyplayer;

import android.app.Activity;
import android.os.Bundle;

import com.winton.player.EasyPlayer;
import com.winton.player.IPlayer;
import com.winton.player.view.EasyPlayerView;

/**
 * @author: winton
 * @time: 2019/1/14 3:44 PM
 * @desc: 描述
 */
public class VideoActivity extends Activity {

    String testUrl = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";

    private EasyPlayerView simplePlayerView;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video);
        simplePlayerView = findViewById(R.id.player);
        simplePlayerView.setupPlayer(EasyPlayer.newInstance(this,IPlayer.PLAYER__IJK));
        simplePlayerView.getPlayer().url(testUrl);
        simplePlayerView.getPlayer().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
