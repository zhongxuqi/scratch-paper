package com.musketeer.scratchpaper.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import com.muskeeter.base.acitivity.BaseActivity

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.adapter.PaperBrowserAdapter
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.fileutils.NoteFileUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.umeng.analytics.MobclickAgent
import java.io.File

class BrowseNoteActivity : BaseActivity() {
    private var mNoteName: String = ""
    val noteBrowser: ViewPager by lazy {
        findViewById(R.id.note_browser) as ViewPager
    }
    val listInfoText : TextView by lazy {
        findViewById(R.id.list_info) as TextView
    }
    val adapter : PaperBrowserAdapter by lazy {
        PaperBrowserAdapter(this, NoteFileUtils.readSortedImageList())
    }

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_browse_note)
    }

    override fun initView() {
        supportActionBar?.hide()
        noteBrowser.adapter = adapter
    }

    override fun initEvent() {
        noteBrowser.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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
                val noteFile = v.tag as File
                val bundle = Bundle()
                bundle.putString("note_name", noteFile.name)
                val intent = Intent()
                intent.putExtras(bundle)
                intent.setClass(this@BrowseNoteActivity, EditNoteActivity::class.java)
                startActivityForResult(intent, Config.ACTION_EDIT_PAPER)
            }
        }
    }

    override fun initData() {
        //init paper content
        val bunle = intent.extras
        if (bunle != null && bunle.getString("note_name") != null) {
            mNoteName = bunle.getString("note_name")
            if (FileUtils.isFileExist(NoteFileUtils.getImagePath(mNoteName))) {
                noteBrowser.currentItem = adapter.getPaperPosition(mNoteName)
            } else {
                NoteFileUtils.deleteImage(mNoteName)
                finish()
            }
        } else {
            finish()
        }
        listInfoText.text = "${noteBrowser.currentItem+1}/${adapter.count}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Config.ACTION_EDIT_PAPER -> {
                adapter.reloadViewAt(noteBrowser.currentItem)
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
