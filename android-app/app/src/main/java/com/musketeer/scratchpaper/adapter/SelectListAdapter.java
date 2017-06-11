/**   
* @Title: SelectListAdapter.java 
* @Package com.musketeer.scratchpaper.adapter 
*
* @author musketeer zhongxuqi@163.com  
* @date 2014-11-16 下午6:46:07 
* @version V1.0   
*/
package com.musketeer.scratchpaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.musketeer.scratchpaper.R;

import java.util.List;

/**
 * @author zhongxuqi
 *
 */
public class SelectListAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<Integer> mImageList;
	
	private int selectId=0;
	
	public SelectListAdapter(Context context, List<Integer> list) {
		mContext=context;
		mImageList=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImageList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mImageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder mHolder;
		if (convertView==null) {
			mHolder=new Holder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_select_set, null);
			mHolder.mPaperImage=(ImageView) convertView.findViewById(R.id.item_content);
			
			convertView.setTag(mHolder);
		} else {
			mHolder=(Holder) convertView.getTag();
		}
		
		mHolder.mPaperImage.setImageResource((int)mImageList.get(position));
		if (selectId==(int)mImageList.get(position)) {
			convertView.setBackgroundResource(R.color.deepskyblue);
		} else {
			convertView.setBackgroundResource(R.color.transparent);
		}
		
		return convertView;
	}
	
	class Holder {
		ImageView mPaperImage;
	}

	/**
	 * @return the selectId
	 */
	public int getSelectId() {
		return selectId;
	}

	/**
	 * @param selectId the selectId to set
	 */
	public void setSelectId(int selectId) {
		this.selectId = selectId;
		notifyDataSetChanged();
	}

}
