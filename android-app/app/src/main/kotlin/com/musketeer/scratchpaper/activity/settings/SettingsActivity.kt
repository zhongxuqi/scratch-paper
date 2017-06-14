package com.musketeer.scratchpaper.activity.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView

import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.BaseActivity
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.SharePreferenceUtils
import com.umeng.analytics.MobclickAgent

class SettingsActivity : BaseActivity() {

    //base setting
    private var mPaperSizeSelector: Spinner? = null
    private var mPaperRowNumText: TextView? = null

    //senior setting
    private var mMaxUndoText: TextView? = null

    private var mDialog: AlertDialog? = null
    private var mNumberPicker: NumberPicker? = null

    override fun setContentView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_settings)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.icon_small)
        //		getActionBar().setTitle(getResources().getString(R.string.title_activity_settings));
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        // TODO Auto-generated method stub
        mPaperSizeSelector = findViewById(R.id.paper_size_selector) as Spinner
        mPaperRowNumText = findViewById(R.id.paper_row_number_text) as TextView
        mMaxUndoText = findViewById(R.id.max_undo) as TextView
    }

    override fun initEvent() {
        // TODO Auto-generated method stub
        findViewById(R.id.paper_row_number).setOnClickListener(this)
        findViewById(R.id.set_max_undo).setOnClickListener(this)
        findViewById(R.id.back_to_defalut).setOnClickListener(this)
    }

    override fun initData() {
        // TODO Auto-generated method stub
        val mAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.paper_size))
        mPaperSizeSelector!!.adapter = mAdapter
        mPaperSizeSelector!!.setSelection(SharePreferenceUtils.getInt(this, SharePreferenceConfig.PAPER_SIZE, 0))
        mPaperSizeSelector!!.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                // TODO Auto-generated method stub
                SharePreferenceUtils.putInt(this@SettingsActivity, SharePreferenceConfig.PAPER_SIZE, position)
            }

            override fun onNothingSelected(adapter: AdapterView<*>) {
                // TODO Auto-generated method stub

            }

        }

        mPaperRowNumText!!.text = "" + AppPreferenceUtils.getRowNum(this)
        mMaxUndoText!!.text = "" + AppPreferenceUtils.getMaxUndo(this)
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        super.onClick(v)
        val builder: AlertDialog.Builder
        val contentView: View
        when (v.id) {
            R.id.paper_row_number -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_numberpicker, null)
                builder.setView(contentView)
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    SharePreferenceUtils.putInt(this@SettingsActivity,
                            SharePreferenceConfig.ROW_NUM, mNumberPicker!!.value)
                    mPaperRowNumText!!.text = "" + mNumberPicker!!.value
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mNumberPicker = contentView.findViewById(R.id.number_picker) as NumberPicker
                mNumberPicker!!.minValue = 2
                mNumberPicker!!.maxValue = 5
                mNumberPicker!!.value = AppPreferenceUtils.getRowNum(this)
                mDialog!!.show()
            }
            R.id.set_max_undo -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_numberpicker, null)
                builder.setView(contentView)
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    SharePreferenceUtils.putInt(this@SettingsActivity,
                            SharePreferenceConfig.MAX_UNDO, mNumberPicker!!.value)
                    mMaxUndoText!!.text = "" + mNumberPicker!!.value
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                mNumberPicker = contentView.findViewById(R.id.number_picker) as NumberPicker
                mNumberPicker!!.minValue = 10
                mNumberPicker!!.maxValue = 200
                mNumberPicker!!.value = AppPreferenceUtils.getMaxUndo(this)
                mDialog!!.show()
            }
            R.id.back_to_defalut -> {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                builder = AlertDialog.Builder(this)
                contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_content, null)
                builder.setView(contentView)
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    SharePreferenceUtils.putInt(this@SettingsActivity, SharePreferenceConfig.ROW_NUM, 3)
                    mPaperRowNumText!!.text = "3"
                    SharePreferenceUtils.putInt(this@SettingsActivity, SharePreferenceConfig.MAX_UNDO,
                            MainApplication.PAPER_MAX_UNDO)
                    mMaxUndoText!!.text = "" + MainApplication.PAPER_MAX_UNDO
                    showCustomToast(resources.getString(R.string.set_success))
                    mDialog!!.dismiss()
                }
                mDialog = builder.create()
                val textview = contentView.findViewById(R.id.alert_content) as TextView
                textview.text = resources.getString(R.string.affirm_back)
                mDialog!!.show()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    public override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}
