/**
 * @Title: BaseDialog.java
 * *
 * @Package com.musketeer.scratchpaper.view
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-15 下午8:48:05
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView

import com.musketeer.scratchpaper.R

/**
 * @author zhongxuqi
 */
class BaseDialog
/**
 * @param context
 */
(context: Context) : Dialog(context, R.style.BaseDialog), View.OnClickListener {

    private var mTitle: TextView? = null
    private var mMainContent: RelativeLayout? = null
    private var mButton1: Button? = null
    private var mButton2: Button? = null
    private var mButton3: Button? = null

    init {
        // TODO Auto-generated constructor stub
        init()
    }

    override fun onCreate(savedInstanceState: Bundle) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)

    }

    fun init() {
        setContentView(R.layout.base_dialog)
        initView()
    }

    fun initView() {
        mTitle = findViewById(R.id.title) as TextView
        mMainContent = findViewById(R.id.main_content) as RelativeLayout
        mButton1 = findViewById(R.id.button1) as Button
        mButton2 = findViewById(R.id.button2) as Button
        mButton3 = findViewById(R.id.button3) as Button
    }

    /**
     * 设置标题
     * @param title
     */
    fun setTitle(title: String) {
        mTitle!!.text = title
    }

    /**
     * 设置button1
     * @param text
     * *
     * @param l
     */
    fun setButton1(text: String, l: View.OnClickListener) {
        mButton1!!.text = text
        mButton1!!.setOnClickListener(l)
        mButton1!!.visibility = View.VISIBLE
    }

    /**
     * 设置button2
     * @param text
     * *
     * @param l
     */
    fun setButton2(text: String, l: View.OnClickListener) {
        mButton2!!.text = text
        mButton2!!.setOnClickListener(l)
        mButton2!!.visibility = View.VISIBLE
    }

    /**
     * 设置button3
     * @param text
     * *
     * @param l
     */
    fun setButton3(text: String, l: View.OnClickListener) {
        mButton3!!.text = text
        mButton3!!.setOnClickListener(l)
        mButton3!!.visibility = View.VISIBLE
    }

    /**
     * 设置内容
     * @param layoutId
     */
    fun setDialogContentView(layoutId: Int) {
        val view = layoutInflater.inflate(layoutId, null)
        val params = RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        view.layoutParams = params
        mMainContent!!.removeAllViews()
        mMainContent!!.addView(view)
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub

    }

}
