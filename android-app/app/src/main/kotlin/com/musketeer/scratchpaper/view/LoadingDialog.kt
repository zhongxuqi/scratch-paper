package com.musketeer.scratchpaper.view

import android.app.Dialog
import android.content.Context
import android.widget.TextView

import com.musketeer.scratchpaper.R

class LoadingDialog(context: Context) : Dialog(context, R.style.BaseDialog) {
    private var rotateImage: RotateImageView? = null
    private var mMessageText: TextView? = null

    init {
        // TODO Auto-generated constructor stub
        init()
    }

    fun init() {
        setContentView(R.layout.base_loadingdialog)
        initView()
    }

    fun initView() {
        rotateImage = findViewById(R.id.loading_image) as RotateImageView
        mMessageText = findViewById(R.id.content) as TextView
    }

    /**
     * 设置文字显示
     * @param content
     */
    fun setMessage(content: String) {
        mMessageText!!.text = content
    }

    fun setMessage(resId: Int) {
        mMessageText!!.text = context.resources.getString(resId)
    }

}
