package com.musketeer.scratchpaper.fileutils

import com.musketeer.scratchpaper.MainApplication

/**
 * Created by zhongxuqi on 15/07/2017.
 */
object NoteFileUtils: BaseFileUtils() {
    val TAG = "NoteFileUtils"

    override fun getCachePath(): String {
        return MainApplication.mCacheNotePath
    }

    override fun getCachePathComp(): String {
        return MainApplication.mCacheNotePathComp
    }
}
