/**   
* @Title: PaperListAdapter.java 
* @Package com.musketeer.scratchpaper.adapter 
*
* @author musketeer zhongxuqi@163.com  
* @date 2014-11-16 下午2:04:31 
* @version V1.0   
*/
package com.musketeer.scratchpaper.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.paperfile.PaperFileUtils;

import java.util.List;

/**
 * @author zhongxuqi
 *
 */
public class PaperListAdapter extends BaseAdapter{
	private Context mContext;
	private List<String> mFileList;
	
	public PaperListAdapter(Context context, List<String> list) {
		mContext=context;
		mFileList=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFileList.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return mFileList.get(position);
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
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_paper_gridlist, null);
			mHolder.mPaperImage=(ImageView) convertView.findViewById(R.id.paper_content);
			mHolder.mPaperName=(TextView) convertView.findViewById(R.id.paper_name);
			
			convertView.setTag(mHolder);
		} else {
			mHolder=(Holder) convertView.getTag();
		}
		
		Bitmap bitmap= PaperFileUtils.getPaperThumbNail(mFileList.get(position));
		
		//如果没有这个文件或文件有误，就删除它
		if (bitmap==null) {
			PaperFileUtils.deletePaper(getItem(position));
			mFileList.remove(position);
			notifyDataSetChanged();
			return convertView;
		}
		
		mHolder.mPaperImage.setImageBitmap(bitmap);
		mHolder.mPaperName.setText(getItem(position));
		
		return convertView;
	}
	
	class Holder {
		ImageView mPaperImage;
		TextView mPaperName;
	}

}
