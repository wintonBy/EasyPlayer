package com.winton.player.model;

import android.net.Uri;

import java.io.File;
import java.io.FileDescriptor;

public class VideoFile extends VideoData {

    private String data;

    public VideoFile(String path) {
        data = path;
        File file = new File(data);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("path is not a file or exist");
        }
    }

    public String getData() {
        return data;
    }

}
