package com.musketeer.scratchpaper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.util.TimeUtils;
import com.musketeer.scratchpaper.MainApplication;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.adapter.PaperListAdapter;
import com.musketeer.scratchpaper.paperfile.PaperFileUtils;
import com.musketeer.scratchpaper.utils.AppPreferenceUtils;
import com.musketeer.scratchpaper.utils.FileUtils;
import com.musketeer.scratchpaper.view.ScratchPaperView;
import com.musketeer.scratchpaper.view.ScratchPaperView.DrawStroke;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.List;

public class EditPaperActivity extends BaseActivity implements OnItemClickListener{

	private static final String KEY_STORE_BITMAP="store_bitmap";
	private static final String KEY_STORE_STROKE="store_stroke";
	
	private MenuItem mPaint;
	
	private ScratchPaperView mScratchPaper;
	private DrawerLayout mDrawerLayout;
	
	private AlertDialog mDialog;
	private AlertDialog mLoadingDialog;
	private TextView loadingText;
	
    //save attribute
    private String paper_name;
    
    private ListView mSavedPaperList;
	private PaperListAdapter mAdapter;
	private List<String> mPaperList;
    
    private Handler handler =new Handler(){
    	@Override
    	//当有消息发送出来的时候就执行Handler的这个方法
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		//只要执行到这里就关闭对话框
    		refreshViews();
    		dismissLoadingDialog();
    		showCustomToast(getResources().getString(R.string.save_success));
    	}
	};
	
	private Handler ChangePaperHandler =new Handler(){
    	@Override
    	//当有消息发送出来的时候就执行Handler的这个方法
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		//只要执行到这里就关闭对话框
    		refreshViews();
    		if (paper_name!=null&&paper_name.length()>0) {
    			initPaperContent(paper_name);
    		} else {
    			mScratchPaper.setPaperAndDesk(AppPreferenceUtils.getPaperChoose(EditPaperActivity.this),
    					AppPreferenceUtils.getDeskChoose(EditPaperActivity.this));
    			mScratchPaper.setMax_undo(AppPreferenceUtils.getMaxUndo(EditPaperActivity.this));
    			mScratchPaper.clearStrokeList();
    			paper_name=TimeUtils.getDateByFileName(Calendar.getInstance().getTimeInMillis());
    		}
    		dismissLoadingDialog();
    		mDrawerLayout.closeDrawer(Gravity.LEFT);
    		showCustomToast(getResources().getString(R.string.save_success));
    	}
	};

	@Override
	public void setContentView(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
		loadingText = (TextView) view.findViewById(R.id.loading_text);
		builder.setView(view);
		mLoadingDialog = builder.create();
		setContentView(R.layout.activity_edit_paper);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.mipmap.icon_small);
		getMenuInflater().inflate(R.menu.edit_scratch_paper, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		AlertDialog.Builder builder;
		switch (item.getItemId()) {
			case R.id.clear_all_title:
				if (mDialog!=null) {
					mDialog.dismiss();
				}
				builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.clear_all_title))
						.setMessage(getResources().getString(R.string.affirm_clear_all));
				builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialog.dismiss();
					}
				});
				builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mScratchPaper.clearAll();
						mDialog.dismiss();
					}
				});
				mDialog = builder.create();
				mDialog.show();
				break;
			case R.id.full_screen:
				toggleFullScreen();
				break;
			case R.id.add_new_paper:
				changePaperContent(null);
				break;
			case R.id.undo:
				mScratchPaper.undoLastAction();
				break;
			case R.id.paint:
				mPaint=item;
				break;
			case R.id.black:
				mPaint.setIcon(R.mipmap.paint_setting_black);
				mScratchPaper.setColor(Color.BLACK);
				mScratchPaper.setStrokeWidth(5);
				mScratchPaper.setIsPointNotice(false);
				break;
			case R.id.red:
				mPaint.setIcon(R.mipmap.paint_setting_red);
				mScratchPaper.setColor(Color.RED);
				mScratchPaper.setStrokeWidth(5);
				mScratchPaper.setIsPointNotice(false);
				break;
			case R.id.blue:
				mPaint.setIcon(R.mipmap.paint_setting_blue);
				mScratchPaper.setColor(Color.BLUE);
				mScratchPaper.setStrokeWidth(5);
				mScratchPaper.setIsPointNotice(false);
				break;
			case R.id.eraser:
				mPaint.setIcon(R.mipmap.icon_eraser);
				mScratchPaper.setColor(Color.WHITE);
				mScratchPaper.setStrokeWidth(40);
				mScratchPaper.setIsPointNotice(true);
				break;
			case R.id.save:
				if (mDialog!=null) {
					mDialog.dismiss();
				}
				builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.save_file));
				View contentView = LayoutInflater.from(this).inflate(R.layout.include_dialog_savefile, null);
				builder.setView(contentView);
				EditText filename=(EditText) contentView.findViewById(R.id.filename);
				filename.setText(paper_name);
				builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialog.dismiss();
					}
				});
				builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDrawerLayout.closeDrawers();
						savePaper();
						mDialog.dismiss();
					}
				});
				mDialog=builder.create();
				mDialog.show();
				break;
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void toggleFullScreen() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				| View.SYSTEM_UI_FLAG_IMMERSIVE);
		} else {
			getWindow().setFlags(~WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mScratchPaper=(ScratchPaperView) findViewById(R.id.scratch_paper);
		mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
		
		mSavedPaperList=(ListView) findViewById(R.id.paper_list);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mSavedPaperList.setOnItemClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mScratchPaper.setPaperAndDesk(AppPreferenceUtils.getPaperChoose(this),
				AppPreferenceUtils.getDeskChoose(this));
		mScratchPaper.setMax_undo(AppPreferenceUtils.getMaxUndo(this));
		
		//read paper files
		mPaperList= PaperFileUtils.readPaperList();
		mAdapter=new PaperListAdapter(this,mPaperList);
		mSavedPaperList.setAdapter(mAdapter);
		
		//init paper name
		Bundle bundle=getIntent().getExtras();
		if (bundle!=null&&bundle.getString("paper_name")!=null) {
			paper_name=bundle.getString("paper_name");
			initPaperContent(paper_name);
		} else {
			paper_name= TimeUtils.getDateByFileName(Calendar.getInstance().getTimeInMillis());
		}
	}
	
	public void refreshViews() {
		mPaperList= PaperFileUtils.readPaperList();
		mAdapter=new PaperListAdapter(this,mPaperList);
		mSavedPaperList.setAdapter(mAdapter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mScratchPaper.startDraw();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Bitmap mStorePaperBackGround = Bitmap.createBitmap(mScratchPaper.getPaperWidth(),
				mScratchPaper.getPaperHeight(), Bitmap.Config.ARGB_8888);
		Canvas bitCanvas = new Canvas(mStorePaperBackGround);
		mScratchPaper.doDrawForSave(bitCanvas);
		outState.putString(KEY_STORE_BITMAP, "store_paper");
		MainApplication.store.put(outState.getString(KEY_STORE_BITMAP), mStorePaperBackGround);
		
		List<DrawStroke> mStrokeList=mScratchPaper.getStrokeList();
		outState.putString(KEY_STORE_STROKE, "store_stroke");
		MainApplication.store.put(outState.getString(KEY_STORE_STROKE), mStrokeList);
		
		mScratchPaper.stopDraw();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey(KEY_STORE_BITMAP)) {
			mScratchPaper.setPaperBackGround((Bitmap) MainApplication.store.get(
					savedInstanceState.get(KEY_STORE_BITMAP)));
			MainApplication.store.remove(savedInstanceState.get(KEY_STORE_BITMAP));
			savedInstanceState.remove(KEY_STORE_BITMAP);
		}
		if (savedInstanceState.containsKey(KEY_STORE_STROKE)) {
			mScratchPaper.setStrokeList((List<DrawStroke>) MainApplication.store.get(
					savedInstanceState.get(KEY_STORE_STROKE)));
			MainApplication.store.remove(savedInstanceState.get(KEY_STORE_STROKE));
			savedInstanceState.remove(KEY_STORE_STROKE);
		}
		mScratchPaper.startDraw();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		AdapterView<?> mListView=parent;
		changePaperContent((String) mListView.getAdapter().getItem(position));
	}

	/**
	 * 保存草稿纸
	 */
	protected void savePaper() {
		// TODO Auto-generated method stub
		final EditText filename=(EditText) mDialog.findViewById(R.id.filename);
		if (filename.getText()==null||
				filename.getText().toString().length()==0) {
			showCustomToast(R.string.name_no_null);
			return;
		}
		
		showLoadingDialogNotCancel(R.string.saving);
		
		//启动纸张保存进程
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (paper_name!=null&&paper_name.length()>0) {
					PaperFileUtils.deletePaper(paper_name);
				}
				
				paper_name=filename.getText().toString();
				
				Bitmap bitmap = Bitmap.createBitmap(mScratchPaper.getPaperWidth(),
						mScratchPaper.getPaperHeight(), Bitmap.Config.ARGB_8888);
				Canvas bitCanvas = new Canvas(bitmap);
				mScratchPaper.doDrawForScreenShot(bitCanvas);
				PaperFileUtils.savePaper(bitmap, paper_name);
				ChangePaperHandler.sendEmptyMessage(0);
			}
			
		}).start();
	}
	
	protected void changePaperContent(String paper_name_tmp) {
		final String new_paper_name=paper_name_tmp;
		if (mDialog!=null) {
			mDialog.dismiss();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.affirm_save_current_paper));
		View contentView = LayoutInflater.from(this).inflate(R.layout.include_change_paper, null);
		builder.setView(contentView);
		EditText editText=(EditText) contentView.findViewById(R.id.filename);
		editText.setText(paper_name);
		builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (new_paper_name != null) {
					paper_name = new String(new_paper_name);
				} else {
					paper_name = null;
				}
				ChangePaperHandler.sendEmptyMessage(0);
				mDialog.dismiss();
			}
		});
		builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final EditText filename = (EditText) mDialog.findViewById(R.id.filename);
				if (filename.getText() == null ||
						filename.getText().toString().length() == 0) {
					showCustomToast(R.string.name_no_null);
					return;
				}

				showLoadingDialogNotCancel(R.string.saving);

				//启动纸张保存进程
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (paper_name != null && paper_name.length() > 0) {
							PaperFileUtils.deletePaper(paper_name);
						}

						paper_name = filename.getText().toString();

						Bitmap bitmap = Bitmap.createBitmap(mScratchPaper.getPaperWidth(),
								mScratchPaper.getPaperHeight(), Bitmap.Config.ARGB_8888);
						Canvas bitCanvas = new Canvas(bitmap);
						mScratchPaper.doDrawForScreenShot(bitCanvas);
						PaperFileUtils.savePaper(bitmap, paper_name);

						if (new_paper_name != null) {
							paper_name = new String(new_paper_name);
						} else {
							paper_name = null;
						}
						ChangePaperHandler.sendEmptyMessage(0);
					}

				}).start();
				mDialog.dismiss();
			}
		});
		builder.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDialog.dismiss();
			}
		});
		mDialog=builder.create();
		mDialog.show();
	}
	
	/**
	 * 初始化纸张内容
	 * @param paper_name
	 */
	protected void initPaperContent(String paper_name) {
		if (FileUtils.isFileExist(PaperFileUtils.getPaperPath(paper_name))) {
			mScratchPaper.setPaperBackGround(PaperFileUtils.getPaper(paper_name));
			mScratchPaper.clearStrokeList();
			mScratchPaper.initPaperPosition();
		} else {
			PaperFileUtils.deletePaper(paper_name);
			finish();
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
}
