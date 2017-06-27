/**
 * @Title: MainApplication.java
 * *
 * @Package com.musketeer.scratchpaper
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-12 下午12:50:27
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper

import android.os.Environment

import com.musketeer.scratchpaper.utils.FileUtils
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.Config
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI

import java.io.File
import java.util.HashMap

/**
 * @author zhongxuqi
 */
class MainApplication : BaseApplication() {

    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()
        instance = this

        // init umeng
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL)
        MobclickAgent.startWithConfigure(MobclickAgent.UMAnalyticsConfig(this, "56ecff3ce0f55ac331000a80", "XiaoMi"))

        iniEnv()

        // share sdk init
        PlatformConfig.setWeixin("wx27f355795896793b", "d50f410ea6ff946cef36ebe39cefb432")
        PlatformConfig.setSinaWeibo("2709929479", "7e3d71dc4b12ebca23a7305ec82cc692", "http://sns.whalecloud.com")
        PlatformConfig.setQQZone("1103577955", "4gx4VFqF6ME4dbl0")
        Config.DEBUG = true
        UMShareAPI.get(this)
    }

    /**
     * 初始化基本的变量
     */
    private fun iniEnv() {
        // TODO Auto-generated method stub
        val sdcard = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        mCachePath = sdcard + "/cache/image/"
        mCachePathComp = sdcard + "/cache/image_comp/"
        FileUtils.createExternalStoragePublicPicture()
        FileUtils.createDir(File(mCachePath))
        FileUtils.createDir(File(mCachePathComp))
    }

    companion object {
        private val TAG = "MainApplication"
        val DEFAULT_PAPER = R.mipmap.bg_paper
        val DEFAULT_PAPER_SMALL = R.mipmap.bg_paper_small
        val DEFAULT_DESK = R.mipmap.bg_desk_default
        val PAPER_MAX_UNDO = 100

        var instance: MainApplication? = null
            private set

        var mCachePath: String? = null
        var mCachePathComp: String? = null

        var errorLogPath: String? = null
        var store: MutableMap<String, Any> = HashMap()
    }

}
