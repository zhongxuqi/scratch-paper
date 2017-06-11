/**   
 * @Title: ImageUtils.java 
 * @Package com.musketeer.lib.util 
 *
 * @author musketeer zhongxuqi@163.com  
 * @date 2014-11-10 下午1:22:46 
 * @version V1.0   
 */
package com.musketeer.scratchpaper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxuqi
 * 
 */
public class ImageUtils {

	/**
	 * 缩放bitmap
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		return resizeAndRotateImage(bitmap, w, h, 0);
	}

	public static Bitmap rotateImage(Bitmap bitmap, float rotateDegree) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateDegree);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * 旋转bitmap
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap resizeAndRotateImage(Bitmap bitmap, int w, int h,
			float rotateDegree) {
		if (bitmap == null || w <= 0 || h <= 0) {
			return null;
		}
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		matrix.postRotate(rotateDegree);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * 把图片转为灰度图
	 * 
	 * @param img
	 * @return
	 */
	public Bitmap convertGreyImg(Bitmap img) {
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		img.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}

	/**
	 * 图片切割
	 * 
	 * @param bitmap
	 * @param xPiece
	 * @param yPiece
	 * @return
	 */
	public static List<Bitmap> split(Bitmap bitmap, int xPiece, int yPiece) {
		List<Bitmap> pieces = new ArrayList<Bitmap>(xPiece * yPiece);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pieceWidth = width / 3;
		int pieceHeight = height / 3;
		for (int i = 0; i < yPiece; i++) {
			for (int j = 0; j < xPiece; j++) {
				Bitmap bitmappiece;
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				bitmappiece = Bitmap.createBitmap(bitmap, xValue, yValue,
						pieceWidth, pieceHeight);
				pieces.add(bitmappiece);
			}
		}

		return pieces;
	}

	/**
	 * 保存图片到指定文件
	 * 
	 * @param bitmap
	 * @param file
	 */
	public static void saveThePicture(Bitmap bitmap, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存图片到指定文件
	 * 
	 * @param bitmap
	 * @param file
	 */
	public static void saveImageToSD(Bitmap bitmap, String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.delete();
			} else if (!file.exists()) {
				file.mkdir();
				file.createNewFile();
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * dp转换为像素px
	 * <p>
	 * Title: Dp2Px
	 * <p>
	 * Description:
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 像素单位px转换为dp
	 * <p>
	 * Title: Px2Dp
	 * <p>
	 * Description:
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/**
	 * 图片文件解析
	 * 
	 * @param filePath
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap decodeFile(String filePath, int width, int height) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(Options paramOptions,
			int paramInt1, int paramInt2) {
		int i = paramOptions.outHeight;
		int j = paramOptions.outWidth;
		int k = 1;
		if ((i > paramInt2) || (j > paramInt1)) {
			int m = Math.round(i / paramInt2);
			int n = Math.round(j / paramInt1);
			k = m < n ? m : n;
		}
		return k;
	}

	/**
	 * 给位图添加阴影
	 * @param originalBitmap
	 * @return
	 */
	public static Bitmap drawImageDropShadow(Bitmap originalBitmap, int[] offsetXY) {
		BlurMaskFilter blurFilter = new BlurMaskFilter(12, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);
        shadowPaint.setShadowLayer(12, -3, -3, Color.parseColor("#33000000"));

        Bitmap shadowImage=originalBitmap.extractAlpha(shadowPaint, offsetXY);
        Bitmap shadowImage32=shadowImage.copy(Config.ARGB_8888, true);
        if (android.os.Build.VERSION.SDK_INT>=19&&!shadowImage32.isPremultiplied()) {
            shadowImage32.setPremultiplied(true);
        }
        Canvas c = new Canvas(shadowImage32); // exception occurs here <----
        c.drawBitmap(originalBitmap, -offsetXY[0], -offsetXY[1], null);

        return shadowImage32;
	}

}
