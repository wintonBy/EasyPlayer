package com.winton.player.model;

import java.util.Map;

public class VideoNetwork extends VideoData {

    private String url;
    private Map<String, String> headers;

    public VideoNetwork(String url, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public boolean needCache() {
        return true;
    }
}
