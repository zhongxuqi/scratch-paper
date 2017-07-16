package com.muskeeter.base.acitivity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

import butterknife.ButterKnife
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.utils.LogUtils

/**
 * Created by zhongxuqi on 15-10-24.
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener, BaseUITask {
    companion object {
        val TAG = "BaseActivity"
    }

    /**屏幕的宽度 */
    var mScreenWidth: Int = 0
    /**屏幕高度 */
    var mScreenHeight: Int = 0
    /**屏幕密度 */
    var mDensity: Float = 0.toFloat()

    private var mLoadingDialog: AlertDialog? = null
    private var loadingText: TextView? = null

    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        mScreenWidth = metric.widthPixels
        mScreenHeight = metric.heightPixels
        mDensity = metric.density

        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        loadingText = view.findViewById(R.id.loading_text) as TextView
        builder.setView(view)
        mLoadingDialog = builder.create()

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
        mToast?.cancel()
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        mToast?.show()
    }

    protected fun showCustomToast(resId: Int) {
        mToast?.cancel()
        mToast = Toast.makeText(this, resources.getString(resId), Toast.LENGTH_SHORT)
        mToast?.show()
    }

    override fun onClick(v: View) {

    }

    /**
     * 设置LoadingDialog并显示
     * @param resId
     */
    protected fun showLoadingDialog(resId: Int) {
        mLoadingDialog!!.setCancelable(true)
        loadingText!!.setText(resId)
        mLoadingDialog!!.show()
    }

    protected fun showLoadingDialog(message: String) {
        mLoadingDialog!!.setCancelable(true)
        loadingText!!.text = message
        mLoadingDialog!!.show()
    }

    protected fun showLoadingDialogNotCancel(resId: Int) {
        mLoadingDialog!!.setCancelable(false)
        loadingText!!.setText(resId)
        mLoadingDialog!!.show()
    }

    protected fun showLoadingDialogNotCancel(message: String) {
        mLoadingDialog!!.setCancelable(false)
        loadingText!!.text = message
        mLoadingDialog!!.show()
    }

    /**
     * 关闭LoadingDialog
     */
    protected fun dismissLoadingDialog() {
        mLoadingDialog!!.dismiss()
    }
}
