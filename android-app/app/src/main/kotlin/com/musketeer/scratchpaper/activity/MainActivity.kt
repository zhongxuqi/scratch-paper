package com.musketeer.scratchpaper.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.ImageView
import android.widget.TextView

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.settings.SettingsActivity
import com.musketeer.scratchpaper.common.Contants
import com.musketeer.scratchpaper.utils.LogUtils
import com.muskeeter.base.acitivity.BaseFragmentActivity
import com.musketeer.scratchpaper.adapter.FragmentAdapter
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.fragment.MainFragment
import com.musketeer.scratchpaper.fragment.MyFragment
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
    private val myFragment = MyFragment()
    private val fragmentList = mutableListOf<Fragment>()

    private var closeTime: Long = 0
    private var interstitialAD: InterstitialAD? = null

    private val mTabIconPaper: ImageView by lazy {
        findViewById(R.id.tab_icon_paper) as ImageView
    }

    private val mTabTitlePaper: TextView by lazy {
        findViewById(R.id.tab_title_paper) as TextView
    }

    private val mTabIconMy: ImageView by lazy {
        findViewById(R.id.tab_icon_my) as ImageView
    }

    private val mTabTitleMy: TextView by lazy {
        findViewById(R.id.tab_title_my) as TextView
    }

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
    }

    override fun initView() {

    }

    override fun initEvent() {
        findViewById(R.id.tab_paper).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                viewPager.setCurrentItem(0)
            }
        })
        findViewById(R.id.tab_my).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                viewPager.setCurrentItem(1)
            }
        })
    }

    override fun initData() {
        fragmentList.add(mainFragment)
        fragmentList.add(myFragment)
        viewPager.adapter = FragmentAdapter(fragmentManager, fragmentList)
        viewPager.setCurrentItem(0)
        selectTab(0)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                selectTab(position)
            }
        })
    }

    fun selectTab(position: Int) {
        mTabIconPaper.setColorFilter(resources.getColor(R.color.tab_default))
        mTabTitlePaper.setTextColor(R.color.tab_default)

        mTabIconMy.setColorFilter(resources.getColor(R.color.tab_default))
        mTabTitleMy.setTextColor(R.color.tab_default)
        when(position) {
            0 -> {
                mTabIconPaper.setColorFilter(resources.getColor(R.color.tab_active))
                mTabTitlePaper.setTextColor(R.color.tab_active)
            }
            1 -> {
                mTabIconMy.setColorFilter(resources.getColor(R.color.tab_active))
                mTabTitleMy.setTextColor(R.color.tab_active)
            }
        }
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
        mainFragment.onActivityResult(requestCode, resultCode, data)
    }

    fun refreshMainFragment() {
        mainFragment.onActivityResult(Config.ACTION_CHANGE_SETTINGS, 0, null)
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
