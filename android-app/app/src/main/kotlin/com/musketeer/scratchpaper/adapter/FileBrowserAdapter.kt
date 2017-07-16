package com.musketeer.scratchpaper.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.view.TouchImageView
import java.io.File

/**
 * Created by zhongxuqi on 10/07/2017.
 */
class FileBrowserAdapter : PagerAdapter {
    companion object {
        val TAG = "FileBrowserAdapter"
    }

    val context : Context
    val fileList: List<File>
    val fileViewList: MutableList<View>
    var listener : View.OnClickListener? = null

    constructor(context: Context, fileList: List<File>) {
        this.context = context
        this.fileList = fileList
        this.fileViewList = mutableListOf()
        for (paper in fileList) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_browser, null)
            view.tag = paper
            val content = view.findViewById(R.id.file_content) as TouchImageView
            content.setImageBitmap(BitmapFactory.decodeFile(paper.absolutePath)
                    .copy(Bitmap.Config.ARGB_8888, true))
            content.tag = paper
            content.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View) {
                    listener?.onClick(v)
                }
            })
            this.fileViewList.add(view)
        }
    }

    fun reloadViewAt(index : Int) {
        if (index > this.fileList.size) {
            return
        }
        val paper = this.fileList[index]
        val content = this.fileViewList[index].findViewById(R.id.file_content) as TouchImageView
        content.setImageBitmap(BitmapFactory.decodeFile(paper.absolutePath)
                .copy(Bitmap.Config.ARGB_8888, true))
        content.tag = paper
        content.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View) {
                listener?.onClick(v)
            }
        })
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return fileViewList.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        container?.addView(fileViewList[position])
        return fileViewList[position]
    }

    override fun getItemPosition(`object`: Any?): Int {
        return this.fileViewList.indexOf(`object`)
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(fileViewList[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return position.toString()
    }

    fun getFilePosition(fileName: String) : Int {
        for (i in 0..(fileViewList.size-1)) {
            val fileInfo = fileViewList[i].tag
            if (fileInfo is File && fileInfo.name.contains(fileName)) {
                return i
            }
        }
        return 0
    }
}