package com.musketeer.scratchpaper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.adapter.PaperListAdapter;
import com.musketeer.scratchpaper.common.SharePreferenceConfig;
import com.musketeer.scratchpaper.paperfile.PaperFileUtils;
import com.musketeer.scratchpaper.utils.AppPreferenceUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class PaperSelectActivity extends BaseActivity implements
		OnItemClickListener{
	
	private GridView mSavedPaperList;
	private PaperListAdapter mAdapter;
	private List<String> mPaperList;

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setResult(RESULT_CANCELED);
		setContentView(R.layout.activity_paper_select);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.paper_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mSavedPaperList=(GridView) findViewById(R.id.paper_gridlist);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mSavedPaperList.setOnItemClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mSavedPaperList.setNumColumns(AppPreferenceUtils.getRowNum(this));
		
		//read paper files
		mPaperList= PaperFileUtils.readPaperList();
		mAdapter=new PaperListAdapter(this,mPaperList);
		mSavedPaperList.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		LogUtils.d("zxq", "onItemClick: " + PaperWidgetProvider.TAG);
		intent.putExtra("widget_name", PaperWidgetProvider.TAG);
		intent.putExtra("paper_name", mPaperList.get(position));
		sendBroadcast(intent);
		
		SharePreferenceUtils.putString(this, SharePreferenceConfig.WIDGET_PAPER_NAME, mPaperList.get(position));
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
