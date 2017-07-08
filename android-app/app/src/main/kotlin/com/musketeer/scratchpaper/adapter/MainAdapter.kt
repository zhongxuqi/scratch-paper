package com.musketeer.scratchpaper.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.bean.PaperGroup

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class MainAdapter: RecyclerView.Adapter<MainViewHolder> {
    companion object {
        val TAG = "MainAdapter"
    }
    var context: Context? = null
    var paperGroupList: MutableList<PaperGroup> = mutableListOf()
        set(value) {
            paperGroupList.clear()
            paperGroupList.addAll(value)
            notifyDataSetChanged()
        }
    var onItemClickListener: View.OnClickListener? = null
    var onItemLongClickListener: View.OnLongClickListener? = null

    constructor(context: Context) {
        this.context = context
    }

    fun replaceItem(old_paper_name: String, new_paper_name: String) {
        paperGroupList.forEachIndexed { _, paperGroup ->
            paperGroup.paperList.forEachIndexed { index, s ->
                if (s == old_paper_name) {
                    paperGroup.paperList.removeAt(index)
                    paperGroup.paperList.add(index, new_paper_name)
                }
            }
        }
    }

    fun removeItem(paper_name: String) {
        paperGroupList.forEachIndexed { indexOfList, paperGroup ->
            paperGroup.paperList.forEachIndexed { indexOfGroup, s ->
                if (s == paper_name) {
                    paperGroup.paperList.removeAt(indexOfGroup)
                    if (paperGroup.paperList.size == 0) {
                        paperGroupList.removeAt(indexOfList)
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return paperGroupList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_list_item, null)
        view.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return MainViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: MainViewHolder?, position: Int) {
        holder?.bindData(paperGroupList[position].timeOfData, paperGroupList[position].paperList, position == 0, position + 1 == paperGroupList.size)
    }
}