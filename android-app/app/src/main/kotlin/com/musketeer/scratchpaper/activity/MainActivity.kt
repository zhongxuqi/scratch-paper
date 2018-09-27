package com.musketeer.scratchpaper.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.miui.zeus.mimo.sdk.MimoSdk

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.utils.LogUtils
import com.muskeeter.base.acitivity.BaseFragmentActivity
import com.musketeer.scratchpaper.activity.WelcomeActivity.Companion.REQUEST_PERMISSIONS
import com.musketeer.scratchpaper.adapter.FragmentAdapter
import com.musketeer.scratchpaper.fragment.ImageFragment
import com.musketeer.scratchpaper.fragment.MainFragment
import com.musketeer.scratchpaper.fragment.NoteFragment
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.UMShareAPI
import com.xiaomi.ad.common.pojo.AdType
import com.umeng.socialize.utils.DeviceConfig.context
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory
import com.miui.zeus.mimo.sdk.ad.IAdWorker
import com.miui.zeus.mimo.sdk.listener.MimoAdListener


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
    var myAdWorker: IAdWorker? = null

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
    }

    override fun initView() {
        window.statusBarColor = Color.BLACK

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
                return
            }
        }
        showAD()
    }

    override fun initEvent() {
        findViewById<View>(R.id.tab_paper).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                viewPager.setCurrentItem(0)
            }
        })
        findViewById<View>(R.id.tab_note).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                viewPager.setCurrentItem(1)
            }
        })
        findViewById<View>(R.id.tab_image).setOnClickListener(object: View.OnClickListener{
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
        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                selectTab(position)
            }
        })
    }

    fun showAD() {
        if (!MimoSdk.isSdkReady()) {
            Handler().postDelayed(object: Runnable{
                override fun run() {
                    this@MainActivity.showAD()
                }
            }, 1000)
            return
        }
        myAdWorker = AdWorkerFactory.getAdWorker(this, window.decorView as ViewGroup, object: MimoAdListener{
            override fun onAdFailed(p0: String?) {
                LogUtils.d(TAG, "onAdFailed")
            }

            override fun onAdDismissed() {
                LogUtils.d(TAG, "onAdDismissed")
            }

            override fun onAdPresent() {
                LogUtils.d(TAG, "onAdPresent")
            }

            override fun onAdClick() {
                LogUtils.d(TAG, "onAdClick")
            }

            override fun onStimulateSuccess() {
                LogUtils.d(TAG, "onStimulateSuccess")
            }

            override fun onAdLoaded(p0: Int) {
                myAdWorker?.show()
            }
        }, AdType.AD_INTERSTITIAL)
        myAdWorker?.load("4b03e72a3ff5c9faf676bd9f0e1e8266") //POSITION_ID: 广告位ID

        // 展示easypass广告
//        mDialog?.dismiss()
//        val builder = AlertDialog.Builder(this)
//        val contentView = LayoutInflater.from(this).inflate(R.layout.ad_easypass, null)
//        builder.setView(contentView)
//        contentView.findViewById<View>(R.id.ad_easypass).setOnClickListener(object: View.OnClickListener{
//            override fun onClick(v: View?) {
//                var lang = Locale.getDefault().language
//                if (lang.contains("zh")) {
//                    lang = "cn"
//                }
//                val uri = Uri.parse("https://www.easypass.tech?lang=$lang")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                startActivity(intent)
//            }
//        })
//        contentView.findViewById<View>(R.id.close).setOnClickListener(object: View.OnClickListener{
//            override fun onClick(v: View?) {
//                mDialog?.dismiss()
//            }
//        })
//        mDialog = builder.create()
//        mDialog?.show()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                showAD()
            }
        }
    }
}
