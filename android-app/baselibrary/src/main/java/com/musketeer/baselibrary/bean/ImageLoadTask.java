package com.musketeer.baselibrary.bean;

import android.graphics.Bitmap;

import com.musketeer.baselibrary.service.ImageLoadOption;

/**
 * Created by zhongxuqi on 15-12-9.
 */
public abstract class ImageLoadTask {
    public String imageUrl;
    public Bitmap bitmap;
    public ImageLoadOption imageLoadOption;

    public ImageLoadTask(String imageUrl, ImageLoadOption imageLoadOption) {
        this.imageUrl=imageUrl;
        this.imageLoadOption=imageLoadOption;
    }

    public abstract void preProcess();

    public abstract void errorProcess();

    public abstract void doOnfinish();
}
