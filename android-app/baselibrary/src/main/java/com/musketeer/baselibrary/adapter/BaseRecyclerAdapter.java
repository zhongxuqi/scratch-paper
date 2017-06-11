package com.musketeer.baselibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.musketeer.baselibrary.bean.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public abstract class BaseRecyclerAdapter<T extends BaseEntity, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context mContext;
    protected List<T> mDataList;

    protected OnItemDeleteListener<T> deleteListener;
    protected OnItemClickListener<T> clickListener;

    public BaseRecyclerAdapter(Context context) {
        super();
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    public BaseRecyclerAdapter(Context context, List<T> list) {
        super();
        this.mContext = context;
        if (list != null) {
            this.mDataList = list;
        } else {
            this.mDataList = new ArrayList<>();
        }
    }

    public void add(T item){
        if (item == null) return;
        mDataList.add(item);
    }

    public void addAll(List<T> list) {
        if (list == null) return;
        mDataList.addAll(list);
    }

    public void refreshList(List<T> list) {
        if (list == null) return;
        mDataList.clear();
        mDataList.addAll(list);
    }

    public void deleteItem(T item) {
        deleteItemById(item.getId());
    }

    public void deleteItemById(int id) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getId() == id) {
                mDataList.remove(i);
                return;
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface OnItemClickListener<T> {
        void onClick(T item);
    }

    public interface OnItemDeleteListener<T> {
        void onItemDelete(T item);
    }
}
