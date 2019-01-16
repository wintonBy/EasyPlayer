package com.winton.player.view.iview;

import android.view.SurfaceView;

/**
 * @author: winton
 * @time: 2019/1/15 11:25 AM
 * @desc: 用SurfaceView实现播放器需要集成此接口
 */
public interface ISPlayerView {

    /**
     * 获取绘制视频的Surface
     * @return
     */
    SurfaceView getSurface();

}
