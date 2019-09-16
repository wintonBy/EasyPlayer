package com.winton.player.model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;

/**
 * @author: winton
 * @time: 2019/1/11 3:42 PM
 * @desc: 视频Model
 */
public class VideoData {
    public static final int TYPE_NETWORK = 1;
    public static final int TYPE_FILE = 2;
    public static final int TYPE_URI = 3;

    public boolean needCache() {
        return false;
    }



    public static class Builder {
        private final int type;

        private String url;
        private Map<String, String> headers;

        private String path;

        private Context context;
        private Uri uri;


        public Builder(int type) {
            this.type = type;
        }

        public Builder url(@NonNull  String url) {
            this.url = url;
            return this;
        }

        public Builder head(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder path(@NonNull String path) {
            this.path = path;
            return this;
        }

        public Builder uri(Context context, Uri uri) {
            this.context = context;
            this.uri = uri;
            return this;
        }

        public VideoData build() {
            if (type == TYPE_NETWORK) {
                if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url is NULL");
                return new VideoNetwork(url, headers);
            }

            if (type == TYPE_FILE) {
                if (TextUtils.isEmpty(path)) throw new IllegalArgumentException("path is NULL");
                return new VideoFile(path);
            }

            if (type == TYPE_URI) {
                if (context == null) {
                    throw new IllegalArgumentException("context is NULL");
                }
                if (uri == null) {
                    throw new IllegalArgumentException("uri is NULL");
                }
                return new VideoUri(context, uri);
            }
            throw new IllegalArgumentException("unsupported video type");
        }

    }



}
