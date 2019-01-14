package com.winton.player.cache;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * @author: winton
 * @time: 2019/1/14 6:20 PM
 * @desc: 缓存帮助类
 */
public class CacheHelper {
    private CacheConfig mConfig;

    private HttpProxyCacheServer proxy;

    private static CacheHelper instance;

    private Context mContext;

    private CacheHelper(Context context){
        mConfig = new CacheConfig();
        context = mContext;
    }

    public static CacheHelper getInstance(Context context){
        if(instance == null){
            synchronized (CacheHelper.class){
                instance = new CacheHelper(context);
            }
        }
        return instance;
    }

    public void config(CacheConfig config){
        this.mConfig = config;
    }

    public HttpProxyCacheServer getProxy(){
        if(proxy == null){
            proxy = new HttpProxyCacheServer.Builder(mContext)
                    .maxCacheSize(mConfig.getCacheSize())
                    .maxCacheFilesCount(mConfig.getCacheFileNum())
                    .build();
        }
        return proxy;
    }


}
