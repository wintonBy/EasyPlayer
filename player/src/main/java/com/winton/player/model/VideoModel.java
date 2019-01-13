package com.winton.player.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: winton
 * @time: 2019/1/11 3:42 PM
 * @desc: 视频Model
 */
public class VideoModel {

    String url;
    Map<String, String> mapHeadData;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getMapHeadData() {
        if(mapHeadData == null){
            mapHeadData = new HashMap<>(1);
        }
        return mapHeadData;
    }

    public void setMapHeadData(Map<String, String> mapHeadData) {
        this.mapHeadData = mapHeadData;
    }
}
