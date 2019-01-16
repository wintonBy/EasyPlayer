package com.winton.player.view.iview;

import android.view.TextureView;

/**
 * @author: winton
 * @time: 2019/1/16 8:30 PM
 * @desc: 用TexttureView实现播放器，需要集成此接口
 */
public interface ITPlayerView {
    /**
     * 获取播控上的TextureView
     * @return
     */
    TextureView getTexture();
}
