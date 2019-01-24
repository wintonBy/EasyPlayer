package com.winton.player.listener;

import android.view.Surface;

/**
 * @author: winton
 * @time: 2019/1/22 8:41 PM
 * @desc: Surface变化监听回调
 */
public interface ISurfaceListener {

    void onSurfaceAvailable(Surface surface);

    void onSurfaceSizeChanged(Surface surface, int width, int height);

    void onSurfaceDestroyed(Surface surface);

}
