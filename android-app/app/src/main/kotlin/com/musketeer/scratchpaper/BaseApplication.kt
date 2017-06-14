package com.musketeer.scratchpaper

import android.app.Application
import android.content.Intent

/**
 * Created by zhongxuqi on 15-10-24.
 */
open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun startService(cls: Class<*>) {
        val intent = Intent()
        intent.setClass(this, cls)
        startService(intent)
    }

    companion object {
        var instance: BaseApplication? = null
    }
}
