package com.musketeer.scratchpaper.fileutils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.musketeer.scratchpaper.bean.BitmapGroup
import com.musketeer.scratchpaper.bean.ImageGroup
import com.musketeer.scratchpaper.utils.BitmapUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.musketeer.scratchpaper.utils.ImageUtils
import java.io.File
import java.util.*

/**
 * Created by zhongxuqi on 16/07/2017.
 */
abstract class BaseFileUtils {
    companion object {
        val TAG = "PaperFileUtils"
    }

    var mBitmapGroup : MutableList<BitmapGroup> = mutableListOf<BitmapGroup>()
    var mSortedBitmapGroup : MutableList<Bitmap> = mutableListOf<Bitmap>()

    abstract fun getCachePath(): String
    abstract fun getCachePathComp(): String

    /**
     * 从图片路径获取图片名
     * @param paper_path
     * *
     * @return
     */
    fun getImageName(paper_path: String): String {
        return paper_path.substring(paper_path.lastIndexOf("/") + 1,
                paper_path.lastIndexOf(".png"))
    }

    /**
     * 从图片名获取缩略图片路径
     * @param image_name
     * *
     * @return
     */
    fun getImageThumbNailPath(image_name: String): String {
        if (image_name.contains(".png")) {
            return getCachePathComp() + image_name
        } else {
            return getCachePathComp() + image_name + ".png"
        }
    }

    /**
     * 从图片名获取图片路径
     * @param image_name
     * *
     * @return
     */
    fun getImagePath(image_name: String): String {
        if (image_name.indexOf(".png") >= 0) {
            return getCachePath() + image_name
        } else {
            return getCachePath() + image_name + ".png"
        }
    }

    /**
     * 从图片名获取图片位图
     * @param image_name
     * *
     * @return
     */
    fun getImage(image_name: String): Bitmap {
        return BitmapFactory.decodeFile(getImagePath(image_name))
                .copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * 从图片名获取图片缩略图
     * @param image_name
     * *
     * @return
     */
    fun getImageThumbNail(image_name: String): Bitmap? {
        if (FileUtils.isFileExist(getImageThumbNailPath(image_name))) {
            return BitmapFactory.decodeFile(getImageThumbNailPath(image_name))
                    .copy(Bitmap.Config.ARGB_8888, true)
        } else {
            return null
        }
    }

    /**
     * 保存图片到SD卡中
     * @param bitmap
     * *
     * @param image_name
     */
    open fun saveImage(bitmap: Bitmap, image_name: String) {
        var formatPaperName = image_name
        if (!formatPaperName.contains(".png")) {
            formatPaperName += ".png"
        }
        ImageUtils.saveImageToSD(bitmap,
                getCachePath() + formatPaperName)
        ImageUtils.saveImageToSD(ImageUtils.resizeImage(bitmap, 800, 1600),
                getCachePathComp() + formatPaperName)
    }

    /**
     * 从SD卡中删除图片
     * @param paper_name
     */
    fun deleteImage(paper_name: String?) {
        if (paper_name == null || paper_name.isEmpty()) {
            return
        }
        FileUtils.deleteFile(getImagePath(paper_name))
        FileUtils.deleteFile(getImageThumbNailPath(paper_name))
    }

    /**
     * 从SD卡中读取文件名列表
     * @return
     */
    fun readImageList(): MutableList<String> {
        val mImageList = ArrayList<String>()
        val f = File(getCachePathComp())
        val files = f.listFiles()
        if (files != null) {
            for (inFile in files) {
                if (!inFile.isDirectory()) {
                    mImageList.add(getImageName(inFile.getPath()))
                }
            }
        }
        return mImageList
    }

    /**
     * 读取文件列表，并按照天归类
     */
    fun readImageListGroup(): MutableList<ImageGroup> {
        val f = File(getCachePathComp())
        val files = f.listFiles()
        val mImageGroupMap = mutableMapOf<Long, ImageGroup>()
        if (files != null) {
            for (inFile in files) {
                if (!inFile.isDirectory()) {
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
                    calendar.timeInMillis = inFile.lastModified()
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val timeOfDay = calendar.timeInMillis
                    if (mImageGroupMap.containsKey(timeOfDay)) {
                        mImageGroupMap.get(timeOfDay)?.imageList?.add(inFile)
                    } else {
                        val paperGroup = ImageGroup(timeOfDay)
                        paperGroup.imageList.add(inFile)
                        mImageGroupMap.put(timeOfDay, paperGroup)
                    }
                }
            }
        }
        val mImageGroupList = mutableListOf<ImageGroup>()
        for (value in mImageGroupMap.values) {
            mImageGroupList.add(value)
        }
        mImageGroupList.sortWith(object: kotlin.Comparator<ImageGroup>{
            override fun compare(o1: ImageGroup, o2: ImageGroup): Int {
                val diff = o2.timeOfData - o1.timeOfData
                if (diff == 0L) {
                    return 0
                } else if (diff > 0L) {
                    return 1
                } else {
                    return -1
                }
            }
        })
        for (imageGroup in mImageGroupList) {
            imageGroup.imageList.sortWith(object : kotlin.Comparator<File> {
                override fun compare(o1: File, o2: File): Int {
                    val diff = o2.lastModified() - o1.lastModified()
                    if (diff == 0L) {
                        return 0
                    } else if (diff > 0L) {
                        return 1
                    } else {
                        return -1
                    }
                }
            })
        }
        return mImageGroupList
    }

    // 读取文件，并转为Bitmap
    fun readBitmapListGroup(reload : Boolean): MutableList<BitmapGroup> {
        if (reload || this.mBitmapGroup.isEmpty()) {
            val imageGroupList = readImageListGroup()
            this.mBitmapGroup = mutableListOf<BitmapGroup>()
            for (imageGroup in imageGroupList) {
                var bitmapGroup = BitmapGroup(imageGroup.timeOfData)
                for (imageFile in imageGroup.imageList) {
                    var bitmap = BitmapUtils.getImageBitmap(imageFile)
                    bitmapGroup.imageList.add(bitmap)
                    bitmapGroup.imageNameMap.put(bitmap, imageFile.name)
                }
                this.mBitmapGroup.add(bitmapGroup)
            }
        }
        return this.mBitmapGroup
    }

    fun readSortedImageList(): MutableList<File> {
        val f = File(getCachePath())
        val files  = mutableListOf<File>()
        for (file in f.listFiles()) {
            files.add(file)
        }
        files.sortWith(object : kotlin.Comparator<File>{
            override fun compare(o1: File, o2: File): Int {
                val diff = o2.lastModified() - o1.lastModified()
                if (diff == 0L) {
                    return 0
                } else if (diff > 0L) {
                    return 1
                } else {
                    return -1
                }
            }
        })
        return files
    }

    fun readSortedBitmapList(reload : Boolean): List<Bitmap> {
        if (reload || this.mSortedBitmapGroup.isEmpty()) {
            val imageList = readSortedImageList()
            this.mSortedBitmapGroup = mutableListOf<Bitmap>()
            for (imageFile in imageList) {
                this.mSortedBitmapGroup.add(BitmapUtils.getImageBitmap(imageFile))
            }
        }
        return this.mSortedBitmapGroup
    }
}