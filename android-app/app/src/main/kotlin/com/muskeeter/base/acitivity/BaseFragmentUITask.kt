package com.muskeeter.base.acitivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by zhongxuqi on 08/07/2017.
 */
interface BaseFragmentUITask {

    /**
     * 设置界面
     */
    fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)

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