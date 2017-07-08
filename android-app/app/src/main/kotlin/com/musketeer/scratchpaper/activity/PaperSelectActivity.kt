package com.musketeer.scratchpaper.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.adapter.PaperListAdapter
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.LogUtils
import com.musketeer.scratchpaper.utils.SharePreferenceUtils
import com.muskeeter.base.acitivity.BaseActivity
import com.umeng.analytics.MobclickAgent

class PaperSelectActivity : BaseActivity(), OnItemClickListener {

    private var mSavedPaperList: GridView? = null
    private var mAdapter: PaperListAdapter? = null
    private var mPaperList: MutableList<String> = ArrayList()

    override fun setContentView(savedInstanceState: Bundle?) {
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.activity_paper_select)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.paper_select, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        // TODO Auto-generated method stub
        mSavedPaperList = findViewById(R.id.paper_gridlist) as GridView
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
        mSavedPaperList!!.onItemClickListener = this
    }

    override fun initData() {
        // TODO Auto-generated method stub
        mSavedPaperList!!.numColumns = AppPreferenceUtils.getRowNum(this)

        //read paper files
        mPaperList = PaperFileUtils.readPaperList()
        mAdapter = PaperListAdapter(this, mPaperList)
        mSavedPaperList!!.adapter = mAdapter
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        // TODO Auto-generated method stub
        val intent = Intent()
        intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
        LogUtils.d("zxq", "onItemClick: " + PaperWidgetProvider.TAG)
        intent.putExtra("widget_name", PaperWidgetProvider.TAG)
        intent.putExtra("paper_name", mPaperList[position])
        sendBroadcast(intent)

        SharePreferenceUtils.putString(this, SharePreferenceConfig.WIDGET_PAPER_NAME, mPaperList!![position])
        setResult(Activity.RESULT_OK)
        finish()
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
