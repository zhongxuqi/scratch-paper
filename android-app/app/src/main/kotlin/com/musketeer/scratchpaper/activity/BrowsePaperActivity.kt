package com.musketeer.scratchpaper.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.musketeer.scratchpaper.utils.SharePreferenceUtils
import com.musketeer.scratchpaper.view.TouchImageView
import com.muskeeter.base.acitivity.BaseActivity
import com.musketeer.scratchpaper.adapter.PaperBrowserAdapter
import com.musketeer.scratchpaper.config.Config
import com.umeng.analytics.MobclickAgent

class BrowsePaperActivity : BaseActivity() {

    private var mPaperName: String = ""
    val paperBrowser : ViewPager by lazy {
        findViewById(R.id.paper_browser) as ViewPager
    }
    val listInfoText : TextView by lazy {
        findViewById(R.id.list_info) as TextView
    }
    val adapter : PaperBrowserAdapter by lazy {
        PaperBrowserAdapter(this, PaperFileUtils.readSortedPaperList())
    }

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_browse_paper)
    }

    override fun initView() {
        supportActionBar?.hide()
        paperBrowser.adapter = adapter
    }

    override fun initEvent() {
        paperBrowser.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                listInfoText.text = "${position+1}/${adapter.count}"
            }
        })
    }

    override fun initData() {
        //init paper content
        val bunle = intent.extras
        if (bunle != null && bunle.getString("paper_name") != null) {
            mPaperName = bunle.getString("paper_name")
            if (FileUtils.isFileExist(PaperFileUtils.getPaperPath(mPaperName))) {
                paperBrowser.currentItem = adapter.getPaperPosition(mPaperName)
            } else {
                PaperFileUtils.deletePaper(mPaperName)
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Config.ACTION_EDIT_PAPER -> {
                initData()

                val intent = Intent()
                intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
                intent.putExtra("widget_name", PaperWidgetProvider.TAG)
                intent.putExtra("paper_name", SharePreferenceUtils.getString(this, SharePreferenceConfig.WIDGET_PAPER_NAME, ""))
                sendBroadcast(intent)
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
