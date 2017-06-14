package com.musketeer.scratchpaper.utils

/*
 * 用法介绍：
 * 设置日志打印级别
 * LogUtil.LEVEL = LogUtil.W;  (默认为LogUtil.I)
 * 设置应用打印日志路径名称
 * LogUtil.setDefaultFilePath(context);
 * 设置应用自定义打印日志路径名称
 * LogUtil.setLogFilePath("com/demo/log");
 * 设置同步打印记录到日志
 * LogUtil.setSyns(true);
 * 设置启用一个新的LOG
 * LogUtil.startNewLog();
 * 打印单行信息
 * LogUtil.i(msg)   LogUtil.v(msg)   LogUtil.w(msg)  LogUtil.d(msg)  LogUtil.e(msg);
 * 打印异常信息
 * LogUtil.i(exp) .... ;
 * 强制打印信息
 * LogUtil.print(msg);  LogUtil.print(exp);
==========================================================================================
 * 一般应用发布前要除去项目中的打印信息，因此只需要设置LogUtil.LEVEL = 10 (比LogUtil.E大即可);
 * 对于强制打印的信息，将无法去除。
 *
 * 欢迎大家补充，以及指点错误之处。
 */

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import com.musketeer.scratchpaper.contant.BuildConfig

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * 打印工具类(Debug模式才会开启打印输出，文件保存日志不影响)
 * @author meimuan
 * * 功能点：
 *
 *
 * *
 * *  * Debug模式才会开启打印输出
 * * 	 * 可以控制打印输出
 * * 	 * 可以简便打印，经过包装得到精准特定格式的日志信息
 * * 	 * 可以直接打印异常
 * * 	 * 可以定位异常信息
 * * 	 * 可以便捷统一修改，优化
 * *   * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * *   * 同步输出日志到本地sdcard文件
 * *
 * *
 *
 *[LogUtil openLog() 打开日志输出（打印输出和文件输出总开关）][.]
 * *
 *
 *[LogUtil closeLog() 关闭日志输出（打印输出和文件输出）][.]
 * *
 *
 *[LogUtil setSyns(boolean) 文件输出开关][.]
 * *
 *
 *
 * * 需要注册权限：
 * * <table>
 * *  <tr>
 * * 		<td>* 读写SD卡权限    </td><td>android.permission.WRITE_EXTERNAL_STORAGE</td>
 * *  </tr>
 * * 	<tr>
 * * 		<td>* 网络权限    </td><td>android.permission.INTERNET</td>
 * *  </tr>
 * * </table>
 * *
 * *
 *
 *
 * * 说明：<br></br>
 * * 1. 日志是针对天数级别的，自动生成的日志名称yyyyMMdd.log形式。
 * * 2. 如果当天的日志需要新开一个（比如：日志很大了，需要从新生成一个）
 * *
 */
object LogUtils {
    /** 控制打印级别 在level级别之上才可以被打印出来  */
    var LEVEL = 1
    /** 打印级别为V，对应Log.v */
    val V = 1
    /** 打印级别为W，对应Log.w */
    val W = 2
    /** 打印级别为I，对应Log.i */
    val I = 3
    /** 打印级别为D，对应Log.d */
    val D = 4
    /** 打印级别为E，对应Log.e */
    val E = 5
    /** 最高级别打印，强制性打印，LEVEL无法关闭。  */
    private val P = Integer.MAX_VALUE
    /** 打印修饰符号 */
    private val _L = "["
    private val _R = "]"
    /** 是否同步输出到本地日志文件  */
    private var IS_SYNS = false
    /** 打印日志保存路径 */
    private var LOG_FILE_DIR = ""
    /** 生成一个日期文件名格式的日式格式对象 */
    private val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    /** 是否新建一个日志文件。 */
    private var IF_START_NEWLOG = true
    /** 保存创建的文件路径  */
    private var CURRENT_LOG_NAME = ""
    /** 针对天数级别的。如果当天已经存在一个LOG了，而使用者需要新开一个LOG，那么将计数  */
    private var FILE_LOG_COUNT = 0
    /** 单个日志的最大的容量,如果一个日志太大了，打开会影响效率 */
    private val LOG_MAX_SIZE = 6 * 1024 * 1024
    /** 检测文件目的地址是否正确  */
    private val pattern = Pattern.compile("(\\w+/)+")
    /** Debug模式，在Debug模式下才打印日志 */
    private val DEBUG_MODE = BuildConfig.DEBUG


    /**
     * 动态关闭日志, 默认打开(关闭打印输出和文件输出)
     *
     * Title: closeLog0
     *
     * Description:
     */
    fun closeLog() {
        LogUtils.LEVEL = 10
    }

    /**
     * 动态打开日志
     *
     * Title: openLog
     *
     * Description:
     */
    fun openLog() {
        LogUtils.LEVEL = 1
    }

    /** 设置是否同步记录信息或者异常到日志文件。 */
    fun setSyns(flag: Boolean) {
        synchronized(LogUtils::class.java) {
            IS_SYNS = flag
        }
    }

    /** 开启一个新的LOG  */
    fun startNewLog() {
        IF_START_NEWLOG = true
    }

    /**
     * 打印信息
     * @param message
     */
    fun i(message: String) {
        if (LEVEL <= I) {
            if (DEBUG_MODE) {
                Log.i(getTag(message), message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun i(exp: Exception) {
        if (LEVEL <= I) {
            if (DEBUG_MODE) {
                Log.i(getTag(exp), getMessage(exp))
            }
            if (IS_SYNS) {
                LogFile.writeLog(exp)
            }
        }
    }

    /**
     * 打印信息
     * @param message
     */
    fun i(tag: String, message: String) {
        if (LEVEL <= I) {
            if (DEBUG_MODE) {
                Log.i(tag, message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun e(message: String) {
        if (LEVEL <= E) {
            if (DEBUG_MODE) {
                Log.e(getTag(message), message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun e(exp: Exception) {
        if (LEVEL <= E) {
            if (DEBUG_MODE) {
                Log.e(getTag(exp), getMessage(exp))
            }
            if (IS_SYNS) {
                LogFile.writeLog(exp)
            }
        }
    }

    fun e(tag: String, message: String) {
        if (LEVEL <= E) {
            if (DEBUG_MODE) {
                Log.e(tag, message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun w(message: String) {
        if (LEVEL <= W) {
            if (DEBUG_MODE) {
                Log.w(getTag(message), message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun w(exp: Exception) {
        if (LEVEL <= W) {
            if (DEBUG_MODE) {
                Log.w(getTag(exp), getMessage(exp))
            }
            if (IS_SYNS) {
                LogFile.writeLog(exp)
            }
        }
    }

    fun w(tag: String, message: String) {
        if (LEVEL <= W) {
            if (DEBUG_MODE) {
                Log.w(tag, message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun v(message: String) {
        if (LEVEL <= V) {
            if (DEBUG_MODE) {
                Log.v(getTag(message), message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun v(exp: Exception) {
        if (LEVEL <= V) {
            if (DEBUG_MODE) {
                Log.v(getTag(exp), getMessage(exp))
            }
            if (IS_SYNS) {
                LogFile.writeLog(exp)
            }
        }
    }

    fun v(tag: String, message: String) {
        if (LEVEL <= V) {
            if (DEBUG_MODE) {
                Log.v(tag, message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun d(message: String) {
        if (LEVEL <= D) {
            if (DEBUG_MODE) {
                Log.d(getTag(message), message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    fun d(exp: Exception) {
        if (LEVEL <= D) {
            if (DEBUG_MODE) {
                Log.d(getTag(exp), getMessage(exp))
            }
            if (IS_SYNS) {
                LogFile.writeLog(exp)
            }
        }
    }

    fun d(tag: String, message: String) {
        if (LEVEL <= D) {
            if (DEBUG_MODE) {
                Log.d(tag, message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }


    /**
     * 强制打印信息
     * @param message
     */
    fun print(message: String) {
        if (LEVEL <= P) {
            if (DEBUG_MODE) {
                Log.e(getTag(message), message)
            }
            if (IS_SYNS) {
                LogFile.writeLog(message)
            }
        }
    }

    /**
     * 强制打印异常
     * @param exp
     */
    fun print(exp: Exception) {
        if (LEVEL <= P) {
            if (DEBUG_MODE) {
                Log.e(getTag(exp), getMessage(exp))
            }
            if (IS_SYNS) {
                LogFile.writeLog(exp)
            }
        }
    }

    /** 获取一个Tag打印标签
     * @param msg
     * *
     * @return
     * *
     * @since JDK 1.5
     */
    private fun getTag(msg: String?): String {
        if (msg != null) {
            //since jdk 1.5
            if (Thread.currentThread().stackTrace.size > 0) {
                val name = Thread.currentThread().stackTrace[0].className
                return _L + name.substring(name.lastIndexOf(".") + 1) + _R
            }
        }
        return _L + "null" + _R
    }

    /**
     * 跟据变量获取一个打印的标签。
     * @param exp
     * *
     * @return
     */
    private fun getTag(exp: Exception?): String {
        if (exp != null) {
            if (exp.stackTrace.size > 0) {
                val name = exp.stackTrace[0].className
                return _L + name.substring(name.lastIndexOf(".") + 1) + _R
            }
            return _L + "exception" + _R
        }
        return _L + "null" + _R
    }

    /**
     * 获取Exception的简便异常信息
     * @param exp
     * *
     * @return
     */
    private fun getMessage(exp: Exception): String {
        val sb = StringBuilder()
        val element = exp.stackTrace
        var n = 0
        sb.append("\n")
        sb.append(exp.toString())
        sb.append("\n")
        for (e in element) {
            sb.append(e.className)
            sb.append(".")
            sb.append(e.methodName)
            sb.append("[")
            sb.append(e.lineNumber)
            sb.append("]")
            sb.append("\n")
            n++
            if (n >= 2) break
        }
        if (exp.cause != null) {
            sb.append("Caused by: ")
            sb.append(exp.message)
        }
        return sb.toString()
    }

    /** 自定义保存文件路径，如果是多重路径，请以xxx/xxx/xxx 形式的‘文件夹’
     * @parma  path : 文件夹名称
     * *  * [将自动以当前时间为文件名拼接成完整路径。请慎用]
     * *
     */
    fun setLogFilePath(path: String) {
        val url = SDcardUtil.path + File.separator + path
        val flag = pattern.matcher(url).matches()
        if (flag) {
            LOG_FILE_DIR = url
        } else {
            LogFile.writeLog("the url is not match file`s dir")
        }
    }

    /** 设置默认路径，以包名为格式的文件夹 */
    fun setDefaultFilePath(context: Context) {
        val pkName = context.packageName.replace("\\.".toRegex(), "\\/")
        setLogFilePath(pkName)
    }

    /** 获取时间字符串  */
    private val currTimeDir: String
        get() = sdf.format(Date())

    /** LOG定制类。
     * 输出LOG到日志。
     */
    private object LogFile {
        /** 内部强制性打印使用。区分print ， 是为了解决无限循环打印exception */
        private fun print(msg: String) {
            if (LEVEL <= P) {
                Log.e(getTag(msg), msg)
            }
        }

        /**
         * 打印信息
         * @param message
         */
        @Synchronized fun writeLog(message: String) {
            val f = file
            if (f != null) {
                try {
                    val fw = FileWriter(f, true)
                    val bw = BufferedWriter(fw)
                    bw.append("\n")
                    bw.append(message)
                    bw.append("\n")
                    bw.flush()
                    bw.close()
                    fw.close()
                } catch (e: IOException) {
                    print("writeLog error, " + e.message)
                }

            } else {
                print("writeLog error, due to the file dir is error")
            }
        }

        /**
         * 打印异常
         * @param exp
         */
        @Synchronized fun writeLog(exp: Exception) {
            val f = file
            if (f != null) {
                try {
                    val fw = FileWriter(f, true)
                    val pw = PrintWriter(fw)
                    pw.append("\n")
                    exp.printStackTrace(pw)
                    pw.flush()
                    pw.close()
                    fw.close()
                } catch (e: IOException) {
                    print("writeLog error, " + e.message)
                }

            } else {
                print("writeLog error, due to the file dir is error")
            }
        }

        /**
         * 获取文件
         * @return
         */
        private //已经存在了
        val file: File?
            get() {
                if ("" == LOG_FILE_DIR) {
                    return null
                }
                synchronized(LogUtils::class.java) {
                    if (!IF_START_NEWLOG) {
                        val currFile = File(CURRENT_LOG_NAME)
                        if (currFile.length() >= LOG_MAX_SIZE) {
                            IF_START_NEWLOG = true
                            return file
                        }
                        return currFile
                    }
                    val f = File(LOG_FILE_DIR)
                    if (!f.exists()) {
                        f.mkdirs()
                    }
                    val file = File(f.absolutePath + File.separator + currTimeDir + ".log")
                    if (!file.exists()) {
                        try {
                            file.createNewFile()
                            FILE_LOG_COUNT = 0
                            IF_START_NEWLOG = false
                            CURRENT_LOG_NAME = file.absolutePath
                        } catch (e: IOException) {
                            print("createFile error , " + e.message)
                        }

                    } else {
                        if (IF_START_NEWLOG) {
                            FILE_LOG_COUNT++
                            return File(f.absolutePath + File.separator + currTimeDir + "_" + FILE_LOG_COUNT + ".log")
                        }
                    }
                    return file
                }
            }
    }

    /**
     * SD卡管理器
     */
    private object SDcardUtil {

        //		public static String getAbsPath() {
        //			if (isMounted()) {
        //				return Environment.getExternalStorageDirectory().getAbsolutePath();
        //			}
        //			return "";
        //		}
        /** 获取Path */
        val path: String
            get() {
                if (isMounted) {
                    return Environment.getExternalStorageDirectory().path
                }
                LogFile.writeLog("please check if sd card is not mounted")
                return ""
            }
        /** 判断SD卡是否mounted */
        val isMounted: Boolean
            @SuppressLint("NewApi")
            get() = Environment.isExternalStorageEmulated()

    }
}

