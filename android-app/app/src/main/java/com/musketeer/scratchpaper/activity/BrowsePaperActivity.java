package com.musketeer.scratchpaper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.baselibrary.view.TouchImageView;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.common.SharePreferenceConfig;
import com.musketeer.scratchpaper.paperfile.PaperFileUtils;
import com.musketeer.scratchpaper.utils.FileUtils;
import com.umeng.analytics.MobclickAgent;

public class BrowsePaperActivity extends BaseActivity {
	private static final int EDIT_PAPER=1;
	
	private TouchImageView mPaperBrowser;
	private String mPaperName;

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_browse_paper);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.mipmap.icon_small);
		getMenuInflater().inflate(R.menu.browse_paper, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.edit:
			Bundle bundle=new Bundle();
			bundle.putString("paper_name", mPaperName);
			startActivityForResult(EditPaperActivity.class,bundle,EDIT_PAPER);
			break;
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mPaperBrowser=(TouchImageView) findViewById(R.id.paper_browser);
		mPaperBrowser.setMinZoom(.5f);
		mPaperBrowser.setMaxZoom(10f);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		//init paper content
		Bundle bunle=getIntent().getExtras();
		if (bunle!=null&&bunle.getString("paper_name")!=null) {
			mPaperName=bunle.getString("paper_name");
			if (FileUtils.isFileExist(PaperFileUtils.getPaperPath(mPaperName))) {
				mPaperBrowser.setImageBitmap(PaperFileUtils.getPaper(mPaperName));
			} else {
				PaperFileUtils.deletePaper(mPaperName);
				finish();
			}
		} else {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case EDIT_PAPER:
			initData();
			
			Intent intent=new Intent();
			intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
			intent.putExtra("widget_name", PaperWidgetProvider.TAG);
			intent.putExtra("paper_name", SharePreferenceUtils.getString(this, SharePreferenceConfig.WIDGET_PAPER_NAME, ""));
			sendBroadcast(intent);
			break;
		}
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
