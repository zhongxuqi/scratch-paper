package com.musketeer.scratchpaper.activity.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.scratchpaper.MainApplication;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.common.SharePreferenceConfig;
import com.musketeer.scratchpaper.utils.AppPreferenceUtils;
import com.musketeer.scratchpaper.view.BaseDialog;
import com.umeng.analytics.MobclickAgent;

public class SettingsActivity extends BaseActivity {
	
	//base setting
	private Spinner mPaperSizeSelector;
	private TextView mPaperRowNumText;
	
	//senior setting
	private TextView mMaxUndoText;
	
	private AlertDialog mDialog;
	private NumberPicker mNumberPicker;

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.mipmap.icon_small);
//		getActionBar().setTitle(getResources().getString(R.string.title_activity_settings));
		getMenuInflater().inflate(R.menu.settings, menu);
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
		mPaperSizeSelector=(Spinner) findViewById(R.id.paper_size_selector);
		mPaperRowNumText=(TextView) findViewById(R.id.paper_row_number_text);
		mMaxUndoText=(TextView) findViewById(R.id.max_undo);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		findViewById(R.id.paper_row_number).setOnClickListener(this);
		findViewById(R.id.set_max_undo).setOnClickListener(this);
		findViewById(R.id.back_to_defalut).setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		ArrayAdapter<String> mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, 
				getResources().getStringArray(R.array.paper_size));
		mPaperSizeSelector.setAdapter(mAdapter);
		mPaperSizeSelector.setSelection(SharePreferenceUtils.getInt(this, SharePreferenceConfig.PAPER_SIZE, 0));
		mPaperSizeSelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				SharePreferenceUtils.putInt(SettingsActivity.this, SharePreferenceConfig.PAPER_SIZE, position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mPaperRowNumText.setText(""+ AppPreferenceUtils.getRowNum(this));
		mMaxUndoText.setText(""+ AppPreferenceUtils.getMaxUndo(this));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		AlertDialog.Builder builder;
		View contentView;
		switch (v.getId()) {
		case R.id.paper_row_number:
			if (mDialog!=null) {
				mDialog.dismiss();
			}
			builder = new AlertDialog.Builder(this);
			contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_numberpicker, null);
			builder.setView(contentView);
			builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDialog.dismiss();
				}
			});
			builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharePreferenceUtils.putInt(SettingsActivity.this,
							SharePreferenceConfig.ROW_NUM, mNumberPicker.getValue());
					mPaperRowNumText.setText(""+mNumberPicker.getValue());
					mDialog.dismiss();
				}
			});
			mDialog=builder.create();
			mNumberPicker=(NumberPicker) contentView.findViewById(R.id.number_picker);
			mNumberPicker.setMinValue(2);
			mNumberPicker.setMaxValue(5);
			mNumberPicker.setValue(AppPreferenceUtils.getRowNum(this));
			mDialog.show();
			break;
		case R.id.set_max_undo:
			if (mDialog!=null) {
				mDialog.dismiss();
			}
			builder = new AlertDialog.Builder(this);
			contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_numberpicker, null);
			builder.setView(contentView);
			builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDialog.dismiss();
				}
			});
			builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharePreferenceUtils.putInt(SettingsActivity.this,
							SharePreferenceConfig.MAX_UNDO, mNumberPicker.getValue());
					mMaxUndoText.setText(""+mNumberPicker.getValue());
					mDialog.dismiss();
				}
			});
			mDialog=builder.create();
			mNumberPicker=(NumberPicker) contentView.findViewById(R.id.number_picker);
			mNumberPicker.setMinValue(10);
			mNumberPicker.setMaxValue(200);
			mNumberPicker.setValue(AppPreferenceUtils.getMaxUndo(this));
			mDialog.show();
			break;
		case R.id.back_to_defalut:
			if (mDialog!=null) {
				mDialog.dismiss();
			}
			builder = new AlertDialog.Builder(this);
			contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_content, null);
			builder.setView(contentView);
			builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDialog.dismiss();
				}
			});
			builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharePreferenceUtils.putInt(SettingsActivity.this, SharePreferenceConfig.ROW_NUM, 3);
					mPaperRowNumText.setText("3");
					SharePreferenceUtils.putInt(SettingsActivity.this, SharePreferenceConfig.MAX_UNDO,
							MainApplication.PAPER_MAX_UNDO);
					mMaxUndoText.setText(""+MainApplication.PAPER_MAX_UNDO);
					showCustomToast(getResources().getString(R.string.set_success));
					mDialog.dismiss();
				}
			});
			mDialog=builder.create();
			TextView textview=(TextView) contentView.findViewById(R.id.alert_content);
			textview.setText(getResources().getString(R.string.affirm_back));
			mDialog.show();
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
