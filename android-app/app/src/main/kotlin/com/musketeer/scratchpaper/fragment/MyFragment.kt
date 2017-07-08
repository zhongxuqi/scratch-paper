package com.musketeer.scratchpaper.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.muskeeter.base.fragment.BaseSupportFragment
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.HelpActivity
import com.musketeer.scratchpaper.activity.MainActivity
import com.musketeer.scratchpaper.activity.settings.SettingsActivity
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.SharePreferenceUtils

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class MyFragment: BaseSupportFragment() {
    companion object {
        val TAG = "MyFragment"
    }

    val mPaperSizeSelector: Spinner by lazy {
        findViewById(R.id.paper_size_selector) as Spinner
    }
    val mPaperRowNumText: TextView by lazy {
        findViewById(R.id.paper_row_number_text) as TextView
    }
    val mMaxUndoText: TextView by lazy {
        findViewById(R.id.max_undo) as TextView
    }

    private var mDialog: AlertDialog? = null
    private var mNumberPicker: NumberPicker? = null

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) {
        BaseView = inflater?.inflate(R.layout.fragment_my, null)
    }

    override fun initView() {

    }

    override fun initEvent() {
        findViewById(R.id.paper_row_number)?.setOnClickListener(this)
        findViewById(R.id.set_max_undo)?.setOnClickListener(this)
        findViewById(R.id.action_help)?.setOnClickListener(this)
    }

    override fun initData() {
        val mAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.paper_size))
        mPaperSizeSelector.adapter = mAdapter
        mPaperSizeSelector.setSelection(SharePreferenceUtils.getInt(activity, SharePreferenceConfig.PAPER_SIZE, 0))
        mPaperSizeSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(adapter: AdapterView<*>, v: View, position: Int, id: Long) {
                SharePreferenceUtils.putInt(context, SharePreferenceConfig.PAPER_SIZE, position)
            }

            override fun onNothingSelected(adapter: AdapterView<*>) {

            }

        }

        mPaperRowNumText.text = AppPreferenceUtils.getRowNum(activity).toString()
        mMaxUndoText.text = AppPreferenceUtils.getMaxUndo(activity).toString()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        val builder: AlertDialog.Builder
        val contentView: View
        when (v.id) {
            R.id.paper_row_number -> {
                if (mDialog != null) {
                    mDialog?.dismiss()
                }
                builder = AlertDialog.Builder(activity)
                contentView = LayoutInflater.from(activity).inflate(R.layout.include_dialog_numberpicker, null)
                builder.setView(contentView)
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    SharePreferenceUtils.putInt(activity,
                            SharePreferenceConfig.ROW_NUM, mNumberPicker!!.value)
                    mPaperRowNumText.text = mNumberPicker?.value.toString()
                    if (activity is MainActivity) {
                        (activity as MainActivity).refreshMainFragment()
                    }
                    mDialog?.dismiss()
                }
                mDialog = builder.create()
                mNumberPicker = contentView.findViewById(R.id.number_picker) as NumberPicker
                mNumberPicker?.minValue = 2
                mNumberPicker?.maxValue = 5
                mNumberPicker?.value = AppPreferenceUtils.getRowNum(activity)
                mDialog?.show()
            }
            R.id.set_max_undo -> {
                if (mDialog != null) {
                    mDialog?.dismiss()
                }
                builder = AlertDialog.Builder(activity)
                contentView = LayoutInflater.from(activity).inflate(R.layout.include_dialog_numberpicker, null)
                builder.setView(contentView)
                builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    SharePreferenceUtils.putInt(activity,
                            SharePreferenceConfig.MAX_UNDO, mNumberPicker!!.value)
                    mMaxUndoText.text = mNumberPicker?.value.toString()
                    mDialog?.dismiss()
                }
                mDialog = builder.create()
                mNumberPicker = contentView.findViewById(R.id.number_picker) as NumberPicker
                mNumberPicker?.minValue = 10
                mNumberPicker?.maxValue = 200
                mNumberPicker?.value = AppPreferenceUtils.getMaxUndo(activity)
                mDialog?.show()
            }
            R.id.action_help -> {
                startActivity(HelpActivity::class.java)
            }
        }
    }
}