package com.musketeer.scratchpaper.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.settings.SettingsActivity
import com.musketeer.scratchpaper.common.Contants
import com.musketeer.scratchpaper.utils.LogUtils
import com.muskeeter.base.acitivity.BaseFragmentActivity
import com.musketeer.scratchpaper.adapter.FragmentAdapter
import com.musketeer.scratchpaper.fragment.MainFragment
import com.qq.e.ads.interstitial.InterstitialAD
import com.qq.e.ads.interstitial.InterstitialADListener
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.UMShareAPI

class MainActivity : BaseFragmentActivity(){
    companion object {
        private val TAG = "MainActivity"
        private val CHANGE_SETTINGS = 3
    }

    private val viewPager: ViewPager by lazy {
        findViewById(R.id.view_paper) as ViewPager
    }
    private val mainFragment = MainFragment()
    private val fragmentList = mutableListOf<Fragment>()

    private var closeTime: Long = 0
    private var interstitialAD: InterstitialAD? = null

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
    }

    override fun initView() {

    }

    override fun initEvent() {

    }

    override fun initData() {
        fragmentList.add(mainFragment)
        viewPager.adapter = FragmentAdapter(fragmentManager, fragmentList)
        viewPager.setCurrentItem(0)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                LogUtils.d(TAG, "onPageSelected: ${position}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.setting -> startActivityForResult(SettingsActivity::class.java, CHANGE_SETTINGS)
            R.id.help -> startActivity(HelpActivity::class.java)
            android.R.id.home -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    public override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    public override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            var ret: Boolean = true
            if (System.currentTimeMillis() - closeTime > 2000) {
                if (interstitialAD != null) {
                    ret = false
                }
                closeTime = System.currentTimeMillis()
                showCustomToast(R.string.close_hint)
            } else {
                interstitialAD?.closePopupWindow()
                finish()
            }
            if (interstitialAD == null) {
                interstitialAD = InterstitialAD(this, Contants.AD_APPID, Contants.AD_SMALL)
                interstitialAD?.setADListener(object: InterstitialADListener{
                    override fun onADExposure() {
                        LogUtils.d(TAG, "onADExposure")
                    }

                    override fun onADOpened() {
                        LogUtils.d(TAG, "onADOpened")
                    }

                    override fun onADClosed() {
                        LogUtils.d(TAG, "onADClosed")
                        interstitialAD = null
                    }

                    override fun onADLeftApplication() {
                        LogUtils.d(TAG, "onADLeftApplication")
                    }

                    override fun onADReceive() {
                        LogUtils.d(TAG, "onADReceive")
                        interstitialAD?.show()
                    }

                    override fun onNoAD(p0: Int) {
                        LogUtils.d(TAG, "onNoAD $p0")
                    }

                    override fun onADClicked() {
                        LogUtils.d(TAG, "onADClicked")
                    }
                })
                interstitialAD?.loadAD()
            }
            return ret
        }
        return false
    }
}
