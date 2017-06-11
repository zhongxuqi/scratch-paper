package com.musketeer.scratchpaper.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.musketeer.baselibrary.Activity.BaseActivity
import com.musketeer.scratchpaper.R
import com.umeng.analytics.MobclickAgent

class HelpActivity : BaseActivity() {

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_help)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.icon_small)
        menuInflater.inflate(R.menu.help, menu)
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

    }

    override fun initEvent() {
        // TODO Auto-generated method stub

    }

    override fun initData() {
        // TODO Auto-generated method stub

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
