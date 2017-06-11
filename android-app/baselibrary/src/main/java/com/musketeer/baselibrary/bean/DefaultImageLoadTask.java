package com.musketeer.baselibrary.bean;

import android.widget.ImageView;

import com.musketeer.baselibrary.service.ImageLoadOption;

/**
 * Created by zhongxuqi on 15-12-9.
 */
public class DefaultImageLoadTask extends ImageLoadTask {
    public ImageView imageView;

    public DefaultImageLoadTask(ImageView imageView,String imageUrl, ImageLoadOption imageLoadOption) {
        super(imageUrl, imageLoadOption);
        this.imageView = imageView;
    }

    @Override
    public void preProcess() {
        if (imageLoadOption == null) {
            return;
        }
        imageView.setImageResource(imageLoadOption.getLoadingImage());
    }

    @Override
    public void errorProcess() {
        if (imageLoadOption == null) {
            return;
        }
        imageView.setImageResource(imageLoadOption.getErrorImage());
    }

    @Override
    public void doOnfinish() {
        imageView.setImageBitmap(bitmap);
    }
}
