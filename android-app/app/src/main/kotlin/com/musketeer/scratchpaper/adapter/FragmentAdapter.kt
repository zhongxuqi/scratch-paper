package com.musketeer.scratchpaper.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class FragmentAdapter(fragmentManager: FragmentManager, fragmentList: List<Fragment>): FragmentPagerAdapter(fragmentManager) {
    val fragmentList: List<Fragment>

    init {
        this.fragmentList = fragmentList
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}