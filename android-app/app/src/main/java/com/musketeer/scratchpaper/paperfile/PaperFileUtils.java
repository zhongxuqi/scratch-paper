/**   
* @Title: PaperFileUtils.java 
* @Package com.musketeer.scratchpaper.paperfile 
*
* @author musketeer zhongxuqi@163.com  
* @date 2014-12-7 下午7:08:03 
* @version V1.0   
*/
package com.musketeer.scratchpaper.paperfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.musketeer.scratchpaper.MainApplication;
import com.musketeer.scratchpaper.utils.FileUtils;
import com.musketeer.scratchpaper.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxuqi
 *
 */
public class PaperFileUtils {
	/**
	 * 从草稿纸路径获取草稿纸名
	 * @param paper_path
	 * @return
	 */
	public static String getPaperName(String paper_path) {
		return paper_path.substring(paper_path.lastIndexOf("/")+1,
				paper_path.lastIndexOf(".png"));
	}
	
	/**
	 * 从草稿纸名获取缩略草稿纸路径
	 * @param paper_name
	 * @return
	 */
	public static String getPaperThumbNailPath(String paper_name) {
		if (paper_name.contains(".png")) {
			return MainApplication.getInstance().mCachePathComp+paper_name;
		} else {
			return MainApplication.getInstance().mCachePathComp+paper_name+".png";
		}
	}
	
	/**
	 * 从草稿纸名获取草稿纸路径
	 * @param paper_name
	 * @return
	 */
	public static String getPaperPath(String paper_name) {
		if (paper_name.indexOf(".png")>=0) {
			return MainApplication.getInstance().mCachePath+paper_name;
		} else {
			return MainApplication.getInstance().mCachePath+paper_name+".png";
		}
	}
	
	/**
	 * 从草稿纸名获取草稿纸位图
	 * @param paper_name
	 * @return
	 */
	public static Bitmap getPaper(String paper_name) {
		return BitmapFactory.decodeFile(getPaperPath(paper_name))
				.copy(Bitmap.Config.ARGB_8888, true);
	}
	
	/**
	 * 从草稿纸名获取草稿纸缩略图
	 * @param paper_name
	 * @return
	 */
	public static Bitmap getPaperThumbNail(String paper_name) {
		if (FileUtils.isFileExist(PaperFileUtils.getPaperThumbNailPath(paper_name))) {
			return BitmapFactory.decodeFile(getPaperThumbNailPath(paper_name))
					.copy(Bitmap.Config.ARGB_8888, true);
		} else {
			return null;
		}
	}
	
	/**
	 * 保存草稿纸到SD卡中
	 * @param bitmap
	 * @param paper_name
	 */
	public static void savePaper(Bitmap bitmap,String paper_name) {
		ImageUtils.saveImageToSD(bitmap,
				MainApplication.getInstance().mCachePath +
						paper_name + ".png");
		ImageUtils.saveImageToSD(ImageUtils.resizeImage(bitmap, 200, 400), 
				MainApplication.getInstance().mCachePathComp+
						paper_name+".png");
	}
	
	/**
	 * 从SD卡中删除草稿纸
	 * @param paper_name
	 */
	public static void deletePaper(String paper_name) {
		if (paper_name==null||paper_name.length()==0) {
			return;
		}
		FileUtils.deleteFile(getPaperPath(paper_name));
		FileUtils.deleteFile(getPaperThumbNailPath(paper_name));
	}
	
	/**
	 * 从SD卡中读取文件名列表
	 * @return
	 */
	public static List<String> readPaperList() {
		List<String> mPaperList=new ArrayList<String>();
		File f = new File(MainApplication.getInstance().mCachePathComp);
		File[] files = f.listFiles();
		if (files != null) {
			for (File inFile : files) {
				if (!inFile.isDirectory()) {
					mPaperList.add(PaperFileUtils.getPaperName(inFile.getPath()));
				}
			}
		}
		return mPaperList;
	}

}
