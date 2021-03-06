package com.musketeer.scratchpaper.activity.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView

import com.musketeer.scratchpaper.R
import com.muskeeter.base.acitivity.BaseActivity
import com.musketeer.scratchpaper.adapter.SelectListAdapter
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.SharePreferenceUtils
import com.umeng.analytics.MobclickAgent

import java.util.ArrayList

class SetPaperActivity : BaseActivity(), OnItemClickListener {

    private var mPaperBGListView: GridView? = null
    private var mAdapter: SelectListAdapter? = null
    private var mPaperSmallIdsList: ArrayList<Int> = ArrayList()
    private var mPaperIdsList: MutableList<Int>? = null

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_set_paper)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.icon_small)
        menuInflater.inflate(R.menu.set_paper, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        // TODO Auto-generated method stub
        mPaperBGListView = findViewById(R.id.image_gridlist) as GridView
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
        mPaperBGListView!!.onItemClickListener = this
    }

    override fun initData() {
        // TODO Auto-generated method stub

        //read paper images
        mPaperIdsList = ArrayList<Int>()
        var images = resources.obtainTypedArray(R.array.paper_images)
        for (i in 0..images.length() - 1) {
            mPaperIdsList!!.add(images.getResourceId(i, 0))
        }
        images.recycle()
        mPaperSmallIdsList = ArrayList<Int>()
        images = resources.obtainTypedArray(R.array.paper_images_small)
        for (i in 0..images.length() - 1) {
            mPaperSmallIdsList!!.add(images.getResourceId(i, 0))
        }
        images.recycle()

        mAdapter = SelectListAdapter(this, mPaperSmallIdsList)
        mAdapter!!.selectId = AppPreferenceUtils.getPaperSmallChoose(this)
        mPaperBGListView!!.adapter = mAdapter
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int,
                             id: Long) {
        // TODO Auto-generated method stub
        mAdapter!!.selectId = mPaperSmallIdsList[position]
        SharePreferenceUtils.putInt(this, SharePreferenceConfig.PAPER_SMALL, mPaperSmallIdsList[position])
        SharePreferenceUtils.putInt(this, SharePreferenceConfig.PAPER, mPaperIdsList!![position])
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
