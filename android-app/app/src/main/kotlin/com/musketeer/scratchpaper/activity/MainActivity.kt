package com.musketeer.scratchpaper.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.view.View.OnClickListener
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener

import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.settings.SettingsActivity
import com.musketeer.scratchpaper.adapter.PaperListAdapter
import com.musketeer.scratchpaper.common.Contants
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.LogUtils
import com.musketeer.scratchpaper.utils.SharePreferenceUtils
import com.qq.e.ads.interstitial.InterstitialAD
import com.qq.e.ads.interstitial.InterstitialADListener
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.shareboard.SnsPlatform
import com.umeng.socialize.utils.ShareBoardlistener

import java.io.File

class MainActivity : BaseActivity(), OnItemClickListener, OnItemLongClickListener {

    companion object {
        private val TAG = "MainActivity"
        private val ADD_NEW_PAPER = 1
        private val EDIT_NEW_PAPER = 2
        private val CHANGE_SETTINGS = 3
    }

    //	private DrawerLayout mDrawerLayout;
    //	private ActionBarDrawerToggle mDrawerToggle;

    //	private Button mNewScratchPaper;
    //	private Button mSetting;
    //	private Button mQuit;

    private var addNewScratchPaper: ImageView? = null

    private var mSavedPaperList: GridView? = null
    private var mAdapter: PaperListAdapter? = null
    private var mPaperList: MutableList<String> = ArrayList()

    //	private BaseDialog mDialog;
    //	private LoadingDialog mLoadingDialog;
    private var mDialog: AlertDialog? = null
    private var mLoadingDialog: AlertDialog? = null
    private var loadingText: TextView? = null

    private var closeTime: Long = 0
    private var interstitialAD: InterstitialAD? = null

    private val handler = object : Handler() {
        override //当有消息发送出来的时候就执行Handler的这个方法
        fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //只要执行到这里就关闭对话框
            dismissLoadingDialog()
            mAdapter!!.notifyDataSetChanged()
            showCustomToast(resources.getString(R.string.rename_success))
        }
    }

    override fun setContentView(savedInstanceState: Bundle?) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        loadingText = view.findViewById(R.id.loading_text) as TextView
        builder.setView(view)
        mLoadingDialog = builder.create()
        setContentView(R.layout.activity_main)
    }

    override fun initView() {
        // TODO Auto-generated method stub
        //		mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        //		mDrawerLayout.openDrawer(Gravity.LEFT);

        //		mNewScratchPaper=(Button) findViewById(R.id.new_scratch_paper);
        //		mSetting=(Button) findViewById(R.id.setting);
        //		mQuit=(Button) findViewById(R.id.quit);

        addNewScratchPaper = findViewById(R.id.add_new_scratch_paper) as ImageView

        mSavedPaperList = findViewById(R.id.paper_gridlist) as GridView
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
        //		mNewScratchPaper.setOnClickListener(this);
        //		mSetting.setOnClickListener(this);
        //		mQuit.setOnClickListener(this);

        addNewScratchPaper!!.setOnClickListener(this)

        mSavedPaperList!!.onItemClickListener = this
        mSavedPaperList!!.onItemLongClickListener = this
    }

    fun refreshViews() {
        mPaperList = PaperFileUtils.readPaperList()
        mAdapter = PaperListAdapter(this, mPaperList)
        mSavedPaperList!!.adapter = mAdapter
    }

    override fun initData() {
        // TODO Auto-generated method stub
        supportActionBar!!.setLogo(R.mipmap.icon_small)
        supportActionBar!!.setIcon(R.mipmap.icon_small)

        //设置抽屉
        //		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
        //		mDrawerLayout.post(new Runnable() {
        //	        @Override
        //	        public void run() {
        //	            mDrawerToggle.syncState();
        //	        }
        //	    });
        //		mDrawerLayout.setDrawerListener(mDrawerToggle);

        mSavedPaperList!!.numColumns = AppPreferenceUtils.getRowNum(this)

        //read paper files
        mPaperList = PaperFileUtils.readPaperList()
        mAdapter = PaperListAdapter(this, mPaperList)
        mSavedPaperList!!.adapter = mAdapter

        checkConfig()
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
        //			if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
        //				mDrawerLayout.closeDrawer(Gravity.LEFT);
        //			} else {
        //				mDrawerLayout.openDrawer(Gravity.LEFT);
        //			}
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        when (v.id) {
            R.id.add_new_scratch_paper -> {
                val myAnimation = AnimationUtils.loadAnimation(this, R.anim.view_scale_larger)
                addNewScratchPaper!!.startAnimation(myAnimation)
                myAnimation.setAnimationListener(object : AnimationListener {

                    override fun onAnimationStart(animation: Animation) {
                        // TODO Auto-generated method stub

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        // TODO Auto-generated method stub
                        startActivityForResult(EditPaperActivity::class.java, ADD_NEW_PAPER)
                    }

                    override fun onAnimationRepeat(animation: Animation) {
                        // TODO Auto-generated method stub

                    }

                })
            }
            R.id.setting -> startActivityForResult(SettingsActivity::class.java, CHANGE_SETTINGS)
            R.id.help -> startActivity(HelpActivity::class.java)
        }
    }

    /**
     * 检查配置的有效性
     */
    private fun checkConfig() {
        // TODO Auto-generated method stub
        try {
            resources.getDrawable(SharePreferenceUtils.getInt(this,
                    SharePreferenceConfig.PAPER,
                    MainApplication.DEFAULT_PAPER))
            resources.getDrawable(SharePreferenceUtils.getInt(this,
                    SharePreferenceConfig.DESK,
                    MainApplication.DEFAULT_DESK))
        } catch (e: NotFoundException) {
            SharePreferenceUtils.putInt(this, SharePreferenceConfig.MAX_UNDO,
                    MainApplication.PAPER_MAX_UNDO)
            SharePreferenceUtils.putInt(this, SharePreferenceConfig.ROW_NUM, 3)
        }

    }

    override fun onItemClick(parent: AdapterView<*>, view: View,
                             position: Int, id: Long) {
        // TODO Auto-generated method stub
        val mListView = parent
        val selectPosition = position

        if (mDialog != null) {
            mDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this)
        val contentView = LayoutInflater.from(this).inflate(R.layout.include_saved_paper_action, null)
        builder.setView(contentView)
        mDialog = builder.create()
        //查看内容
        val lookButton = contentView.findViewById(R.id.look) as TextView
        lookButton.setOnClickListener {
            // TODO Auto-generated method stub
            val bundle = Bundle()
            bundle.putString("paper_name", mListView.adapter.getItem(selectPosition) as String)
            startActivityForResult(BrowsePaperActivity::class.java, bundle, EDIT_NEW_PAPER)
            mDialog!!.dismiss()
        }
        //编辑内容
        val editButton = contentView.findViewById(R.id.edit) as TextView
        editButton.setOnClickListener {
            // TODO Auto-generated method stub
            val bundle = Bundle()
            bundle.putString("paper_name", mListView.adapter.getItem(selectPosition) as String)
            startActivityForResult(EditPaperActivity::class.java, bundle, EDIT_NEW_PAPER)
            mDialog!!.dismiss()
        }
        //重命名
        val renameButton = contentView.findViewById(R.id.rename) as TextView
        renameButton.setOnClickListener(OnClickListener {
            // TODO Auto-generated method stub
            mDialog!!.dismiss()
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(resources.getString(R.string.rename))
            builder.setView(LayoutInflater.from(this@MainActivity).inflate(R.layout.include_dialog_edittext, null))
            builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
            builder.setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                mDialog!!.dismiss()

                val rename = mDialog!!.findViewById(R.id.edittext) as EditText
                if (rename.text.isEmpty()) {
                    showCustomToast(R.string.name_no_null)
                    return@OnClickListener
                }

                showLoadingDialogNotCancel(R.string.saving)

                Thread(Runnable {
                    val paper_name = rename.text.toString()

                    if (paper_name != mAdapter!!.getItem(selectPosition)) {
                        //读取原文件
                        val bitmap = PaperFileUtils.getPaper(mAdapter!!.getItem(selectPosition))

                        //保存文件
                        PaperFileUtils.savePaper(bitmap, paper_name)

                        //删除文件
                        PaperFileUtils.deletePaper(mAdapter!!.getItem(selectPosition))

                        mPaperList.removeAt(selectPosition)
                        mPaperList.add(selectPosition, paper_name)
                    }
                    handler.sendEmptyMessage(0)
                }).start()
            })
            mDialog = builder.create()
            mDialog!!.show()
        })
        val shareButton = contentView.findViewById(R.id.share) as TextView
        shareButton.setOnClickListener {
            LogUtils.d(TAG, "file: ${mAdapter?.getItem(selectPosition)}")
            val filePath = PaperFileUtils.getPaperPath(mAdapter!!.getItem(selectPosition))
            val image = UMImage(this, File(filePath))
            image.setThumb(UMImage(this, File(filePath)))
            image.compressStyle = UMImage.CompressStyle.SCALE
            ShareAction(this).withText(mAdapter?.getItem(selectPosition)).withMedia(image)
                .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
            .setShareboardclickCallback(object: ShareBoardlistener{
            override fun onclick(p0: SnsPlatform?, p1: SHARE_MEDIA?) {
                ShareAction(this@MainActivity).setPlatform(p1).withText(mAdapter?.getItem(selectPosition)).withMedia(image)
                    .setCallback(object: UMShareListener{
                        override fun onResult(p0: SHARE_MEDIA?) {
                            LogUtils.d(TAG, "onResult SHARE_MEDIA: $p0")
                        }

                        override fun onCancel(p0: SHARE_MEDIA?) {
                            LogUtils.d(TAG, "onCancel SHARE_MEDIA: $p0")
                        }

                        override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                            p1?.printStackTrace()
                            LogUtils.d(TAG, "onError SHARE_MEDIA: $p0")
                        }

                        override fun onStart(p0: SHARE_MEDIA?) {
                            LogUtils.d(TAG, "onStart SHARE_MEDIA: $p0")
                        }
                    }).share()
            }
        }).open()
            mDialog?.dismiss()
        }
        mDialog!!.show()
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int,
                                 id: Long): Boolean {
        // TODO Auto-generated method stub
        val mListView = parent
        val selectPosition = position
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.affirm_delete))
        builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
            PaperFileUtils.deletePaper(mListView.adapter.getItem(selectPosition) as String)
            mPaperList!!.removeAt(selectPosition)
            mAdapter!!.notifyDataSetChanged()
            mDialog!!.dismiss()
        }
        mDialog = builder.create()
        mDialog!!.show()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_NEW_PAPER -> refreshViews()
            EDIT_NEW_PAPER -> refreshViews()
            CHANGE_SETTINGS -> initData()
        }
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
