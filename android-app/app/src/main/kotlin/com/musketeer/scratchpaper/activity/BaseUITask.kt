package com.musketeer.scratchpaper.activity

import android.os.Bundle

/**
 * Created by zhongxuqi on 15-10-24.
 */
interface BaseUITask {

    /**
     * 设置界面
     */
    fun setContentView(savedInstanceState: Bundle?)

    /**
     * 初始化视图
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initEvent()

    /**
     * 初始化数据
     */
    fun initData()
}
