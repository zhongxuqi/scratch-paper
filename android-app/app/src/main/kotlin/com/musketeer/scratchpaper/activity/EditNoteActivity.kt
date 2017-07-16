package com.musketeer.scratchpaper.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.OnColorSelectedListener
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.muskeeter.base.acitivity.BaseActivity
import com.muskeeter.base.utils.ScreenUtils
import com.musketeer.scratchpaper.MainApplication

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.adapter.PaperListAdapter
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.fileutils.NoteFileUtils
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.FileUtils
import com.musketeer.scratchpaper.utils.LogUtils
import com.musketeer.scratchpaper.utils.TimeUtils
import com.musketeer.scratchpaper.view.ScratchNoteView
import com.nightonke.boommenu.BoomButtons.OnBMClickListener
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.umeng.analytics.MobclickAgent
import java.util.*

class EditNoteActivity : BaseActivity(), AdapterView.OnItemClickListener, OnBMClickListener {
    companion object {
        val TAG = "EditNoteActivity"
        val ACTION_SAVE = 0
        val ACTION_SAVE_WITH_EXIT = 1
        private val KEY_STORE_BITMAP = "store_bitmap"
        private val KEY_STORE_STROKE = "store_stroke"
    }

    private val mScratchNote: ScratchNoteView by lazy {
        findViewById(R.id.scratch_paper) as ScratchNoteView
    }
    private var mDrawerLayout: DrawerLayout? = null
    private val mBoomMenuButton: BoomMenuButton by lazy {
        findViewById(R.id.bmb) as BoomMenuButton
    }
    private val mPaintStatus: ImageView by lazy {
        findViewById(R.id.paint_status) as ImageView
    }

    private var mDialog: AlertDialog? = null

    //save attribute
    private var note_name: String = ""

    private var mSavedPaperList: ListView? = null
    private var mAdapter: PaperListAdapter? = null
    private var mNoteList: MutableList<String> = ArrayList()

    private val ChangePaperHandler = object : Handler() {
        override //当有消息发送出来的时候就执行Handler的这个方法
        fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == EditNoteActivity.ACTION_SAVE_WITH_EXIT) {
                finish()
                return
            }
            //只要执行到这里就关闭对话框
            refreshViews()
            if (note_name.isNotEmpty()) {
                initPaperContent(note_name)
            } else {
                mScratchNote.setPaper(AppPreferenceUtils.getPaperChoose(this@EditNoteActivity))
                mScratchNote.max_undo = AppPreferenceUtils.getMaxUndo(this@EditNoteActivity)
                mScratchNote.clearStrokeList()
                note_name = TimeUtils.getDateByFileName(Calendar.getInstance().timeInMillis)
            }
            dismissLoadingDialog()
            mDrawerLayout!!.closeDrawer(Gravity.LEFT)
            showCustomToast(resources.getString(R.string.save_success))
        }
    }

    override fun setContentView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_note)
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
        mScratchNote.setPaper(AppPreferenceUtils.getPaperChoose(this))
        mScratchNote.max_undo = 5000

        //read paper files
        mNoteList = NoteFileUtils.readImageList()
        mAdapter = PaperListAdapter(this, mNoteList)
        mSavedPaperList!!.adapter = mAdapter

        //init paper name
        val bundle = intent.extras
        if (bundle != null && bundle.getString("note_name") != null) {
            note_name = bundle.getString("note_name")
            initPaperContent(note_name)
        } else {
            note_name = TimeUtils.getDateByFileName(Calendar.getInstance().timeInMillis)
        }

        // init boom menu
        for (i in 0..(mBoomMenuButton.buttonPlaceEnum.buttonNumber() - 1)) {
            when(i) {
                0 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_save_black_24dp)))
                }
                1 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_undo_black_24dp)))
                }
                2 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_edit_black_24dp)))
                }
                3 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.mipmap.icon_eraser)))
                }
                4 -> {
                    mBoomMenuButton.addBuilder(initBmbBuilder().normalImageDrawable(resources.getDrawable(R.drawable.ic_insert_drive_file_black_24dp)))
                }
            }
        }
    }

    override fun onBoomButtonClick(index: Int) {
        val builder: AlertDialog.Builder
        when(index) {
            0 -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                builder.setTitle(resources.getString(R.string.save_file))
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    mDrawerLayout!!.closeDrawers()
                    savePaper(EditNoteActivity.ACTION_SAVE)
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mDialog!!.show()
            }
            1 -> {
                mScratchNote.undoLastAction()
            }
            2 -> {
                mPaintStatus.setImageResource(R.drawable.ic_edit_black_24dp)
                if (mScratchNote.color == Color.WHITE) {
                    mScratchNote.color = Color.BLACK
                }
                mPaintStatus.setColorFilter(mScratchNote.color)
                mScratchNote.strokeWidth = 5
                mScratchNote.isErase = false
            }
            3 -> {
                mPaintStatus.setImageResource(R.mipmap.icon_eraser)
                mPaintStatus.setColorFilter(Color.TRANSPARENT)
                mScratchNote.color = Color.WHITE
                mScratchNote.strokeWidth = 240
                mScratchNote.isErase = true
            }
            4 -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                builder.setTitle(resources.getString(R.string.clear_all_title))
                        .setMessage(resources.getString(R.string.affirm_clear_all))
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    mScratchNote.clearAll()
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mDialog!!.show()
            }
        }
    }

    fun refreshViews() {
        mNoteList = NoteFileUtils.readImageList()
        mAdapter = PaperListAdapter(this, mNoteList)
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
        mScratchNote.startDraw()
        MobclickAgent.onResume(this)
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        super.onClick(v)
        when (v.id) {
            R.id.paint_status -> {
                if (mScratchNote.isErase) {
                    return
                }
                mDialog?.dismiss()
                val builder = AlertDialog.Builder(this)
                val view = LayoutInflater.from(this).inflate(R.layout.dialog_paint_config, null)
                builder.setView(view)
                val colorPicker = view.findViewById(R.id.color_picker_view) as ColorPickerView
                val strokePicker = view.findViewById(R.id.paint_sroke_width) as SeekBar
                builder.setNegativeButton(resources.getText(R.string.cancel), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        mDialog?.dismiss()
                    }
                })
                builder.setPositiveButton(resources.getText(R.string.affirm), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        mPaintStatus.setColorFilter(colorPicker.selectedColor)
                        mScratchNote.color = colorPicker.selectedColor
                        if (strokePicker.progress < Config.MIN_STROKE_WIDTH) {
                            strokePicker.progress = Config.MIN_STROKE_WIDTH
                        }
                        mScratchNote.strokeWidth = strokePicker.progress
                        dialog?.dismiss()
                    }
                })
                mDialog = builder.create()
                mDialog?.show()
                if (mScratchNote.color == Color.BLACK) {
                    colorPicker.setInitialColor(Color.BLUE, true)
                } else {
                    colorPicker.setInitialColor(mScratchNote.color, true)
                }
                strokePicker.progress = mScratchNote.strokeWidth
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState)
        val mStorePaperBackGround = Bitmap.createBitmap(mScratchNote.mNoteBackGround!!.width,
                mScratchNote.mNoteBackGround!!.height, Bitmap.Config.ARGB_8888)
        val bitCanvas = Canvas(mStorePaperBackGround)
        mScratchNote.doDrawForSave(bitCanvas)
        outState.putString(EditNoteActivity.KEY_STORE_BITMAP, "store_paper")
        MainApplication.store.put(outState.getString(EditNoteActivity.KEY_STORE_BITMAP), mStorePaperBackGround)

        val mStrokeList = mScratchNote.strokeList
        outState.putString(EditNoteActivity.KEY_STORE_STROKE, "store_stroke")
        MainApplication.store.put(outState.getString(EditNoteActivity.KEY_STORE_STROKE), mStrokeList)

        mScratchNote.stopDraw()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(EditNoteActivity.KEY_STORE_BITMAP)) {
            mScratchNote.paperBackGround = MainApplication.store.get(
                    savedInstanceState.get(EditNoteActivity.KEY_STORE_BITMAP)) as Bitmap
            MainApplication.store.remove(savedInstanceState.get(EditNoteActivity.KEY_STORE_BITMAP))
            savedInstanceState.remove(EditNoteActivity.KEY_STORE_BITMAP)
        }
        if (savedInstanceState.containsKey(EditNoteActivity.KEY_STORE_STROKE)) {
            mScratchNote.strokeList = MainApplication.store.get(
                    savedInstanceState.get(EditNoteActivity.KEY_STORE_STROKE)) as MutableList<ScratchNoteView.DrawStroke>
            MainApplication.store.remove(savedInstanceState.get(EditNoteActivity.KEY_STORE_STROKE))
            savedInstanceState.remove(EditNoteActivity.KEY_STORE_STROKE)
        }
        mScratchNote.startDraw()
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
            if (note_name.isNotEmpty()) {
                NoteFileUtils.deleteImage(note_name)
            }

            val bitmap = Bitmap.createBitmap(mScratchNote.mNoteBackGround!!.width,
                    mScratchNote.mNoteBackGround!!.height, Bitmap.Config.ARGB_8888)
            val bitCanvas = Canvas(bitmap)
            mScratchNote.doDrawForScreenShot(bitCanvas)
            NoteFileUtils.saveImage(bitmap, note_name)
            mScratchNote.isEdited = false
            ChangePaperHandler.sendEmptyMessage(action)
        }).start()
    }

    protected fun changePaperContent(note_name_tmp: String) {
        val new_note_name = note_name_tmp
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.affirm_save_current_paper))
        builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
            note_name = new_note_name
            ChangePaperHandler.sendEmptyMessage(0)
            mDialog!!.dismiss()
        }
        builder.setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
            showLoadingDialogNotCancel(R.string.saving)

            //启动纸张保存进程
            Thread(Runnable {
                // TODO Auto-generated method stub
                if (note_name.isNotEmpty()) {
                    NoteFileUtils.deleteImage(note_name)
                }

                val bitmap = Bitmap.createBitmap(mScratchNote.mNoteBackGround!!.width,
                        mScratchNote.mNoteBackGround!!.height, Bitmap.Config.ARGB_8888)
                val bitCanvas = Canvas(bitmap)
                mScratchNote.doDrawForScreenShot(bitCanvas)
                NoteFileUtils.saveImage(bitmap, note_name)
                note_name = new_note_name
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
     * @param note_name
     */
    protected fun initPaperContent(note_name: String) {
        LogUtils.d(TAG, NoteFileUtils.getImagePath(note_name))
        if (FileUtils.isFileExist(NoteFileUtils.getImagePath(note_name))) {
            mScratchNote.paperBackGround = NoteFileUtils.getImage(note_name)
            mScratchNote.clearStrokeList()
        } else {
            NoteFileUtils.deleteImage(note_name)
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mScratchNote.isEdited) {
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
                savePaper(EditPaperActivity.ACTION_SAVE_WITH_EXIT)
                mDialog?.dismiss()
            }
            mDialog = builder.create()
            mDialog?.show()
            return true
        }
        return super.onKeyDown(keyCode, event);
    }
}
