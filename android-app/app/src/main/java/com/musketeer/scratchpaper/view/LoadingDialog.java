package com.musketeer.scratchpaper.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.musketeer.baselibrary.view.RotateImageView;
import com.musketeer.scratchpaper.R;

public class LoadingDialog extends Dialog{
	private RotateImageView rotateImage;
	private TextView mMessageText;

	public LoadingDialog(Context context) {
		super(context, R.style.BaseDialog);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void init() {
		setContentView(R.layout.base_loadingdialog);
		initView();
	}
	
	public void initView() {
		rotateImage=(RotateImageView) findViewById(R.id.loading_image);
		mMessageText=(TextView) findViewById(R.id.content);
	}
	
	/**
	 * 设置文字显示
	 * @param content
	 */
	public void setMessage(String content) {
		mMessageText.setText(content);
	}
	
	public void setMessage(int resId) {
		mMessageText.setText(getContext().getResources().getString(resId));
	}

}
