package com.musketeer.scratchpaper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.scratchpaper.MainApplication;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.activity.settings.SettingsActivity;
import com.musketeer.scratchpaper.adapter.PaperListAdapter;
import com.musketeer.scratchpaper.common.SharePreferenceConfig;
import com.musketeer.scratchpaper.paperfile.PaperFileUtils;
import com.musketeer.scratchpaper.utils.AppPreferenceUtils;
import com.musketeer.scratchpaper.view.BaseDialog;
import com.musketeer.scratchpaper.view.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends BaseActivity implements
		OnItemClickListener, OnItemLongClickListener{
	private static final int ADD_NEW_PAPER=1;
	private static final int EDIT_NEW_PAPER=2;
	private static final int CHANGE_SETTINGS=3;
	
//	private DrawerLayout mDrawerLayout;
//	private ActionBarDrawerToggle mDrawerToggle;
	
//	private Button mNewScratchPaper;
//	private Button mSetting;
//	private Button mQuit;
	
	private ImageView addNewScratchPaper;
	
	private GridView mSavedPaperList;
	private PaperListAdapter mAdapter;
	private List<String> mPaperList;
	
//	private BaseDialog mDialog;
//	private LoadingDialog mLoadingDialog;
	private AlertDialog mDialog;
	private AlertDialog mLoadingDialog;
	private TextView loadingText;

	private Handler handler =new Handler() {
		@Override
		//当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//只要执行到这里就关闭对话框
			dismissLoadingDialog();
			mAdapter.notifyDataSetChanged();
			showCustomToast(getResources().getString(R.string.rename_success));
		}
	};

	@Override
	public void setContentView(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
		loadingText = (TextView) view.findViewById(R.id.loading_text);
		builder.setView(view);
		mLoadingDialog = builder.create();
		setContentView(R.layout.activity_main);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
//		mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
//		mDrawerLayout.openDrawer(Gravity.LEFT);
		
//		mNewScratchPaper=(Button) findViewById(R.id.new_scratch_paper);
//		mSetting=(Button) findViewById(R.id.setting);
//		mQuit=(Button) findViewById(R.id.quit);
		
		addNewScratchPaper=(ImageView) findViewById(R.id.add_new_scratch_paper);
		
		mSavedPaperList=(GridView) findViewById(R.id.paper_gridlist);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
//		mNewScratchPaper.setOnClickListener(this);
//		mSetting.setOnClickListener(this);
//		mQuit.setOnClickListener(this);
		
		addNewScratchPaper.setOnClickListener(this);
		
		mSavedPaperList.setOnItemClickListener(this);
		mSavedPaperList.setOnItemLongClickListener(this);
	}
	
	public void refreshViews() {
		mPaperList= PaperFileUtils.readPaperList();
		mAdapter=new PaperListAdapter(this,mPaperList);
		mSavedPaperList.setAdapter(mAdapter);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		getSupportActionBar().setLogo(R.mipmap.icon_small);
		getSupportActionBar().setIcon(R.mipmap.icon_small);
		
		//设置抽屉
//		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
//		mDrawerLayout.post(new Runnable() {
//	        @Override
//	        public void run() {
//	            mDrawerToggle.syncState();
//	        }
//	    });
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		mSavedPaperList.setNumColumns(AppPreferenceUtils.getRowNum(this));
		
		//read paper files
		mPaperList= PaperFileUtils.readPaperList();
		mAdapter=new PaperListAdapter(this,mPaperList);
		mSavedPaperList.setAdapter(mAdapter);

		checkConfig();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.setting:
			startActivityForResult(SettingsActivity.class,CHANGE_SETTINGS);
			break;
		case R.id.help:
			startActivity(HelpActivity.class);
			break;
		case android.R.id.home:
//			if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
//				mDrawerLayout.closeDrawer(Gravity.LEFT);
//			} else {
//				mDrawerLayout.openDrawer(Gravity.LEFT);
//			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_new_scratch_paper:
			Animation myAnimation=AnimationUtils.loadAnimation(this, R.anim.view_scale_larger);
			addNewScratchPaper.startAnimation(myAnimation);
			myAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					startActivityForResult(EditPaperActivity.class,ADD_NEW_PAPER);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
			});
			break;
		case R.id.setting:
			startActivityForResult(SettingsActivity.class,CHANGE_SETTINGS);
			break;
		case R.id.help:
			startActivity(HelpActivity.class);
			break;
		}
	}

	/**
	 * 检查配置的有效性
	 */
	private void checkConfig() {
		// TODO Auto-generated method stub
		try {
			getResources().getDrawable(SharePreferenceUtils.getInt(this,
					SharePreferenceConfig.PAPER,
					MainApplication.DEFAULT_PAPER));
			getResources().getDrawable(SharePreferenceUtils.getInt(this,
					SharePreferenceConfig.DESK,
					MainApplication.DEFAULT_DESK));
		} catch (NotFoundException e) {
			SharePreferenceUtils.putInt(this, SharePreferenceConfig.MAX_UNDO, 
					MainApplication.PAPER_MAX_UNDO);
			SharePreferenceUtils.putInt(this, SharePreferenceConfig.ROW_NUM, 3);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		final AdapterView<?> mListView=parent;
		final int selectPosition=position;
		
		if (mDialog!=null) {
			mDialog.dismiss();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View contentView = LayoutInflater.from(this).inflate(R.layout.include_saved_paper_action, null);
		builder.setView(contentView);
		mDialog=builder.create();
		//查看内容
		TextView lookButton=(TextView) contentView.findViewById(R.id.look);
		lookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("paper_name", (String) mListView.getAdapter().getItem(selectPosition));
				startActivityForResult(BrowsePaperActivity.class, bundle, EDIT_NEW_PAPER);
				mDialog.dismiss();
			}

		});
		//编辑内容
		TextView editButton=(TextView) contentView.findViewById(R.id.edit);
		editButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle=new Bundle();
				bundle.putString("paper_name",(String)mListView.getAdapter().getItem(selectPosition));
				startActivityForResult(EditPaperActivity.class,bundle,EDIT_NEW_PAPER);
				mDialog.dismiss();
			}
			
		});
		//重命名
		TextView renameButton=(TextView) contentView.findViewById(R.id.rename);
		renameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(getResources().getString(R.string.rename));
				builder.setView(LayoutInflater.from(MainActivity.this).inflate(R.layout.include_dialog_edittext, null));
				builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialog.dismiss();
					}
				});
				builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialog.dismiss();

						final EditText rename = (EditText) mDialog.findViewById(R.id.edittext);
						if (rename.getText().toString() == null ||
								rename.getText().toString().length() == 0) {
							showCustomToast(R.string.name_no_null);
							return;
						}

						showLoadingDialogNotCancel(R.string.saving);

						new Thread(new Runnable() {
							@Override
							public void run() {
								String paper_name = rename.getText().toString();

								if (!paper_name.equals(mAdapter.getItem(selectPosition))) {
									//读取原文件
									Bitmap bitmap = PaperFileUtils.getPaper(mAdapter.getItem(selectPosition));

									//保存文件
									PaperFileUtils.savePaper(bitmap, paper_name);

									//删除文件
									PaperFileUtils.deletePaper(mAdapter.getItem(selectPosition));

									mPaperList.remove(selectPosition);
									mPaperList.add(selectPosition, paper_name);
								}
								handler.sendEmptyMessage(0);
							}
						}).start();
					}
				});
				mDialog = builder.create();
				mDialog.show();
			}

		});
		mDialog.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		final AdapterView<?> mListView=parent;
		final int selectPosition=position;
		if (mDialog!=null) {
			mDialog.dismiss();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.affirm_delete));
		builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDialog.dismiss();
			}
		});
		builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PaperFileUtils.deletePaper((String) mListView.getAdapter().getItem(selectPosition));
				mPaperList.remove(selectPosition);
				mAdapter.notifyDataSetChanged();
				mDialog.dismiss();
			}
		});
		mDialog=builder.create();
		mDialog.show();
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ADD_NEW_PAPER:
			refreshViews();
			break;
		case EDIT_NEW_PAPER:
			refreshViews();
			break;
		case CHANGE_SETTINGS:
			initData();
			break;
		}
	}

	/**
	 * 设置LoadingDialog并显示
	 * @param resId
	 */
	protected void showLoadingDialog(int resId) {
		mLoadingDialog.setCancelable(true);
		loadingText.setText(resId);
		mLoadingDialog.show();
	}

	protected void showLoadingDialog(String message) {
		mLoadingDialog.setCancelable(true);
		loadingText.setText(message);
		mLoadingDialog.show();
	}

	protected void showLoadingDialogNotCancel(int resId) {
		mLoadingDialog.setCancelable(false);
		loadingText.setText(resId);
		mLoadingDialog.show();
	}

	protected void showLoadingDialogNotCancel(String message) {
		mLoadingDialog.setCancelable(false);
		loadingText.setText(message);
		mLoadingDialog.show();
	}

	/**
	 * 关闭LoadingDialog
	 */
	protected void dismissLoadingDialog() {
		mLoadingDialog.dismiss();
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
