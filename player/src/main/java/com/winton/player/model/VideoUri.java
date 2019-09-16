package com.winton.player.model;

import android.content.Context;
import android.net.Uri;

public class VideoUri extends VideoData{
    private Context context;

    private Uri  uri;

    public VideoUri(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    public Context getContext() {
        return context;
    }

    public Uri getUri() {
        return uri;
    }
}
