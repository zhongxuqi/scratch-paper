package com.musketeer.scratchpaper

import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.musketeer.scratchpaper.activity.BaseActivity
import com.musketeer.scratchpaper.activity.MainActivity

class WelcomeActivity : BaseActivity() {

    private var mWelcomeView: ImageView? = null

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_welcome)
    }

    override fun initView() {
        supportActionBar!!.hide()
        mWelcomeView = findViewById(R.id.welcome_image) as ImageView
    }

    override fun initEvent() {
        Handler().postDelayed(object: Runnable{
            override fun run() {
                startActivity(MainActivity::class.java)
                finish()
            }
        }, 2000)
    }

    override fun initData() {
        mWelcomeView!!.setImageResource(R.mipmap.welcome)
    }
}
