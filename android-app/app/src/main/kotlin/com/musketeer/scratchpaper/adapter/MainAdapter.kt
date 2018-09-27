package com.musketeer.scratchpaper.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.bean.BitmapGroup
import com.musketeer.scratchpaper.bean.ImageGroup

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class MainAdapter: RecyclerView.Adapter<MainViewHolder> {
    companion object {
        val TAG = "MainAdapter"
    }
    var context: Context? = null
    var imageGroupList: MutableList<BitmapGroup> = mutableListOf()
        set(value) {
            imageGroupList.clear()
            imageGroupList.addAll(value)
            notifyDataSetChanged()
        }
    var onItemClickListener: View.OnClickListener? = null
    var onItemLongClickListener: View.OnLongClickListener? = null

    constructor(context: Context) {
        this.context = context
    }

    fun removeItem(paper_name: String) {
        var hasMatch = false
        var isInner = false
        var indexOut = 0
        var indexInner = 0
        imageGroupList.forEachIndexed { indexOfList, paperGroup ->
            paperGroup.imageList.forEachIndexed { indexOfGroup, s ->
                if (paperGroup.imageNameMap.containsKey(s) && paperGroup.imageNameMap.get(s)!!.contains(paper_name)) {
                    hasMatch = true
                    if (paperGroup.imageList.size == 1) {
                        indexOut = indexOfList
                    } else {
                        isInner = true
                        indexOut = indexOfList
                        indexInner = indexOfGroup
                    }
                }
            }
        }
        if (hasMatch) {
            if (isInner) {
                imageGroupList[indexOut].imageList.removeAt(indexInner)
            } else {
                imageGroupList.removeAt(indexOut)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imageGroupList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_list_item, null)
        view.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return MainViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder?.bindData(imageGroupList[position].timeOfData, imageGroupList[position].imageList, imageGroupList[position].imageNameMap,
                position == 0, position + 1 == imageGroupList.size)
    }
}