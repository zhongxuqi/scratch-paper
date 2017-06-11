package com.musketeer.scratchpaper.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.musketeer.baselibrary.Activity.BaseActivity
import com.musketeer.baselibrary.util.SharePreferenceUtils
import com.musketeer.baselibrary.view.TouchImageView
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.umeng.analytics.MobclickAgent

class BrowsePaperActivity : BaseActivity() {

    private var mPaperBrowser: TouchImageView? = null
    private var mPaperName: String = ""

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_browse_paper)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.icon_small)
        menuInflater.inflate(R.menu.browse_paper, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.edit -> {
                val bundle = Bundle()
                bundle.putString("paper_name", mPaperName)
                startActivityForResult(EditPaperActivity::class.java, bundle, EDIT_PAPER)
            }
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        // TODO Auto-generated method stub
        mPaperBrowser = findViewById(R.id.paper_browser) as TouchImageView
        mPaperBrowser!!.minZoom = .5f
        mPaperBrowser!!.maxZoom = 10f
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
    }

    override fun initData() {
        // TODO Auto-generated method stub
        //init paper content
        val bunle = intent.extras
        if (bunle != null && bunle.getString("paper_name") != null) {
            mPaperName = bunle.getString("paper_name")
            if (FileUtils.isFileExist(PaperFileUtils.getPaperPath(mPaperName))) {
                mPaperBrowser!!.setImageBitmap(PaperFileUtils.getPaper(mPaperName))
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
            EDIT_PAPER -> {
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

    companion object {
        private val EDIT_PAPER = 1
    }
}
