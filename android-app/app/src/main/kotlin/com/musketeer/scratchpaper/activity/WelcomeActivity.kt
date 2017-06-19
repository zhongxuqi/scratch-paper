package com.musketeer.scratchpaper.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.common.Contants
import com.musketeer.scratchpaper.utils.LogUtils
import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener


class WelcomeActivity : BaseActivity() {
    companion object {
        val TAG = "WelcomeActivity"
        val REQUEST_PERMISSIONS = 0
        val handler = Handler()
    }

    private var mWelcomeView: RelativeLayout? = null
    private var mSkipButton: TextView? = null
    private var hasSkip: Boolean = false

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_welcome)
    }

    override fun initView() {
        supportActionBar!!.hide()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_PERMISSIONS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
        }
        mWelcomeView = findViewById(R.id.adcontent) as RelativeLayout
        SplashAD(this, mWelcomeView!!, View(this), Contants.AD_APPID, Contants.AD_LARGE, object: SplashADListener{
            override fun onNoAD(ecode: Int) {
                LogUtils.d(TAG, "ecode:$ecode")
            }

            override fun onADDismissed() {
                LogUtils.d(TAG, "onADDismissed")
            }

            override fun onADPresent() {
                LogUtils.d(TAG, "onADPresent")
            }

            override fun onADClicked() {
                LogUtils.d(TAG, "onADClicked")
            }

            override fun onADTick(p0: Long) {
                LogUtils.d(TAG, "onADTick:$p0")
            }
        }, 0)
        mSkipButton = findViewById(R.id.skip_button) as TextView
    }

    override fun initEvent() {
        mSkipButton?.setOnClickListener {
            start()
        }
        handler.post(CloseRunner(3))
    }

    override fun initData() {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_PERMISSIONS)
                    return
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
                    return
                }
                startActivity(WelcomeActivity::class.java)
                finish()
            }
        }
    }

    fun start() {
        synchronized(this) {
            if (!hasSkip) {
                startActivity(MainActivity::class.java)
                finish()
                this.hasSkip = true
            }
        }
    }

    inner class CloseRunner(time: Int): Runnable {
        var totalTime: Int

        init {
            totalTime = time
        }

        override fun run() {
            this.totalTime--
            this@WelcomeActivity.mSkipButton?.setText("点击跳过 (${totalTime}s)")
            if (this.totalTime > 0) {
                handler.postDelayed(this, 1000)
            } else {
                start()
            }
        }
    }
}
