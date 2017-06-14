/**
 * @Title: SharePreferenceUtils.java
 * *
 * @Package com.musketeer.lib.util
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-12 下午1:51:30
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * @author zhongxuqi
 */
object SharePreferenceUtils {
    val PREFERENCE_NAME = "Musketeer"

    fun getBoolean(context: Context, key: String,
                   defaultvalue: Boolean): Boolean {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean(key, defaultvalue)
    }

    fun putBoolean(context: Context, key: String, value: Boolean): Boolean {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = mSharedPreferences.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    fun getString(context: Context, key: String,
                  defaultvalue: String): String {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        return mSharedPreferences.getString(key, defaultvalue)
    }

    fun putString(context: Context, key: String, value: String): Boolean {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = mSharedPreferences.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun getInt(context: Context, key: String, defaultvalue: Int): Int {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        return mSharedPreferences.getInt(key, defaultvalue)
    }

    fun putInt(context: Context, key: String, value: Int): Boolean {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = mSharedPreferences.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun getLong(context: Context, key: String, defaultvalue: Long): Long {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        return mSharedPreferences.getLong(key, defaultvalue)
    }

    fun putLong(context: Context, key: String, value: Long): Boolean {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = mSharedPreferences.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun getFloat(context: Context, key: String, defaultvalue: Float): Float {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        return mSharedPreferences.getFloat(key, defaultvalue)
    }

    fun putFloat(context: Context, key: String, value: Float): Boolean {
        val mSharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = mSharedPreferences.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

}
