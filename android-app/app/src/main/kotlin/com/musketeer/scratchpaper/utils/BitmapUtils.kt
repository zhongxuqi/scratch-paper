package com.musketeer.scratchpaper.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

/**
 * Created by zhongxuqi on 16/07/2017.
 */
object BitmapUtils {
    /**
     * 从文件名获取图片位图
     * @param paper_name
     * *
     * @return
     */
    fun getImageBitmap(image: File): Bitmap? {
        var bm = BitmapFactory.decodeFile(image.absolutePath)
        if (bm == null) {
            return null
        }
        return bm.copy(Bitmap.Config.ARGB_8888, true)
    }
}