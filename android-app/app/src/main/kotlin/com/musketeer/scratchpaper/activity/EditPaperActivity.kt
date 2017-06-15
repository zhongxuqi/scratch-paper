package com.musketeer.scratchpaper.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.adapter.PaperListAdapter
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.musketeer.scratchpaper.utils.TimeUtils
import com.musketeer.scratchpaper.view.ScratchPaperView
import com.musketeer.scratchpaper.view.ScratchPaperView.DrawStroke
import com.umeng.analytics.MobclickAgent
import java.util.ArrayList

import java.util.Calendar

class EditPaperActivity : BaseActivity(), OnItemClickListener {

    private var mPaint: MenuItem? = null

    private var mScratchPaper: ScratchPaperView? = null
    private var mDrawerLayout: DrawerLayout? = null

    private var mDialog: AlertDialog? = null
    private var mLoadingDialog: AlertDialog? = null
    private var loadingText: TextView? = null

    //save attribute
    private var paper_name: String = ""

    private var mSavedPaperList: ListView? = null
    private var mAdapter: PaperListAdapter? = null
    private var mPaperList: MutableList<String> = ArrayList()

    private val handler = object : Handler() {
        override //当有消息发送出来的时候就执行Handler的这个方法
        fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //只要执行到这里就关闭对话框
            refreshViews()
            dismissLoadingDialog()
            showCustomToast(resources.getString(R.string.save_success))
        }
    }

    private val ChangePaperHandler = object : Handler() {
        override //当有消息发送出来的时候就执行Handler的这个方法
        fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //只要执行到这里就关闭对话框
            refreshViews()
            if (paper_name.isNotEmpty()) {
                initPaperContent(paper_name)
            } else {
                mScratchPaper!!.setPaperAndDesk(AppPreferenceUtils.getPaperChoose(this@EditPaperActivity),
                        AppPreferenceUtils.getDeskChoose(this@EditPaperActivity))
                mScratchPaper!!.max_undo = AppPreferenceUtils.getMaxUndo(this@EditPaperActivity)
                mScratchPaper!!.clearStrokeList()
                paper_name = TimeUtils.getDateByFileName(Calendar.getInstance().timeInMillis)
            }
            dismissLoadingDialog()
            mDrawerLayout!!.closeDrawer(Gravity.LEFT)
            showCustomToast(resources.getString(R.string.save_success))
        }
    }

    override fun setContentView(savedInstanceState: Bundle?) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        loadingText = view.findViewById(R.id.loading_text) as TextView
        builder.setView(view)
        mLoadingDialog = builder.create()
        setContentView(R.layout.activity_edit_paper)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.icon_small)
        menuInflater.inflate(R.menu.edit_scratch_paper, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val builder: AlertDialog.Builder
        when (item.itemId) {
            R.id.clear_all_title -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                builder.setTitle(resources.getString(R.string.clear_all_title))
                        .setMessage(resources.getString(R.string.affirm_clear_all))
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    mScratchPaper!!.clearAll()
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mDialog!!.show()
            }
            R.id.full_screen -> toggleFullScreen()
            R.id.add_new_paper -> changePaperContent("")
            R.id.undo -> mScratchPaper!!.undoLastAction()
            R.id.paint -> mPaint = item
            R.id.black -> {
                mPaint!!.setIcon(R.mipmap.paint_setting_black)
                mScratchPaper!!.color = Color.BLACK
                mScratchPaper!!.strokeWidth = 5
                mScratchPaper!!.isErase = false
            }
            R.id.red -> {
                mPaint!!.setIcon(R.mipmap.paint_setting_red)
                mScratchPaper!!.color = Color.RED
                mScratchPaper!!.strokeWidth = 5
                mScratchPaper!!.isErase = false
            }
            R.id.blue -> {
                mPaint!!.setIcon(R.mipmap.paint_setting_blue)
                mScratchPaper!!.color = Color.BLUE
                mScratchPaper!!.strokeWidth = 5
                mScratchPaper!!.isErase = false
            }
            R.id.eraser -> {
                mPaint!!.setIcon(R.mipmap.icon_eraser)
                mScratchPaper!!.color = Color.WHITE
                mScratchPaper!!.strokeWidth = 240
                mScratchPaper!!.isErase = true
            }
            R.id.save -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                builder.setTitle(resources.getString(R.string.save_file))
                val contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_savefile, null)
                builder.setView(contentView)
                val filename = contentView.findViewById(R.id.filename) as EditText
                filename.setText(paper_name)
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    mDrawerLayout!!.closeDrawers()
                    savePaper()
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mDialog!!.show()
            }
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN.inv(),
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun initView() {
        // TODO Auto-generated method stub
        mScratchPaper = findViewById(R.id.scratch_paper) as ScratchPaperView
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        mSavedPaperList = findViewById(R.id.paper_list) as ListView
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
        mSavedPaperList!!.onItemClickListener = this
    }

    override fun initData() {
        // TODO Auto-generated method stub
        mScratchPaper!!.setPaperAndDesk(AppPreferenceUtils.getPaperChoose(this),
                AppPreferenceUtils.getDeskChoose(this))
        mScratchPaper!!.max_undo = AppPreferenceUtils.getMaxUndo(this)

        //read paper files
        mPaperList = PaperFileUtils.readPaperList()
        mAdapter = PaperListAdapter(this, mPaperList)
        mSavedPaperList!!.adapter = mAdapter

        //init paper name
        val bundle = intent.extras
        if (bundle != null && bundle.getString("paper_name") != null) {
            paper_name = bundle.getString("paper_name")
            initPaperContent(paper_name)
        } else {
            paper_name = TimeUtils.getDateByFileName(Calendar.getInstance().timeInMillis)
        }
    }

    fun refreshViews() {
        mPaperList = PaperFileUtils.readPaperList()
        mAdapter = PaperListAdapter(this, mPaperList)
        mSavedPaperList!!.adapter = mAdapter
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        mScratchPaper!!.startDraw()
        MobclickAgent.onResume(this)
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        super.onClick(v)
        when (v.id) {

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState)
        val mStorePaperBackGround = Bitmap.createBitmap(mScratchPaper!!.paperWidth,
                mScratchPaper!!.paperHeight, Bitmap.Config.ARGB_8888)
        val bitCanvas = Canvas(mStorePaperBackGround)
        mScratchPaper!!.doDrawForSave(bitCanvas)
        outState.putString(KEY_STORE_BITMAP, "store_paper")
        MainApplication.store.put(outState.getString(KEY_STORE_BITMAP), mStorePaperBackGround)

        val mStrokeList = mScratchPaper!!.strokeList
        outState.putString(KEY_STORE_STROKE, "store_stroke")
        MainApplication.store.put(outState.getString(KEY_STORE_STROKE), mStrokeList)

        mScratchPaper!!.stopDraw()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(KEY_STORE_BITMAP)) {
            mScratchPaper!!.paperBackGround = MainApplication.store.get(
                    savedInstanceState.get(KEY_STORE_BITMAP)) as Bitmap
            MainApplication.store.remove(savedInstanceState.get(KEY_STORE_BITMAP))
            savedInstanceState.remove(KEY_STORE_BITMAP)
        }
        if (savedInstanceState.containsKey(KEY_STORE_STROKE)) {
            mScratchPaper!!.strokeList = MainApplication.store.get(
                    savedInstanceState.get(KEY_STORE_STROKE)) as MutableList<DrawStroke>
            MainApplication.store.remove(savedInstanceState.get(KEY_STORE_STROKE))
            savedInstanceState.remove(KEY_STORE_STROKE)
        }
        mScratchPaper!!.startDraw()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int,
                             id: Long) {
        // TODO Auto-generated method stub
        val mListView = parent
        changePaperContent(mListView.adapter.getItem(position) as String)
    }

    /**
     * 保存草稿纸
     */
    protected fun savePaper() {
        // TODO Auto-generated method stub
        val filename = mDialog!!.findViewById(R.id.filename) as EditText
        if (filename.text == null || filename.text.isEmpty()) {
            showCustomToast(R.string.name_no_null)
            return
        }

        showLoadingDialogNotCancel(R.string.saving)

        //启动纸张保存进程
        Thread(Runnable {
            // TODO Auto-generated method stub
            if (paper_name.isNotEmpty()) {
                PaperFileUtils.deletePaper(paper_name)
            }

            paper_name = filename.text.toString()

            val bitmap = Bitmap.createBitmap(mScratchPaper!!.paperWidth,
                    mScratchPaper!!.paperHeight, Bitmap.Config.ARGB_8888)
            val bitCanvas = Canvas(bitmap)
            mScratchPaper!!.doDrawForScreenShot(bitCanvas)
            PaperFileUtils.savePaper(bitmap, paper_name)
            ChangePaperHandler.sendEmptyMessage(0)
        }).start()
    }

    protected fun changePaperContent(paper_name_tmp: String) {
        val new_paper_name = paper_name_tmp
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.affirm_save_current_paper))
        val contentView = LayoutInflater.from(this).inflate(R.layout.include_change_paper, null)
        builder.setView(contentView)
        val editText = contentView.findViewById(R.id.filename) as EditText
        editText.setText(paper_name)
        builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
            paper_name = new_paper_name
            ChangePaperHandler.sendEmptyMessage(0)
            mDialog!!.dismiss()
        }
        builder.setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
            val filename = mDialog!!.findViewById(R.id.filename) as EditText
            if (filename.text == null || filename.text.toString().isEmpty()) {
                showCustomToast(R.string.name_no_null)
                return@OnClickListener
            }

            showLoadingDialogNotCancel(R.string.saving)

            //启动纸张保存进程
            Thread(Runnable {
                // TODO Auto-generated method stub
                if (paper_name.isNotEmpty()) {
                    PaperFileUtils.deletePaper(paper_name)
                }

                paper_name = filename.text.toString()

                val bitmap = Bitmap.createBitmap(mScratchPaper!!.paperWidth,
                        mScratchPaper!!.paperHeight, Bitmap.Config.ARGB_8888)
                val bitCanvas = Canvas(bitmap)
                mScratchPaper!!.doDrawForScreenShot(bitCanvas)
                PaperFileUtils.savePaper(bitmap, paper_name)
                paper_name = new_paper_name
                ChangePaperHandler.sendEmptyMessage(0)
            }).start()
            mDialog!!.dismiss()
        })
        builder.setNeutralButton(resources.getString(R.string.cancel)) { dialog, which -> mDialog!!.dismiss() }
        mDialog = builder.create()
        mDialog!!.show()
    }

    /**
     * 初始化纸张内容
     * @param paper_name
     */
    protected fun initPaperContent(paper_name: String) {
        if (FileUtils.isFileExist(PaperFileUtils.getPaperPath(paper_name))) {
            mScratchPaper!!.paperBackGround = PaperFileUtils.getPaper(paper_name)
            mScratchPaper!!.clearStrokeList()
            mScratchPaper!!.initPaperPosition()
        } else {
            PaperFileUtils.deletePaper(paper_name)
            finish()
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

    companion object {
        private val KEY_STORE_BITMAP = "store_bitmap"
        private val KEY_STORE_STROKE = "store_stroke"
    }
}
