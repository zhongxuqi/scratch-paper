package com.musketeer.scratchpaper.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muskeeter.base.acitivity.BaseFragmentActivity
import com.muskeeter.base.fragment.BaseSupportFragment
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.settings.SettingsActivity
import com.musketeer.scratchpaper.config.Config

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class MyFragment: BaseSupportFragment() {
    companion object {
        val TAG = "MyFragment"
    }

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) {
        BaseView = inflater?.inflate(R.layout.fragment_my, null)
    }

    override fun initView() {

    }

    override fun initEvent() {
        findViewById(R.id.goto_settings)?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent()
                intent.setClass(activity, SettingsActivity::class.java)
                activity.startActivityForResult(intent, Config.ACTION_CHANGE_SETTINGS)
            }
        })
    }

    override fun initData() {

    }
}