/**
 * @Title: TimeUtils.java
 * *
 * @Package com.musketeer.lib.util
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-10 下午7:46:30
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author zhongxuqi
 */
object TimeUtils {

    /**
     * 格式为yyyy年MM月dd日  HH:mm:ss
     *
     * Title: getDateCN
     *
     * Description:
     * @return
     */
    val dateCN: String
        get() {
            val format = SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss")
            val date = format.format(Date(System.currentTimeMillis()))
            return date
        }

    /**
     * 格式为yyyy年MM月dd日  HH:mm
     *
     * Title: getDateCN
     *
     * Description:
     * @return
     */
    val dateCNNotSecond: String
        get() {
            val format = SimpleDateFormat("yyyy年MM月dd日  HH:mm")
            val date = format.format(Date(System.currentTimeMillis()))
            return date
        }

    /**
     * 格式为yyyy-MM-dd
     *
     * Title: getDateEN
     *
     * Description:
     * @return
     */
    val dateNotMinByCurrentTime: String
        get() {
            val format1 = SimpleDateFormat("yyyy-MM-dd")
            val date1 = format1.format(Date(System.currentTimeMillis()))
            return date1
        }

    /**
     * 格式为yyyy-MM-dd
     *
     * Title: getDateEN
     *
     * Description:
     * @return
     */
    fun getDateNotMin(timeMills: Long): String {
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        val date1 = format1.format(Date(timeMills))
        return date1
    }

    /**
     * 格式为MM-dd
     *
     * Title: getDateEN
     *
     * Description:
     * @return
     */
    fun getDateNotYear(timeMills: Long): String {
        val format1 = SimpleDateFormat("MM-dd")
        val date1 = format1.format(Date(timeMills))
        return date1
    }

    /**
     * 格式为yyyy-MM-dd HH:mm:ss
     *
     * Title: getDateEN
     *
     * Description:
     * @return
     */
    val dateEN: String
        get() {
            val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date1 = format1.format(Date(System.currentTimeMillis()))
            return date1
        }

    /**
     * 格式为yyyy-MM-dd HH:mm
     *
     * Title: getDateEN
     *
     * Description:
     * @return
     */
    val dateENNotSecond: String
        get() {
            val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val date1 = format1.format(Date(System.currentTimeMillis()))
            return date1
        }

    /**
     * 格式为yyyy-MM-dd HH:mm
     *
     * Title: getDateEN
     *
     * Description:
     * @return
     */
    fun getDateENNotSecond(timeMills: Long): String {
        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date1 = format1.format(Date(timeMills))
        return date1
    }

    /**
     * 格式为yyyy-MM-dd HH:mm:ss
     *
     * Title: getDateEN
     *
     * Description:
     * @param timeMills
     * *
     * @return
     */
    fun getDateEN(timeMills: Long): String {
        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date1 = format1.format(Date(timeMills))
        return date1
    }

    /**
     * 格式为HH:mm
     *
     * Title: getDate
     *
     * Description:
     * @return
     */
    val dateByHourAndMin: String
        get() {
            val format = SimpleDateFormat("HH:mm")
            val date = format.format(Date(System.currentTimeMillis()))
            return date
        }

    /**
     * 格式为yyyy_MM_dd_HH_mm_ss
     *
     * Title: getDate
     *
     * Description:
     * @return
     */
    fun getDateByFileName(timeMills: Long): String {
        val format = SimpleDateFormat("yyyy_MM_dd HH_mm_ss")
        val date = format.format(Date(timeMills))
        return date
    }

}
