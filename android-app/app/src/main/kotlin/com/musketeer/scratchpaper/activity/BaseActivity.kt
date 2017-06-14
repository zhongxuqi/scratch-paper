package com.musketeer.scratchpaper.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast

import butterknife.ButterKnife
import com.musketeer.scratchpaper.utils.LogUtils

/**
 * Created by zhongxuqi on 15-10-24.
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener, BaseUITask {

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
        windowManager.defaultDisplay.getMetrics(metric)
        mScreenWidth = metric.widthPixels
        mScreenHeight = metric.heightPixels
        mDensity = metric.density

        setContentView(savedInstanceState)
        ButterKnife.bind(this)
        initView()
        initEvent()
        initData()
    }

    protected fun startActivity(cls: Class<*>) {
        val intent = Intent()
        intent.setClass(this, cls)
        startActivity(intent)
    }

    protected fun startActivity(cls: Class<*>, bundle: Bundle) {
        val intent = Intent()
        intent.putExtras(bundle)
        intent.setClass(this, cls)
        startActivity(intent)
    }

    protected fun startActivityForResult(cls: Class<*>, requestCode: Int) {
        val intent = Intent()
        intent.setClass(this, cls)
        super.startActivityForResult(intent, requestCode)
    }

    protected fun startActivityForResult(cls: Class<*>, bundle: Bundle, requestCode: Int) {
        val intent = Intent()
        intent.putExtras(bundle)
        intent.setClass(this, cls)
        super.startActivityForResult(intent, requestCode)
    }

    protected fun showCustomDebug(text: String) {
        LogUtils.d(TAG, text)
    }

    protected fun showCustomToast(text: String) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        mToast!!.show()
    }

    protected fun showCustomToast(resId: Int) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this, resources.getString(resId), Toast.LENGTH_SHORT)
        mToast!!.show()
    }

    override fun onClick(v: View) {

    }

    companion object {
        val TAG = "Musketeer_BaseActivity"
    }
}
