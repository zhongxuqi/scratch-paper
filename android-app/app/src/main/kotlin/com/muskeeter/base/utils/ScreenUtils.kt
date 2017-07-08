/**
 * @Title: ScreenUtils.java
 * *
 * @Package com.musketeer.lib.util
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-16 上午11:03:34
 * *
 * @version V1.0
 */
package com.muskeeter.base.utils

import android.content.Context

/**
 * @author zhongxuqi
 */
object ScreenUtils {
    fun dpToPx(context: Context?, dp: Float): Float {
        if (context == null) {
            return -1f
        }
        return dp * context.resources.displayMetrics.density
    }

    fun pxToDp(context: Context?, px: Float): Float {
        if (context == null) {
            return -1f
        }
        return px / context.resources.displayMetrics.density
    }

    fun dpToPxInt(context: Context, dp: Float): Float {
        return (dpToPx(context, dp) + 0.5f).toInt().toFloat()
    }

    fun pxToDpCeilInt(context: Context, px: Float): Float {
        return (pxToDp(context, px) + 0.5f).toInt().toFloat()
    }

}
