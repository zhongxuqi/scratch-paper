/**   
* @Title: BaseDialog.java 
* @Package com.musketeer.scratchpaper.view 
*
* @author musketeer zhongxuqi@163.com  
* @date 2014-11-15 下午8:48:05 
* @version V1.0   
*/
package com.musketeer.scratchpaper.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musketeer.scratchpaper.R;

/**
 * @author zhongxuqi
 *
 */
public class BaseDialog extends Dialog implements
		View.OnClickListener {
	
	private TextView mTitle;
	private RelativeLayout mMainContent;
	private Button mButton1,mButton2,mButton3;

	/**
	 * @param context
	 */
	public BaseDialog(Context context) {
		super(context,R.style.BaseDialog);
		// TODO Auto-generated constructor stub
		init();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	public void init() {
		setContentView(R.layout.base_dialog);
		initView();
	}
	
	public void initView() {
		mTitle=(TextView) findViewById(R.id.title);
		mMainContent=(RelativeLayout) findViewById(R.id.main_content);
		mButton1=(Button) findViewById(R.id.button1);
		mButton2=(Button) findViewById(R.id.button2);
		mButton3=(Button) findViewById(R.id.button3);
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title) {
		mTitle.setText(title);
	}
	
	/**
	 * 设置button1
	 * @param text
	 * @param l
	 */
	public void setButton1(String text, View.OnClickListener l) {
		mButton1.setText(text);
		mButton1.setOnClickListener(l);
		mButton1.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置button2
	 * @param text
	 * @param l
	 */
	public void setButton2(String text, View.OnClickListener l) {
		mButton2.setText(text);
		mButton2.setOnClickListener(l);
		mButton2.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置button3
	 * @param text
	 * @param l
	 */
	public void setButton3(String text, View.OnClickListener l) {
		mButton3.setText(text);
		mButton3.setOnClickListener(l);
		mButton3.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置内容
	 * @param layoutId
	 */
	public void setDialogContentView(int layoutId) {
		View view=getLayoutInflater().inflate(layoutId, null);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(params);
		mMainContent.removeAllViews();
		mMainContent.addView(view);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
