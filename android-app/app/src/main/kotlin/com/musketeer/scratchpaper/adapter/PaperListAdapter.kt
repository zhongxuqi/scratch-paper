/**
 * @Title: PaperListAdapter.java
 * *
 * @Package com.musketeer.scratchpaper.adapter
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-16 下午2:04:31
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.paperfile.PaperFileUtils

/**
 * @author zhongxuqi
 */
class PaperListAdapter(private val mContext: Context, private val mFileList: MutableList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return mFileList.size
    }

    override fun getItem(position: Int): String {
        // TODO Auto-generated method stub
        return mFileList[position]
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // TODO Auto-generated method stub
        val mHolder: Holder
        if (convertView == null) {
            mHolder = Holder()
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_paper_gridlist, null)
            mHolder.mPaperImage = convertView!!.findViewById(R.id.paper_content) as ImageView
            mHolder.mPaperName = convertView.findViewById(R.id.paper_name) as TextView

            convertView.tag = mHolder
        } else {
            mHolder = convertView.tag as Holder
        }

        val bitmap = PaperFileUtils.getPaperThumbNail(mFileList[position])

        //如果没有这个文件或文件有误，就删除它
        if (bitmap == null) {
            PaperFileUtils.deletePaper(getItem(position))
            mFileList.removeAt(position)
            notifyDataSetChanged()
            return convertView
        }

        mHolder.mPaperImage!!.setImageBitmap(bitmap)
        mHolder.mPaperName!!.text = getItem(position)

        return convertView
    }

    internal inner class Holder {
        var mPaperImage: ImageView? = null
        var mPaperName: TextView? = null
    }

}
