package com.musketeer.scratchpaper.activity.settings

import android.content.res.TypedArray
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView

import com.musketeer.baselibrary.Activity.BaseActivity
import com.musketeer.baselibrary.util.SharePreferenceUtils
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.adapter.SelectListAdapter
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.umeng.analytics.MobclickAgent

import java.util.ArrayList

class SetDeskActivity : BaseActivity(), OnItemClickListener {

    private var mDeskBGListView: GridView? = null
    private var mAdapter: SelectListAdapter? = null
    private var mDeskIdsList: MutableList<Int> = ArrayList()

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_set_desk)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.icon_small)
        menuInflater.inflate(R.menu.set_desk, menu)
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
        mDeskBGListView = findViewById(R.id.image_gridlist) as GridView
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
        mDeskBGListView!!.onItemClickListener = this
    }

    override fun initData() {
        // TODO Auto-generated method stub

        //read desk images
        mDeskIdsList = ArrayList<Int>()
        val images = resources.obtainTypedArray(R.array.desk_images)
        for (i in 0..images.length() - 1) {
            mDeskIdsList!!.add(images.getResourceId(i, 0))
        }
        images.recycle()
        mAdapter = SelectListAdapter(this, mDeskIdsList)
        mAdapter!!.selectId = AppPreferenceUtils.getDeskChoose(this)
        mDeskBGListView!!.adapter = mAdapter
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int,
                             id: Long) {
        // TODO Auto-generated method stub
        mAdapter!!.selectId = mDeskIdsList[position]
        SharePreferenceUtils.putInt(this, SharePreferenceConfig.DESK, mDeskIdsList!![position])
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
