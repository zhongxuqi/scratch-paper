package com.musketeer.scratchpaper.activity

import com.muskeeter.base.acitivity.BaseActivity

import com.musketeer.scratchpaper.activity.base.BaseBrowseActivity
import com.musketeer.scratchpaper.fileutils.BaseFileUtils
import com.musketeer.scratchpaper.fileutils.ImageFileUtils

class BrowseImageActivity : BaseBrowseActivity() {
    override fun bundleKey(): String {
        return "image_name"
    }

    override fun editActivity(): Class<out BaseActivity> {
        return EditImageActivity::class.java
    }

    override fun fileUtils(): BaseFileUtils {
        return ImageFileUtils
    }
}
