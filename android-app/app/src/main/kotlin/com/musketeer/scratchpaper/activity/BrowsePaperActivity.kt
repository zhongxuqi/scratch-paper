package com.musketeer.scratchpaper.activity

import com.muskeeter.base.acitivity.BaseActivity
import com.musketeer.scratchpaper.activity.base.BaseBrowseActivity
import com.musketeer.scratchpaper.fileutils.BaseFileUtils
import com.musketeer.scratchpaper.fileutils.PaperFileUtils

class BrowsePaperActivity : BaseBrowseActivity() {
    override fun bundleKey(): String {
        return "paper_name"
    }

    override fun editActivity(): Class<out BaseActivity> {
        return EditPaperActivity::class.java
    }

    override fun fileUtils(): BaseFileUtils {
        return PaperFileUtils
    }
}
