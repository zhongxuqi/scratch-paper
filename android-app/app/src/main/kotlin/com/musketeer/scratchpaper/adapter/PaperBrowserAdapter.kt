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
    val paperList : List<File>
    val paperViewList: MutableList<View>
    var listener : View.OnClickListener? = null

    constructor(context: Context, paperList: List<File>) {
        this.context = context
        this.paperList = paperList
        this.paperViewList = mutableListOf()
        for (paper in paperList) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_paper_browser, null)
            view.tag = paper
            val content = view.findViewById(R.id.paper_content) as TouchImageView
            content.setImageBitmap(BitmapFactory.decodeFile(paper.absolutePath)
                    .copy(Bitmap.Config.ARGB_8888, true))
            content.tag = paper
            content.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View) {
                    listener?.onClick(v)
                }
            })
            this.paperViewList.add(view)
        }
    }

    fun reloadViewAt(index : Int) {
        if (index > this.paperList.size) {
            return
        }
        val paper = this.paperList[index]
        val content = this.paperViewList[index].findViewById(R.id.paper_content) as TouchImageView
        content.setImageBitmap(BitmapFactory.decodeFile(paper.absolutePath)
                .copy(Bitmap.Config.ARGB_8888, true))
        content.tag = paper
        content.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View) {
                listener?.onClick(v)
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
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return paperViewList.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        container?.addView(paperViewList[position])
        return paperViewList[position]
    }

    override fun getItemPosition(`object`: Any?): Int {
        return this.paperViewList.indexOf(`object`)
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(paperViewList[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return position.toString()
    }

    fun getPaperPosition(paperName : String) : Int {
        for (i in 0..(paperViewList.size-1)) {
            val fileInfo = paperViewList[i].tag
            if (fileInfo is File && fileInfo.name.contains(paperName)) {
                return i
            }
        }
        return 0
    }
}