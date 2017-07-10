package com.musketeer.scratchpaper.bean

import java.io.File

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class PaperGroup(timeOfData: Long) {
    val timeOfData: Long
    val paperList = mutableListOf<File>()

    init {
        this.timeOfData = timeOfData
    }
}