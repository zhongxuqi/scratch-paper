/**
 * @Title: PaperFileUtils.java
 * *
 * @Package com.musketeer.scratchpaper.paperfile
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-12-7 下午7:08:03
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.paperfile

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.bean.PaperGroup
import com.musketeer.scratchpaper.common.Contants
import com.musketeer.scratchpaper.utils.FileUtils
import com.musketeer.scratchpaper.utils.ImageUtils

import java.io.File
import java.util.ArrayList

/**
 * @author zhongxuqi
 */
object PaperFileUtils {
    /**
     * 从草稿纸路径获取草稿纸名
     * @param paper_path
     * *
     * @return
     */
    fun getPaperName(paper_path: String): String {
        return paper_path.substring(paper_path.lastIndexOf("/") + 1,
                paper_path.lastIndexOf(".png"))
    }

    /**
     * 从草稿纸名获取缩略草稿纸路径
     * @param paper_name
     * *
     * @return
     */
    fun getPaperThumbNailPath(paper_name: String): String {
        if (paper_name.contains(".png")) {
            return MainApplication.mCachePathComp + paper_name
        } else {
            return MainApplication.mCachePathComp + paper_name + ".png"
        }
    }

    /**
     * 从草稿纸名获取草稿纸路径
     * @param paper_name
     * *
     * @return
     */
    fun getPaperPath(paper_name: String): String {
        if (paper_name.indexOf(".png") >= 0) {
            return MainApplication.mCachePath + paper_name
        } else {
            return MainApplication.mCachePath + paper_name + ".png"
        }
    }

    /**
     * 从草稿纸名获取草稿纸位图
     * @param paper_name
     * *
     * @return
     */
    fun getPaper(paper_name: String): Bitmap {
        return BitmapFactory.decodeFile(getPaperPath(paper_name))
                .copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * 从草稿纸名获取草稿纸缩略图
     * @param paper_name
     * *
     * @return
     */
    fun getPaperThumbNail(paper_name: String): Bitmap? {
        if (FileUtils.isFileExist(PaperFileUtils.getPaperThumbNailPath(paper_name))) {
            return BitmapFactory.decodeFile(getPaperThumbNailPath(paper_name))
                    .copy(Bitmap.Config.ARGB_8888, true)
        } else {
            return null
        }
    }

    /**
     * 保存草稿纸到SD卡中
     * @param bitmap
     * *
     * @param paper_name
     */
    fun savePaper(bitmap: Bitmap, paper_name: String) {
        ImageUtils.saveImageToSD(bitmap,
                MainApplication.mCachePath +
                        paper_name + ".png")
        ImageUtils.saveImageToSD(ImageUtils.resizeImage(bitmap, 200, 400),
                MainApplication.mCachePathComp +
                        paper_name + ".png")
    }

    /**
     * 从SD卡中删除草稿纸
     * @param paper_name
     */
    fun deletePaper(paper_name: String?) {
        if (paper_name == null || paper_name.length == 0) {
            return
        }
        FileUtils.deleteFile(getPaperPath(paper_name))
        FileUtils.deleteFile(getPaperThumbNailPath(paper_name))
    }

    /**
     * 从SD卡中读取文件名列表
     * @return
     */
    fun readPaperList(): MutableList<String> {
        val mPaperList = ArrayList<String>()
        val f = File(MainApplication.mCachePathComp)
        val files = f.listFiles()
        if (files != null) {
            for (inFile in files) {
                if (!inFile.isDirectory()) {
                    mPaperList.add(PaperFileUtils.getPaperName(inFile.getPath()))
                }
            }
        }
        return mPaperList
    }

    /**
     * 读取文件列表，并按照天归类
     */
    fun readPaperListGroup(): MutableList<PaperGroup> {
        val mPaperList = ArrayList<String>()
        val f = File(MainApplication.mCachePathComp)
        val files = f.listFiles()
        val mPaperGroupMap = mutableMapOf<Long, PaperGroup>()
        if (files != null) {
            for (inFile in files) {
                if (!inFile.isDirectory()) {
                    val timeOfDay = (inFile.lastModified() / Contants.DAY_SPAN) * Contants.DAY_SPAN
                    if (mPaperGroupMap.containsKey(timeOfDay)) {
                        mPaperGroupMap.get(timeOfDay)?.paperList?.add(PaperFileUtils.getPaperName(inFile.getPath()))
                    } else {
                        val paperGroup = PaperGroup(timeOfDay)
                        paperGroup.paperList.add(PaperFileUtils.getPaperName(inFile.getPath()))
                        mPaperGroupMap.put(timeOfDay, paperGroup)
                    }
                }
            }
        }
        val mPaperGroupList = mutableListOf<PaperGroup>()
        for (value in mPaperGroupMap.values) {
            mPaperGroupList.add(value)
        }
        mPaperGroupList.sortedByDescending {
            it.timeOfData
        }
        return mPaperGroupList
    }
}
