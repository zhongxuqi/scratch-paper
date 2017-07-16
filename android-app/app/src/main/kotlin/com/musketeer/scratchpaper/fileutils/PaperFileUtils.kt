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
package com.musketeer.scratchpaper.fileutils

import com.musketeer.scratchpaper.MainApplication

/**
 * @author zhongxuqi
 */
object PaperFileUtils: BaseFileUtils() {
    val TAG = "PaperFileUtils"

    override fun getCachePath(): String {
        return MainApplication.mCachePath
    }

    override fun getCachePathComp(): String {
        return MainApplication.mCachePathComp
    }
}
