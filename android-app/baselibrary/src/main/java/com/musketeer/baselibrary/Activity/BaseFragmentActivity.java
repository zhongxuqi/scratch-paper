/**   
* @Title: BaseFragmentActivity.java 
* @Package com.musketeer.lib.activity 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-15 下午3:44:18 
* @version V1.0   
*/
package com.musketeer.baselibrary.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.musketeer.baselibrary.util.LogUtils;

import butterknife.ButterKnife;

/**
 * @author zhongxuqi
 *
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements OnClickListener, BaseUITask{
	public static final String TAG = "Musketeer_BaseFragmentActivity";
	
	/**屏幕的宽度*/
	protected int mScreenWidth;
	/**屏幕高度*/
	protected int mScreenHeight;
	/**屏幕密度*/
	protected float mDensity;
	
	private Toast mToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;

		setContentView(savedInstanceState);
		ButterKnife.bind(this);
		initView();
		initEvent();
		initData();
	}
	
	protected void startActivity(Class<?> cls) {
		Intent intent=new Intent();
		intent.setClass(this, cls);
		startActivity(intent);
	}
	
	protected void startActivity(Class<?> cls,Bundle bundle) {
		Intent intent=new Intent();
		intent.putExtras(bundle);
		intent.setClass(this, cls);
		startActivity(intent);
	}
	
	protected void startActivityForResult(Class<?> cls,int requestCode) {
		Intent intent=new Intent();
		intent.setClass(this, cls);
		super.startActivityForResult(intent, requestCode);
	}
	
	protected void startActivityForResult(Class<?> cls,Bundle bundle,int requestCode) {
		Intent intent=new Intent();
		intent.putExtras(bundle);
		intent.setClass(this, cls);
		super.startActivityForResult(intent, requestCode);
	}
	
	protected void showCustomDebug(String text) {
		LogUtils.d(TAG, text);
	}
	
	protected void showCustomToast(String text) {
		if (mToast!=null) {
			mToast.cancel();
		}
		mToast=Toast.makeText(this, text, Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	protected void showCustomToast(int resId) {
		if (mToast!=null) {
			mToast.cancel();
		}
		mToast=Toast.makeText(this, getResources().getString(resId), Toast.LENGTH_SHORT);
		mToast.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	public int getScreenWidth() {
		return mScreenWidth;
	}

	public int getScreenHeight() {
		return mScreenHeight;
	}

}
