package com.musketeer.scratchpaper.bean

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class PaperGroup(timeOfData: Long) {
    val timeOfData: Long
    val paperList = mutableListOf<String>()

    init {
        this.timeOfData = timeOfData
    }
}