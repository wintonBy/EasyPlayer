package com.winton.player;

import android.content.Context;

import com.winton.player.utils.Debuger;

/**
 * @author: winton
 * @time: 2019/1/11 11:03 AM
 * @desc: 对外提供的类
 */
public class EasyPlayer {

    /**
     * 获取播放器实例
     * @param context
     * @param playerType
     * @return
     */
    public static IPlayer newInstance(Context context, @Type int playerType){
        return Player.newInstance(context,playerType);
    }

    /**
     * 是否开启日志
     * @param open
     */
    public static void openLog(boolean open){
        if(open){
            Debuger.enable();
        }else {
            Debuger.disable();
        }
    }
}
