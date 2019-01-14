package com.winton.player.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

/**
 * @author: winton
 * @time: 2019/1/14 5:31 PM
 * @desc: 屏幕旋转工具类
 */
@SuppressWarnings("ALL")
public class OrientationUtil {
    public static final int ORIENTAIION0 = 0;
    public static final int ORIENTAIION90 = ORIENTAIION0 + 1;
    public static final int ORIENTAIION180 = ORIENTAIION90 + 1;
    public static final int ORIENTAIION270 = ORIENTAIION180 + 1;

    private OrientationEventListener mOrientationEventListener;
    private OrientationUtil instance;
    private int mOrientation;
    private boolean portrait;
    private boolean fullScreenClick = false;
    private boolean backClcik = false;

    public OrientationUtil(final Activity activity) {
        mOrientationEventListener = new OrientationEventListener(activity.getApplicationContext(), SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation != -1) {
                    mOrientation = orientation;
                    if (backClcik) {
                        if (orientation >= 330 || orientation <= 60) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                                backClcik = false;
                            }
                        }
                    }
                    if (fullScreenClick) {
                        if ((orientation >= 250 && orientation <= 300) || (orientation >= 60 && orientation <= 160)) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                                fullScreenClick = false;
                            }
                        }
                    }
                }
            }
        };
    }

    public void enableListenen() {
        if (mOrientationEventListener != null && mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    public void removeListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener = null;
        }
    }

    public void setPortrait(boolean por) {
        portrait = por;
    }

    public void setFullScreenClick(boolean fullScreenClick) {
        this.fullScreenClick = fullScreenClick;
    }

    public void setBackClcik(boolean backClcik) {
        this.backClcik = backClcik;
    }

    public void disableListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
            mOrientation = -1;
        }
    }

    public int getmOrientation() {
        if (mOrientation > 330 || mOrientation <= 70) {
            if (portrait) {
                return ORIENTAIION0;
            } else {
                return ORIENTAIION90;
            }
        }
        if (mOrientation > 70 && mOrientation <= 160) {
            return ORIENTAIION90;
        }
        if (mOrientation > 160 && mOrientation <= 250) {
            return ORIENTAIION180;
        }
        if (mOrientation > 250 && mOrientation <= 330) {
            return ORIENTAIION270;
        }
        return -1;
    }
}
