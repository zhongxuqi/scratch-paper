package com.musketeer.scratchpaper.activity.base

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import com.muskeeter.base.acitivity.BaseActivity
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.adapter.FileBrowserAdapter
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.fileutils.BaseFileUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.umeng.analytics.MobclickAgent
import java.io.File

/**
 * Created by zhongxuqi on 16/07/2017.
 */
abstract class BaseBrowseActivity : BaseActivity() {
    private var mFileName: String = ""
    val fileBrowser: ViewPager by lazy {
        findViewById(R.id.file_browser) as ViewPager
    }
    val listInfoText : TextView by lazy {
        findViewById(R.id.list_info) as TextView
    }
    val adapter : FileBrowserAdapter by lazy {
        FileBrowserAdapter(this, fileUtils().readSortedImageList())
    }

    abstract fun bundleKey(): String
    abstract fun editActivity(): Class<out BaseActivity>
    abstract fun fileUtils(): BaseFileUtils

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_browse)
    }

    override fun initView() {
        supportActionBar?.hide()
        fileBrowser.adapter = adapter
    }

    override fun initEvent() {
        fileBrowser.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                listInfoText.text = "${position+1}/${adapter.count}"
            }
        })
        adapter.listener = object: View.OnClickListener{
            override fun onClick(v: View) {
                val fileFile = v.tag as File
                val bundle = Bundle()
                bundle.putString(bundleKey(), fileFile.name)
                val intent = Intent()
                intent.putExtras(bundle)
                intent.setClass(this@BaseBrowseActivity, editActivity())
                startActivityForResult(intent, Config.ACTION_EDIT_FILE)
            }
        }
    }

    override fun initData() {
        val bunle = intent.extras
        if (bunle != null && bunle.getString(bundleKey()) != null) {
            mFileName = bunle.getString(bundleKey())
            if (FileUtils.isFileExist(fileUtils().getImagePath(mFileName))) {
                fileBrowser.currentItem = adapter.getFilePosition(mFileName)
            } else {
                fileUtils().deleteImage(mFileName)
                finish()
            }
        } else {
            finish()
        }
        listInfoText.text = "${fileBrowser.currentItem+1}/${adapter.count}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Config.ACTION_EDIT_FILE -> {
                adapter.reloadViewAt(fileBrowser.currentItem)
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    public override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}