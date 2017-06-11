/**
 * @Title: SelectListAdapter.java
 * *
 * @Package com.musketeer.scratchpaper.adapter
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-16 下午6:46:07
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

import com.musketeer.scratchpaper.R

/**
 * @author zhongxuqi
 */
class SelectListAdapter(private val mContext: Context, private val mImageList: List<Int>) : BaseAdapter() {

    /**
     * @return the selectId
     */
    /**
     * @param selectId the selectId to set
     */
    var selectId = 0
        set(selectId) {
            field = selectId
            notifyDataSetChanged()
        }

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return mImageList.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return mImageList[position]
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_set, null)
            mHolder.mPaperImage = convertView!!.findViewById(R.id.item_content) as ImageView

            convertView.tag = mHolder
        } else {
            mHolder = convertView.tag as Holder
        }

        mHolder.mPaperImage!!.setImageResource(mImageList[position])
        if (this.selectId == mImageList[position]) {
            convertView.setBackgroundResource(R.color.deepskyblue)
        } else {
            convertView.setBackgroundResource(R.color.transparent)
        }

        return convertView
    }

    internal inner class Holder {
        var mPaperImage: ImageView? = null
    }

}
