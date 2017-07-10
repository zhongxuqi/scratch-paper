package com.musketeer.scratchpaper.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muskeeter.base.acitivity.BaseActivity
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.EditPaperActivity
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.LogUtils
import com.musketeer.scratchpaper.view.TouchImageView
import java.io.File

/**
 * Created by zhongxuqi on 10/07/2017.
 */
class PaperBrowserAdapter: PagerAdapter {
    companion object {
        val TAG = "PaperBrowserAdapter"
    }

    val context : Context
    val paperList : MutableList<View>

    constructor(context: Context, paperList: List<File>) {
        this.context = context
        this.paperList = mutableListOf()
        for (paper in paperList) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_paper_browser, null)
            view.tag = paper
            val content = view.findViewById(R.id.paper_content) as TouchImageView
            content.setImageBitmap(BitmapFactory.decodeFile(paper.absolutePath)
                    .copy(Bitmap.Config.ARGB_8888, true))
            content.tag = paper
            content.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View) {
                    val paperFile = v.tag as File
                    if (context is BaseActivity) {
                        val bundle = Bundle()
                        bundle.putString("paper_name", paperFile.name)
                        val intent = Intent()
                        intent.putExtras(bundle)
                        intent.setClass(context, EditPaperActivity::class.java)
                        context.startActivityForResult(intent, Config.ACTION_EDIT_PAPER)
                    }
                }
            })
            this.paperList.add(view)
        }
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return paperList.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        container?.addView(paperList[position])
        return paperList[position]
    }

    override fun getItemPosition(`object`: Any?): Int {
        return this.paperList.indexOf(`object`)
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(paperList[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return position.toString()
    }

    fun getPaperPosition(paperName : String) : Int {
        for (i in 0..(paperList.size-1)) {
            val fileInfo = paperList[i].tag
            if (fileInfo is File && fileInfo.name.contains(paperName)) {
                return i
            }
        }
        return 0
    }
}