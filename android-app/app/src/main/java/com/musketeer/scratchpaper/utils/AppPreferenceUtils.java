/**   
* @Title: AppPreferenceUtils.java 
* @Package com.musketeer.scratchpaper.utils 
*
* @author musketeer zhongxuqi@163.com  
* @date 2014-11-16 下午6:45:46 
* @version V1.0   
*/
package com.musketeer.scratchpaper.utils;

import android.content.Context;

import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.scratchpaper.MainApplication;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.common.SharePreferenceConfig;

/**
 * @author zhongxuqi
 *
 */
public class AppPreferenceUtils {
	
	/**
	 * 获取最大返回操作数
	 * @param context
	 * @return
	 */
	public static int getMaxUndo(Context context) {
		return SharePreferenceUtils.getInt(context, SharePreferenceConfig.MAX_UNDO,
				MainApplication.PAPER_MAX_UNDO);
	}
	
	/**
	 * 获取纸张材质
	 * @param context
	 * @return
	 */
	public static int getPaperChoose(Context context) {
		switch (SharePreferenceUtils.getInt(context, SharePreferenceConfig.PAPER_SIZE, 0)) {
		case 0:
			return R.mipmap.bg_paper;
		case 1:
			return R.mipmap.paper_medium;
		case 2:
			return R.mipmap.paper_big;
		}
		return MainApplication.DEFAULT_PAPER;
		
//		return SharePreferenceUtils.getInt(context, SharePreferenceConfig.PAPER, 
//				MainApplication.DEFAULT_PAPER);
	}
	
	/**
	 * 获取纸张材质
	 * @param context
	 * @return
	 */
	public static int getPaperSmallChoose(Context context) {
		return SharePreferenceUtils.getInt(context, SharePreferenceConfig.PAPER_SMALL, 
				MainApplication.DEFAULT_PAPER_SMALL);
	}
	
	/**
	 * 获取桌面材质
	 * @param context
	 * @return
	 */
	public static int getDeskChoose(Context context) {
		return MainApplication.DEFAULT_DESK;
		
//		return SharePreferenceUtils.getInt(context, SharePreferenceConfig.DESK, 
//				MainApplication.DEFAULT_DESK);
	}
	
	public static int getRowNum(Context context) {
		return SharePreferenceUtils.getInt(context, SharePreferenceConfig.ROW_NUM, 3);
	}

}
