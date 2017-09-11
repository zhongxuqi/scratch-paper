package com.musketeer.scratchpaper.bean

import android.graphics.Bitmap

/**
 * Created by zhongxuqi on 11/09/2017.
 */

class BitmapGroup(timeOfData: Long) {
    val timeOfData: Long
    val imageList = mutableListOf<Bitmap>()
    val imageNameMap = mutableMapOf<Bitmap, String>()

    init {
        this.timeOfData = timeOfData
    }
}
