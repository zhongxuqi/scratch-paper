/**
 * @Title: ImageUtils.java
 * *
 * @Package com.musketeer.lib.util
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-10 下午1:22:46
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

/**
 * @author zhongxuqi
 */
class ImageUtils {

    /**
     * 把图片转为灰度图

     * @param img
     * *
     * @return
     */
    fun convertGreyImg(img: Bitmap): Bitmap {
        val width = img.width // 获取位图的宽
        val height = img.height // 获取位图的高

        val pixels = IntArray(width * height) // 通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height)
        val alpha = 0xFF shl 24
        for (i in 0..height - 1) {
            for (j in 0..width - 1) {
                var grey = pixels[width * i + j]

                val red = grey and 0x00FF0000 shr 16
                val green = grey and 0x0000FF00 shr 8
                val blue = grey and 0x000000FF

                grey = (red.toFloat() * 0.3 + green.toFloat() * 0.59 + blue.toFloat() * 0.11).toInt()
                grey = alpha or (grey shl 16) or (grey shl 8) or grey
                pixels[width * i + j] = grey
            }
        }
        val result = Bitmap.createBitmap(width, height, Config.RGB_565)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    companion object {

        /**
         * 缩放bitmap

         * @param bitmap
         * *
         * @param w
         * *
         * @param h
         * *
         * @return
         */
        fun resizeImage(bitmap: Bitmap, w: Int, h: Int): Bitmap {
            return resizeAndRotateImage(bitmap, w, h, 0f)
        }

        fun rotateImage(bitmap: Bitmap, rotateDegree: Float): Bitmap {
            val BitmapOrg = bitmap
            val width = BitmapOrg.width
            val height = BitmapOrg.height
            val matrix = Matrix()
            matrix.postRotate(rotateDegree)
            val resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                    height, matrix, true)
            return resizedBitmap
        }

        /**
         * 旋转bitmap

         * @param bitmap
         * *
         * @param w
         * *
         * @param h
         * *
         * @return
         */
        fun resizeAndRotateImage(bitmap: Bitmap, w: Int, h: Int,
                                 rotateDegree: Float): Bitmap {
            val BitmapOrg = bitmap
            val width = BitmapOrg.width
            val height = BitmapOrg.height
            val newWidth = w
            val newHeight = h
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            matrix.postRotate(rotateDegree)
            val resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                    height, matrix, true)
            return resizedBitmap
        }

        /**
         * 图片切割

         * @param bitmap
         * *
         * @param xPiece
         * *
         * @param yPiece
         * *
         * @return
         */
        fun split(bitmap: Bitmap, xPiece: Int, yPiece: Int): List<Bitmap> {
            val pieces = ArrayList<Bitmap>(xPiece * yPiece)
            val width = bitmap.width
            val height = bitmap.height
            val pieceWidth = width / 3
            val pieceHeight = height / 3
            for (i in 0..yPiece - 1) {
                for (j in 0..xPiece - 1) {
                    val bitmappiece: Bitmap
                    val xValue = j * pieceWidth
                    val yValue = i * pieceHeight
                    bitmappiece = Bitmap.createBitmap(bitmap, xValue, yValue,
                            pieceWidth, pieceHeight)
                    pieces.add(bitmappiece)
                }
            }

            return pieces
        }

        /**
         * 保存图片到指定文件

         * @param bitmap
         * *
         * @param file
         */
        fun saveThePicture(bitmap: Bitmap, file: File) {
            try {
                val fos = FileOutputStream(file)
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                    fos.flush()
                    fos.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        /**
         * 保存图片到指定文件

         * @param bitmap
         * *
         * @param file
         */
        fun saveImageToSD(bitmap: Bitmap, fileName: String) {
            try {
                val file = File(fileName)
                if (!file.exists()) {
                    file.delete()
                } else if (!file.exists()) {
                    file.mkdir()
                    file.createNewFile()
                }
                val bos = BufferedOutputStream(
                        FileOutputStream(file))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                bos.flush()
                bos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        /**
         * dp转换为像素px
         *
         *
         * Title: Dp2Px
         *
         *
         * Description:

         * @param context
         * *
         * @param dp
         * *
         * @return
         */
        fun Dp2Px(context: Context, dp: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }

        /**
         * 像素单位px转换为dp
         *
         *
         * Title: Px2Dp
         *
         *
         * Description:

         * @param context
         * *
         * @param px
         * *
         * @return
         */
        fun Px2Dp(context: Context, px: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (px / scale + 0.5f).toInt()
        }

        /**
         * 图片文件解析

         * @param filePath
         * *
         * @param width
         * *
         * @param height
         * *
         * @return
         */
        fun decodeFile(filePath: String, width: Int, height: Int): Bitmap {
            val options = Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            options.inSampleSize = calculateInSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(filePath, options)
        }

        fun calculateInSampleSize(paramOptions: Options,
                                  paramInt1: Int, paramInt2: Int): Int {
            val i = paramOptions.outHeight
            val j = paramOptions.outWidth
            var k = 1
            if (i > paramInt2 || j > paramInt1) {
                val m = Math.round((i / paramInt2).toFloat())
                val n = Math.round((j / paramInt1).toFloat())
                k = if (m < n) m else n
            }
            return k
        }

        /**
         * 给位图添加阴影
         * @param originalBitmap
         * *
         * @return
         */
        fun drawImageDropShadow(originalBitmap: Bitmap, offsetXY: IntArray): Bitmap {
            val blurFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.OUTER)
            val shadowPaint = Paint()
            shadowPaint.maskFilter = blurFilter
            shadowPaint.setShadowLayer(12f, -3f, -3f, Color.parseColor("#33000000"))

            val shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY)
            val shadowImage32 = shadowImage.copy(Config.ARGB_8888, true)
            if (android.os.Build.VERSION.SDK_INT >= 19 && !shadowImage32.isPremultiplied) {
                shadowImage32.isPremultiplied = true
            }
            val c = Canvas(shadowImage32) // exception occurs here <----
            c.drawBitmap(originalBitmap, (-offsetXY[0]).toFloat(), (-offsetXY[1]).toFloat(), null)

            return shadowImage32
        }
    }

}
