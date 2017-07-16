package com.musketeer.scratchpaper.activity

import com.muskeeter.base.acitivity.BaseActivity

import com.musketeer.scratchpaper.activity.base.BaseBrowseActivity
import com.musketeer.scratchpaper.fileutils.BaseFileUtils
import com.musketeer.scratchpaper.fileutils.NoteFileUtils

class BrowseNoteActivity : BaseBrowseActivity() {
    override fun bundleKey(): String {
        return "note_name"
    }

    override fun editActivity(): Class<out BaseActivity> {
        return EditNoteActivity::class.java
    }

    override fun fileUtils(): BaseFileUtils {
        return NoteFileUtils
    }
}
