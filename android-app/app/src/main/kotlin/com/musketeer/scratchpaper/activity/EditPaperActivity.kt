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
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.OnColorSelectedListener
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder

import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.adapter.PaperListAdapter
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.musketeer.scratchpaper.utils.TimeUtils
import com.musketeer.scratchpaper.view.ScratchPaperView
import com.musketeer.scratchpaper.view.ScratchPaperView.DrawStroke
import com.muskeeter.base.acitivity.BaseActivity
import com.muskeeter.base.utils.ScreenUtils
import com.nightonke.boommenu.BoomButtons.OnBMClickListener
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.umeng.analytics.MobclickAgent
import java.util.ArrayList

import java.util.Calendar

class EditPaperActivity : BaseActivity(), OnItemClickListener, OnBMClickListener {

    companion object {
        val TAG = "EditPaperActivity"
        val ACTION_SAVE = 0
        val ACTION_SAVE_WITH_EXIT = 1
        private val KEY_STORE_BITMAP = "store_bitmap"
        private val KEY_STORE_STROKE = "store_stroke"
    }

    private var mPaint: MenuItem? = null

    private val mScratchPaper: ScratchPaperView by lazy {
        findViewById(R.id.scratch_paper) as ScratchPaperView
    }
    private var mDrawerLayout: DrawerLayout? = null
    private val mBoomMenuButton: BoomMenuButton by lazy {
        findViewById(R.id.bmb) as BoomMenuButton
    }
    private val mPaintStatus: ImageView by lazy {
        findViewById(R.id.paint_status) as ImageView
    }

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
            if (msg.what == ACTION_SAVE_WITH_EXIT) {
                finish()
                return
            }
            //只要执行到这里就关闭对话框
            refreshViews()
            if (paper_name.isNotEmpty()) {
                initPaperContent(paper_name)
            } else {
                mScratchPaper.setPaperAndDesk(AppPreferenceUtils.getPaperChoose(this@EditPaperActivity),
                        AppPreferenceUtils.getDeskChoose(this@EditPaperActivity))
                mScratchPaper.max_undo = AppPreferenceUtils.getMaxUndo(this@EditPaperActivity)
                mScratchPaper.clearStrokeList()
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
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_paper)
    }

    override fun initView() {
        // TODO Auto-generated method stub
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        mSavedPaperList = findViewById(R.id.paper_list) as ListView
        mPaintStatus.setImageResource(R.drawable.ic_edit_black_24dp)
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
        mSavedPaperList!!.onItemClickListener = this
        mPaintStatus.setOnClickListener(this)
    }

    fun initBmbBuilder(): SimpleCircleButton.Builder {
        return SimpleCircleButton.Builder().isRound(false).shadowCornerRadius(ScreenUtils.dpToPx(this, 20F).toInt())
                .buttonCornerRadius(ScreenUtils.dpToPx(this, 20F).toInt()).normalColor(Color.WHITE).listener(this)
    }

    override fun initData() {
        // TODO Auto-generated method stub
        mScratchPaper.setPaperAndDesk(AppPreferenceUtils.getPaperChoose(this),
                AppPreferenceUtils.getDeskChoose(this))
        mScratchPaper.max_undo = AppPreferenceUtils.getMaxUndo(this)

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

        // init boom menu
        for (i in 0..(mBoomMenuButton.buttonPlaceEnum.buttonNumber() - 1)) {
            when(i) {
                0 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_note_add_black_24dp)))
                }
                1 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_save_black_24dp)))
                }
                2 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_undo_black_24dp)))
                }
                3 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_edit_black_24dp)))
                }
                4 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.mipmap.icon_eraser)))
                }
                5 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_clear_black_24dp)))
                }
            }
        }
    }

    override fun onBoomButtonClick(index: Int) {
        val builder: AlertDialog.Builder
        when(index) {
            0 -> {
                changePaperContent("")
            }
            1 -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                builder.setTitle(resources.getString(R.string.save_file))
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    mDrawerLayout!!.closeDrawers()
                    savePaper(ACTION_SAVE)
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mDialog!!.show()
            }
            2 -> {
                mScratchPaper.undoLastAction()
            }
            3 -> {
                mPaintStatus.setImageResource(R.drawable.ic_edit_black_24dp)
                if (mScratchPaper.color == Color.WHITE) {
                    mScratchPaper.color = Color.BLACK
                }
                mPaintStatus.setColorFilter(mScratchPaper.color)
                mScratchPaper.strokeWidth = 5
                mScratchPaper.isErase = false
            }
            4 -> {
                mPaintStatus.setImageResource(R.mipmap.icon_eraser)
                mPaintStatus.setColorFilter(Color.TRANSPARENT)
                mScratchPaper.color = Color.WHITE
                mScratchPaper.strokeWidth = 240
                mScratchPaper.isErase = true
            }
            5 -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                builder.setTitle(resources.getString(R.string.clear_all_title))
                        .setMessage(resources.getString(R.string.affirm_clear_all))
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    mScratchPaper.clearAll()
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mDialog!!.show()
            }
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
        mScratchPaper.startDraw()
        MobclickAgent.onResume(this)
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        super.onClick(v)
        when (v.id) {
            R.id.paint_status -> {
                if (mScratchPaper.isErase) {
                    return
                }
                ColorPickerDialogBuilder.with(this).setTitle(resources.getString(R.string.choose_color))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(object: OnColorSelectedListener {
                            override fun onColorSelected(selectedColor: Int) {
                                mPaintStatus.setColorFilter(selectedColor)
                                mScratchPaper.color = selectedColor
                            }
                        })
                        .setPositiveButton(resources.getText(R.string.affirm), object: ColorPickerClickListener {
                            override fun onClick(dialog: DialogInterface?, lastSelectedColor: Int, allColors: Array<out Int>?) {
                                mPaintStatus.setColorFilter(lastSelectedColor)
                                mScratchPaper.color = lastSelectedColor
                                dialog?.dismiss()
                            }
                        })
                        .setNegativeButton(resources.getText(R.string.cancel), object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog?.dismiss()
                            }
                        })
                        .build().show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState)
        val mStorePaperBackGround = Bitmap.createBitmap(mScratchPaper.paperWidth,
                mScratchPaper.paperHeight, Bitmap.Config.ARGB_8888)
        val bitCanvas = Canvas(mStorePaperBackGround)
        mScratchPaper.doDrawForSave(bitCanvas)
        outState.putString(KEY_STORE_BITMAP, "store_paper")
        MainApplication.store.put(outState.getString(KEY_STORE_BITMAP), mStorePaperBackGround)

        val mStrokeList = mScratchPaper.strokeList
        outState.putString(KEY_STORE_STROKE, "store_stroke")
        MainApplication.store.put(outState.getString(KEY_STORE_STROKE), mStrokeList)

        mScratchPaper.stopDraw()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(KEY_STORE_BITMAP)) {
            mScratchPaper.paperBackGround = MainApplication.store.get(
                    savedInstanceState.get(KEY_STORE_BITMAP)) as Bitmap
            MainApplication.store.remove(savedInstanceState.get(KEY_STORE_BITMAP))
            savedInstanceState.remove(KEY_STORE_BITMAP)
        }
        if (savedInstanceState.containsKey(KEY_STORE_STROKE)) {
            mScratchPaper.strokeList = MainApplication.store.get(
                    savedInstanceState.get(KEY_STORE_STROKE)) as MutableList<DrawStroke>
            MainApplication.store.remove(savedInstanceState.get(KEY_STORE_STROKE))
            savedInstanceState.remove(KEY_STORE_STROKE)
        }
        mScratchPaper.startDraw()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        // TODO Auto-generated method stub
        val mListView = parent
        changePaperContent(mListView.adapter.getItem(position) as String)
    }

    /**
     * 保存草稿纸
     */
    protected fun savePaper(action : Int) {
        showLoadingDialogNotCancel(R.string.saving)

        //启动纸张保存进程
        Thread(Runnable {
            // TODO Auto-generated method stub
            if (paper_name.isNotEmpty()) {
                PaperFileUtils.deletePaper(paper_name)
            }

            val bitmap = Bitmap.createBitmap(mScratchPaper.paperWidth,
                    mScratchPaper.paperHeight, Bitmap.Config.ARGB_8888)
            val bitCanvas = Canvas(bitmap)
            mScratchPaper.doDrawForScreenShot(bitCanvas)
            PaperFileUtils.savePaper(bitmap, paper_name)
            mScratchPaper.isEdited = false
            ChangePaperHandler.sendEmptyMessage(action)
        }).start()
    }

    protected fun changePaperContent(paper_name_tmp: String) {
        val new_paper_name = paper_name_tmp
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.affirm_save_current_paper))
        builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
            paper_name = new_paper_name
            ChangePaperHandler.sendEmptyMessage(0)
            mDialog!!.dismiss()
        }
        builder.setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
            showLoadingDialogNotCancel(R.string.saving)

            //启动纸张保存进程
            Thread(Runnable {
                // TODO Auto-generated method stub
                if (paper_name.isNotEmpty()) {
                    PaperFileUtils.deletePaper(paper_name)
                }

                val bitmap = Bitmap.createBitmap(mScratchPaper.paperWidth,
                        mScratchPaper.paperHeight, Bitmap.Config.ARGB_8888)
                val bitCanvas = Canvas(bitmap)
                mScratchPaper.doDrawForScreenShot(bitCanvas)
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
            mScratchPaper.paperBackGround = PaperFileUtils.getPaper(paper_name)
            mScratchPaper.clearStrokeList()
            mScratchPaper.initPaperPosition()
        } else {
            PaperFileUtils.deletePaper(paper_name)
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mScratchPaper.isEdited) {
            mDialog?.dismiss()
            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.exit_with_save_file))
            builder.setNeutralButton(resources.getString(R.string.cancel)) { dialog, which -> mDialog!!.dismiss() }
            builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                mDialog?.dismiss()
                finish()
            }
            builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                mDrawerLayout!!.closeDrawers()
                savePaper(ACTION_SAVE_WITH_EXIT)
                mDialog?.dismiss()
            }
            mDialog = builder.create()
            mDialog?.show()
            return true
        }
        return super.onKeyDown(keyCode, event);
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
