package com.musketeer.scratchpaper.activity.settings;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.adapter.SelectListAdapter;
import com.musketeer.scratchpaper.common.SharePreferenceConfig;
import com.musketeer.scratchpaper.utils.AppPreferenceUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class SetDeskActivity extends BaseActivity implements OnItemClickListener{
	
	private GridView mDeskBGListView;
	private SelectListAdapter mAdapter;
	private List<Integer> mDeskIdsList;

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_set_desk);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.mipmap.icon_small);
		getMenuInflater().inflate(R.menu.set_desk, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mDeskBGListView=(GridView) findViewById(R.id.image_gridlist);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mDeskBGListView.setOnItemClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
		//read desk images
		mDeskIdsList=new ArrayList<Integer>();
		TypedArray images = getResources().obtainTypedArray(R.array.desk_images);
		for (int i=0;i<images.length();i++) {
			mDeskIdsList.add(images.getResourceId(i, 0));
		}
		images.recycle();
		mAdapter=new SelectListAdapter(this, mDeskIdsList);
		mAdapter.setSelectId(AppPreferenceUtils.getDeskChoose(this));
		mDeskBGListView.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		mAdapter.setSelectId(mDeskIdsList.get(position));
		SharePreferenceUtils.putInt(this, SharePreferenceConfig.DESK, mDeskIdsList.get(position));
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
