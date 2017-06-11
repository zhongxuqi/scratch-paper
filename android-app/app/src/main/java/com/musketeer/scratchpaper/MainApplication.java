/**   
* @Title: MainApplication.java 
* @Package com.musketeer.scratchpaper 
*
* @author musketeer zhongxuqi@163.com  
* @date 2014-11-12 下午12:50:27 
* @version V1.0   
*/
package com.musketeer.scratchpaper;

import android.os.Environment;

import com.musketeer.baselibrary.BaseApplication;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.scratchpaper.utils.FileUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhongxuqi
 *
 */
public class MainApplication extends BaseApplication {
	private static final String TAG = "MainApplication";
	public static final int DEFAULT_PAPER=R.mipmap.bg_paper;
	public static final int DEFAULT_PAPER_SMALL=R.mipmap.bg_paper_small;
	public static final int DEFAULT_DESK=R.mipmap.bg_desk_default;
	public static final int PAPER_MAX_UNDO=100;
	
	private static MainApplication instance;
	
	public static String mCachePath;
	public static String mCachePathComp;
	
    public static String errorLogPath;
    public static Map<String, Object> store=new HashMap<String, Object>();
    
    public static MainApplication getInstance() {
    	return instance;
    }

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance=this;

		// init umeng
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
		MobclickAgent. startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "56ecff3ce0f55ac331000a80", "XiaoMi"));
		
		iniEnv();
	}

	/**
	 * 初始化基本的变量
	 */
	private void iniEnv() {
		// TODO Auto-generated method stub
        String sdcard=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mCachePath=sdcard+"/cache/image/";
        mCachePathComp=sdcard+"/cache/image_comp/";
        FileUtils.createExternalStoragePublicPicture();
		FileUtils.createDir(new File(mCachePath));
		FileUtils.createDir(new File(mCachePathComp));
	}

}
