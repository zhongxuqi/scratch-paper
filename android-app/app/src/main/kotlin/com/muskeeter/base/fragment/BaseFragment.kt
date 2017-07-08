package com.muskeeter.base.fragment

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.muskeeter.base.acitivity.BaseActivity
import com.muskeeter.base.acitivity.BaseFragmentUITask
import com.muskeeter.base.acitivity.BaseUITask
import com.musketeer.scratchpaper.utils.LogUtils

/**
 * Created by zhongxuqi on 08/07/2017.
 */

abstract class BaseFragment: Fragment(), View.OnClickListener, BaseFragmentUITask {
    companion object {
        val TAG = "BaseFragment"
    }

    protected var BaseView: View? = null

    /**屏幕的宽度 */
    protected var mScreenWidth: Int = 0
    /**屏幕高度 */
    protected var mScreenHeight: Int = 0
    /**屏幕密度 */
    protected var mDensity: Float = 0.toFloat()

    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val metric = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metric)
        mScreenWidth = metric.widthPixels
        mScreenHeight = metric.heightPixels
        mDensity = metric.density
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setContentView(inflater, container, savedInstanceState)
        initView()
        initEvent()
        initData()
        return BaseView
    }

    protected fun findViewById(id: Int): View? {
        return BaseView?.findViewById(id)
    }

    protected fun startActivity(cls: Class<*>) {
        val intent = Intent()
        intent.setClass(activity, cls)
        startActivity(intent)
    }

    protected fun startActivity(cls: Class<*>, bundle: Bundle) {
        val intent = Intent()
        intent.putExtras(bundle)
        intent.setClass(activity, cls)
        startActivity(intent)
    }

    protected fun startActivityForResult(cls: Class<*>, requestCode: Int) {
        val intent = Intent()
        intent.setClass(activity, cls)
        super.startActivityForResult(intent, requestCode)
    }

    protected fun startActivityForResult(cls: Class<*>, bundle: Bundle, requestCode: Int) {
        val intent = Intent()
        intent.putExtras(bundle)
        intent.setClass(activity, cls)
        super.startActivityForResult(intent, requestCode)
    }

    protected fun showCustomDebug(text: String) {
        LogUtils.d(BaseActivity.TAG, text)
    }

    protected fun showCustomToast(text: String) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT)
        mToast!!.show()
    }

    protected fun showCustomToast(resId: Int) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(activity, resources.getString(resId), Toast.LENGTH_SHORT)
        mToast!!.show()
    }

    override fun onClick(v: View) {

    }
}
