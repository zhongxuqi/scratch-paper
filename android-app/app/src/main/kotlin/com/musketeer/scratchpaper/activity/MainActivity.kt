package com.musketeer.scratchpaper.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.ImageView
import android.widget.TextView

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.common.Contants
import com.musketeer.scratchpaper.utils.LogUtils
import com.muskeeter.base.acitivity.BaseFragmentActivity
import com.musketeer.scratchpaper.adapter.FragmentAdapter
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.fragment.ImageFragment
import com.musketeer.scratchpaper.fragment.MainFragment
import com.musketeer.scratchpaper.fragment.NoteFragment
import com.qq.e.ads.interstitial.InterstitialAD
import com.qq.e.ads.interstitial.InterstitialADListener
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.UMShareAPI

class MainActivity : BaseFragmentActivity(){
    companion object {
        private val TAG = "MainActivity"
    }

    private val viewPager: ViewPager by lazy {
        findViewById(R.id.view_paper) as ViewPager
    }
    private val mainFragment = MainFragment()
    private val noteFragment = NoteFragment()
    private val imageFragment = ImageFragment()
    private val fragmentList = mutableListOf<Fragment>()

    private var closeTime: Long = 0
    private var interstitialAD: InterstitialAD? = null

    private val mTabIconPaper: ImageView by lazy {
        findViewById(R.id.tab_icon_paper) as ImageView
    }

    private val mTabTitlePaper: TextView by lazy {
        findViewById(R.id.tab_title_paper) as TextView
    }

    private val mTabIconNote: ImageView by lazy {
        findViewById(R.id.tab_icon_note) as ImageView
    }

    private val mTabTitleNote: TextView by lazy {
        findViewById(R.id.tab_title_note) as TextView
    }

    private val mTabIconImage: ImageView by lazy {
        findViewById(R.id.tab_icon_image) as ImageView
    }

    private val mTabTitleImage: TextView by lazy {
        findViewById(R.id.tab_title_image) as TextView
    }

    var mDialog: AlertDialog? = null

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
    }

    override fun initView() {

        // 展示easypass广告
        mDialog?.dismiss()
        val builder = AlertDialog.Builder(this)
        val contentView = LayoutInflater.from(this).inflate(R.layout.ad_easypass, null)
        builder.setView(contentView)
        contentView.findViewById(R.id.ad_easypass).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val uri = Uri.parse("https://www.easypass.tech")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        })
        mDialog = builder.create()
        mDialog?.show()
    }

    override fun initEvent() {
        findViewById(R.id.tab_paper).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                viewPager.setCurrentItem(0)
            }
        })
        findViewById(R.id.tab_note).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                viewPager.setCurrentItem(1)
            }
        })
        findViewById(R.id.tab_image).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                viewPager.setCurrentItem(2)
            }
        })
    }

    override fun initData() {
        fragmentList.add(mainFragment)
        fragmentList.add(noteFragment)
        fragmentList.add(imageFragment)
        viewPager.adapter = FragmentAdapter(supportFragmentManager, fragmentList)
        viewPager.offscreenPageLimit = 2
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
        mTabTitlePaper.setTextColor(resources.getColor(R.color.tab_default))

        mTabIconNote.setColorFilter(resources.getColor(R.color.tab_default))
        mTabTitleNote.setTextColor(resources.getColor(R.color.tab_default))

        mTabIconImage.setColorFilter(resources.getColor(R.color.tab_default))
        mTabTitleImage.setTextColor(resources.getColor(R.color.tab_default))
        when(position) {
            0 -> {
                mTabIconPaper.setColorFilter(resources.getColor(R.color.tab_active))
                mTabTitlePaper.setTextColor(resources.getColor(R.color.tab_active))
            }
            1 -> {
                mTabIconNote.setColorFilter(resources.getColor(R.color.tab_active))
                mTabTitleNote.setTextColor(resources.getColor(R.color.tab_active))
            }
            2 -> {
                mTabIconImage.setColorFilter(resources.getColor(R.color.tab_active))
                mTabTitleImage.setTextColor(resources.getColor(R.color.tab_active))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        mainFragment.onActivityResult(requestCode, resultCode, data)
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
//                if (interstitialAD != null) {
//                    ret = false
//                }
                closeTime = System.currentTimeMillis()
                showCustomToast(R.string.close_hint)
            } else {
//                interstitialAD?.closePopupWindow()
                finish()
            }
//            if (interstitialAD == null) {
//                interstitialAD = InterstitialAD(this, Contants.AD_APPID, Contants.AD_SMALL)
//                interstitialAD?.setADListener(object: InterstitialADListener{
//                    override fun onADExposure() {
//                        LogUtils.d(TAG, "onADExposure")
//                    }
//
//                    override fun onADOpened() {
//                        LogUtils.d(TAG, "onADOpened")
//                    }
//
//                    override fun onADClosed() {
//                        LogUtils.d(TAG, "onADClosed")
//                        interstitialAD = null
//                    }
//
//                    override fun onADLeftApplication() {
//                        LogUtils.d(TAG, "onADLeftApplication")
//                    }
//
//                    override fun onADReceive() {
//                        LogUtils.d(TAG, "onADReceive")
//                        interstitialAD?.show()
//                    }
//
//                    override fun onNoAD(p0: Int) {
//                        LogUtils.d(TAG, "onNoAD $p0")
//                    }
//
//                    override fun onADClicked() {
//                        LogUtils.d(TAG, "onADClicked")
//                    }
//                })
//                interstitialAD?.loadAD()
//            }
            return ret
        }
        return false
    }
}
