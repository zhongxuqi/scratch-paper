/**
 * @Title: RotateImageView.java
 * *
 * @Package com.musketeer.lib.view
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-12-10 下午9:16:14
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView

/**
 * @author zhongxuqi
 */
class RotateImageView : ImageView {
    private var mRotateAnimation: RotateAnimation? = null

    /**
     * @param context
     * *
     * @param attrs
     * *
     * @param defStyleAttr
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // TODO Auto-generated constructor stub
        init()
    }

    /**
     * @param context
     * *
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // TODO Auto-generated constructor stub
        init()
    }

    /**
     * @param context
     */
    constructor(context: Context) : super(context) {
        // TODO Auto-generated constructor stub
        init()
    }

    fun init() {
        mRotateAnimation = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateAnimation!!.fillAfter = true
        mRotateAnimation!!.duration = 1000
        mRotateAnimation!!.interpolator = LinearInterpolator()
        mRotateAnimation!!.repeatCount = Animation.INFINITE
        mRotateAnimation!!.repeatMode = Animation.RESTART
        animation = mRotateAnimation
    }

    override fun onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow()
        init()
    }

}
