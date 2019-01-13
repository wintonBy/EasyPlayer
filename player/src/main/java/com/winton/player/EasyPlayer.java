package com.winton.player;

import android.content.Context;

/**
 * @author: winton
 * @time: 2019/1/11 11:03 AM
 * @desc: 对外提供的类
 */
public class EasyPlayer {

    /**
     * 获取播放器实例
     * @param context
     * @param palyerType
     * @return
     */
    public static IPlayer newInstance(Context context, @Type int palyerType){
        return Player.newInstance(context,palyerType);
    }
}
