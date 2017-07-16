package com.musketeer.scratchpaper.fileutils

import android.graphics.Bitmap
import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.utils.ImageUtils

/**
 * Created by zhongxuqi on 16/07/2017.
 */
object ImageFileUtils: BaseFileUtils() {
    val TAG = "ImageFileUtils"

    override fun getCachePath(): String {
        return MainApplication.mCacheImagePath
    }

    override fun getCachePathComp(): String {
        return MainApplication.mCacheImagePathComp
    }

    /**
     * 保存图片到SD卡中
     * @param bitmap
     * *
     * @param image_name
     */
    override fun saveImage(bitmap: Bitmap, image_name: String) {
        var formatPaperName = image_name
        if (!formatPaperName.contains(".png")) {
            formatPaperName += ".png"
        }
        ImageUtils.saveImageToSD(bitmap,
                getCachePath() + formatPaperName)
        var ratio : Float = 1F
        if (bitmap.width > 800) {
            ratio = 800.toFloat() / bitmap.width
        }
        ImageUtils.saveImageToSD(ImageUtils.resizeImage(bitmap, (bitmap.width.toFloat() * ratio).toInt(),
                (bitmap.height.toFloat() * ratio).toInt()),
                getCachePathComp() + formatPaperName)
    }
}