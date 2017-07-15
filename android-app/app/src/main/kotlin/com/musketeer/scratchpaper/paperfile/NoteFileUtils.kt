package com.musketeer.scratchpaper.paperfile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.bean.PaperGroup
import com.musketeer.scratchpaper.utils.FileUtils
import com.musketeer.scratchpaper.utils.ImageUtils
import java.io.File
import java.util.*

/**
 * Created by zhongxuqi on 15/07/2017.
 */
object NoteFileUtils {
    val TAG = "NoteFileUtils"

    /**
     * 从便签路径获取草稿纸名
     * @param note_path
     * *
     * @return
     */
    fun getNoteName(note_path: String): String {
        return note_path.substring(note_path.lastIndexOf("/") + 1,
                note_path.lastIndexOf(".png"))
    }

    /**
     * 从便签名获取缩略便签路径
     * @param note_name
     * *
     * @return
     */
    fun getNoteThumbNailPath(note_name: String): String {
        if (note_name.contains(".png")) {
            return MainApplication.mCacheNotePathComp + note_name
        } else {
            return MainApplication.mCacheNotePathComp + note_name + ".png"
        }
    }

    /**
     * 从便签名获取便签路径
     * @param note_name
     * *
     * @return
     */
    fun getNotePath(note_name: String): String {
        if (note_name.indexOf(".png") >= 0) {
            return MainApplication.mCacheNotePath + note_name
        } else {
            return MainApplication.mCacheNotePath + note_name + ".png"
        }
    }

    /**
     * 从便签名获取便签位图
     * @param paper_name
     * *
     * @return
     */
    fun getNoteBitmap(note: File): Bitmap {
        return BitmapFactory.decodeFile(note.absolutePath)
                .copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * 从便签名获取便签位图
     * @param note_name
     * *
     * @return
     */
    fun getNote(note_name: String): Bitmap {
        return BitmapFactory.decodeFile(NoteFileUtils.getNotePath(note_name))
                .copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * 从便签名获取便签缩略图
     * @param note_name
     * *
     * @return
     */
    fun getNoteThumbNail(note_name: String): Bitmap? {
        if (FileUtils.isFileExist(NoteFileUtils.getNoteThumbNailPath(note_name))) {
            return BitmapFactory.decodeFile(NoteFileUtils.getNoteThumbNailPath(note_name))
                    .copy(Bitmap.Config.ARGB_8888, true)
        } else {
            return null
        }
    }

    /**
     * 保存便签到SD卡中
     * @param bitmap
     * *
     * @param note_name
     */
    fun saveNote(bitmap: Bitmap, note_name: String) {
        var formatPaperName = note_name
        if (!formatPaperName.contains(".png")) {
            formatPaperName += ".png"
        }
        ImageUtils.saveImageToSD(bitmap,
                MainApplication.mCacheNotePath + formatPaperName)
        ImageUtils.saveImageToSD(ImageUtils.resizeImage(bitmap, 800, 1600),
                MainApplication.mCacheNotePathComp + formatPaperName)
    }

    /**
     * 从SD卡中删除便签
     * @param note_name
     */
    fun deleteNote(note_name: String?) {
        if (note_name == null || note_name.isEmpty()) {
            return
        }
        FileUtils.deleteFile(NoteFileUtils.getNotePath(note_name))
        FileUtils.deleteFile(NoteFileUtils.getNoteThumbNailPath(note_name))
    }

    /**
     * 从SD卡中读取便签列表
     * @return
     */
    fun readNoteList(): MutableList<String> {
        val mNoteList = ArrayList<String>()
        val f = File(MainApplication.mCacheNotePathComp)
        val files = f.listFiles()
        if (files != null) {
            for (inFile in files) {
                if (!inFile.isDirectory()) {
                    mNoteList.add(NoteFileUtils.getNoteName(inFile.getPath()))
                }
            }
        }
        return mNoteList
    }

    /**
     * 读取便签列表，并按照天归类
     */
    fun readNoteListGroup(): MutableList<PaperGroup> {
        val f = File(MainApplication.mCacheNotePathComp)
        val files = f.listFiles()
        val mNoteGroupMap = mutableMapOf<Long, PaperGroup>()
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
                    if (mNoteGroupMap.containsKey(timeOfDay)) {
                        mNoteGroupMap.get(timeOfDay)?.paperList?.add(inFile)
                    } else {
                        val paperGroup = PaperGroup(timeOfDay)
                        paperGroup.paperList.add(inFile)
                        mNoteGroupMap.put(timeOfDay, paperGroup)
                    }
                }
            }
        }
        val mNoteGroupList = mutableListOf<PaperGroup>()
        for (value in mNoteGroupMap.values) {
            mNoteGroupList.add(value)
        }
        mNoteGroupList.sortWith(object: kotlin.Comparator<PaperGroup>{
            override fun compare(o1: PaperGroup, o2: PaperGroup): Int {
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
        for (paperGroup in mNoteGroupList) {
            paperGroup.paperList.sortWith(object : kotlin.Comparator<File> {
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
        return mNoteGroupList
    }

    fun readSortedNoteList(): MutableList<File> {
        val f = File(MainApplication.mCacheNotePath)
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
}