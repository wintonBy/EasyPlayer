package com.winton.player.cache;

/**
 * @author: winton
 * @time: 2019/1/14 6:20 PM
 * @desc: 缓存视频的配置文件
 */
public class CacheConfig {


    /**
     * 缓存大小 默认1G
     */
    public long cacheSize = 1 * 1024 * 1024 *1024;
    /**
     * 缓存文件数限制,默认不限制
     */
    public int cacheFileNum = Integer.MAX_VALUE;


    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getCacheFileNum() {
        return cacheFileNum;
    }

    public void setCacheFileNum(int cacheFileNum) {
        this.cacheFileNum = cacheFileNum;
    }
}
