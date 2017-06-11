package com.musketeer.scratchpaper.utils

import android.os.Environment
import android.text.TextUtils
import android.util.Log

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.LineNumberReader
import java.io.OutputStream
import java.io.Reader
import java.util.ArrayList
import java.util.regex.Pattern

/**

 * @author qh
 */
object FileUtils {

    private val TAG = "FileUtil"

    /**
     * Gets the Android external storage directory
     */
    val sdCardPath: File
        get() = Environment.getExternalStorageDirectory()

    /**
     * create public picture file if not exists
     * @return if the file exists return true, otherwise, return false and create folder
     */
    fun createExternalStoragePublicPicture(): Boolean {
        if (!isSDCardAvailable) {
            return false
        }
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return path.mkdirs()
    }

    val externalStoragePublicDownload: String
        get() {
            if (!isSDCardAvailable) {
                return ""
            }
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!path.exists()) {
                path.mkdirs()
            }
            return path.absolutePath
        }

    /**
     * 创建文件
     * @param file
     * *
     * @return
     */
    fun createNewFile(file: File): File? {

        try {

            if (file.exists()) {
                return file
            }

            val dir = file.parentFile
            if (!dir.exists()) {
                dir.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
        } catch (e: IOException) {
            Log.e(TAG, "", e)
            return null
        }

        return file
    }

    /**
     * 创建文件
     * @param path
     */
    fun createNewFile(path: String): File? {
        val file = File(path)
        return createNewFile(file)
    }// end method createText()

    /**
     * 删除文件

     * @param path
     */
    fun deleteFile(path: String) {
        val file = File(path)
        deleteFile(file)
    }

    /**
     * 删除文件
     * @param file
     */
    fun deleteFile(file: File) {
        if (!file.exists()) {
            return
        }
        if (file.isFile) {
            file.delete()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            for (i in files.indices) {
                deleteFile(files[i])
            }
        }
        file.delete()
    }

    @JvmOverloads fun write(path: String, content: String, append: Boolean = false): Boolean {
        return write(File(path), content, append)
    }

    @JvmOverloads fun write(file: File?, content: String, append: Boolean = false): Boolean {
        var file = file
        if (file == null || TextUtils.isEmpty(content)) {
            return false
        }
        if (!file.exists()) {
            file = createNewFile(file)
        }
        var ops: FileOutputStream? = null
        try {
            ops = FileOutputStream(file!!, append)
            ops.write(content.toByteArray())
        } catch (e: Exception) {
            Log.e(TAG, "", e)
            return false
        } finally {
            try {
                ops!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "", e)
            }

            ops = null
        }

        return true
    }

    /**
     * 获得文件名

     * @param path
     * *
     * @return
     */
    fun getFileName(path: String): String? {
        if (TextUtils.isEmpty(path)) {
            return null
        }
        var f: File? = File(path)
        val name = f!!.name
        f = null
        return name
    }

    /**
     * 读取文件内容，从第startLine行开始，读取lineCount行

     * @param file
     * *
     * @param startLine
     * *
     * @param lineCount
     * *
     * @return 读到文字的list,如果list.size<lineCount则说明读到文件末尾了></lineCount则说明读到文件末尾了>

     */
    fun readFile(file: File?, startLine: Int, lineCount: Int): List<String>? {
        if (file == null || startLine < 1 || lineCount < 1) {
            return null
        }
        if (!file.exists()) {
            return null
        }
        var fileReader: FileReader? = null
        var list: MutableList<String>? = null
        try {
            list = ArrayList<String>()
            fileReader = FileReader(file)
            val lnr = LineNumberReader(fileReader)
            var end = false
            for (i in 1..startLine - 1) {
                if (lnr.readLine() == null) {
                    end = true
                    break
                }
            }
            if (end == false) {
                for (i in startLine..startLine + lineCount - 1) {
                    val line = lnr.readLine() ?: break
                    list.add(line)

                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "read log error!", e)
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return list
    }

    /**
     * 创建文件夹
     * @param dir
     * *
     * @return
     */
    fun createDir(dir: File): Boolean {
        try {
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return true
        } catch (e: Exception) {
            Log.e(TAG, "create dir error", e)
            return false
        }

    }


    /**
     * 判断SD卡上的文件是否存在
     *
     * Title: isFileExist
     *
     * Description:
     * @param fileName
     * *
     * @return
     */
    fun isFileExist(fileName: String): Boolean {
        val file = File(fileName)
        return file.exists()
    }


    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * Title: write2SDFromInput
     *
     * Description:
     * @param path
     * *
     * @param fileName
     * *
     * @param input
     * *
     * @return
     */
    fun write2SDFromInput(path: String, fileName: String,
                          input: InputStream): File? {
        var file: File? = null
        var output: OutputStream? = null
        try {
            createFolder(path)
            file = createNewFile(path + "/" + fileName)
            output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var len = -1

            while (run {len = input.read(buffer); len >= 0}) {
                output.write(buffer, 0, len)
            }
            output.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                output!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return file
    }

    /**
     * 从文件中一行一行的读取文件
     *
     * Title: readFile
     *
     * Description:
     * @param file
     * *
     * @return
     */
    fun readFile(file: File): String {
        var content = ""
        var string = ""
        val read = FileReader(file)
        val br = BufferedReader(read)
        try {
            while (run { content = br.readLine().toString().trim { it <= ' ' }; content.isNotEmpty() }) {
                string += content + "\r\n"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                read.close()
                br.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        println("string=" + string)
        return string.toString()
    }

    /**
     * check the state of sdCard
     * @return if is available return true
     */
    val isSDCardAvailable: Boolean
        get() {
            val canRead = Environment.getExternalStorageDirectory().canRead()
            val onlyRead = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY
            val unMounted = Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED

            return !(!canRead || onlyRead || unMounted)
        }

    /**
     * check the directory is available , if had existed return true, otherwise created
     * @param path the path of file
     * *
     * @return
     */
    fun isDirectoryAvailable(path: File): Boolean {
        if (path.exists()) {
            return true
        }
        return path.mkdirs()
    }

    /**
     * check the directory is available , if had existed return true, otherwise created
     * @param path the path of file
     * *
     * @return
     */
    fun isDirectoryAvailable(path: String): Boolean {
        val file = File(path)
        return isDirectoryAvailable(file)
    }

    /**
     * check the file is exists or not
     * @param dirctory the file directory
     * *
     * @param fileName the file name
     * *
     * @return if exists return true
     */
    fun isFileExists(directory: File, fileName: String): Boolean {
        val file = File(directory, fileName)
        return file.exists()
    }

    /**
     * create a file , if not exist and create it
     * @param directory the directory of the file exists
     * *
     * @param fileName the name of file
     * *
     * @return if success return true, otherwise create fail or file exist
     */
    fun createFile(directory: File, fileName: String): Boolean {
        if (!isFileExists(directory, fileName)) {
            val file = File(directory, fileName)
            try {
                return file.createNewFile()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }
        return false
    }

    /**
     * create a folder
     * @param pathName the directory of folder, for example:
     *
     ***mnt\sdcard\app_name\folder_name**
     * *               you should input [.createFolder], if not exist will create it,
     * *               otherwise return exist path
     * *
     * @return if success return true
     */
    fun createFolder(vararg pathName: String): Boolean {
        val folder = File(buildDirectory(*pathName))
        if (!folder.exists()) {
            return folder.mkdirs()
        }
        return true
    }

    /**
     * 判断文件的类型
     * @param fileName 文件
     * *
     * @param postfixName 文件后缀集合
     * *
     * @return 如果是postfixName后缀文件，则返回true
     */
    fun checkPostfixOfFile(file: File, vararg postfixName: String): Boolean {
        val fName = file.name
        val dotIndex = fName.lastIndexOf(".")//获取后缀名前的分隔符"."在fName中的位置。
        if (dotIndex < 0) {
            return false
        }
        val end = fName.substring(dotIndex + 1, fName.length).toLowerCase()//获取文件的后缀名
        for (postfix in postfixName) {
            if (end == postfix) return true
        }
        return false
    }

    /**
     * create folder and return path, if exist not create
     *
     * Title: createAppTempFolder
     *
     * Description:
     * @return the path of directory
     */
    fun getFolderByPath(vararg pathName: String): String {
        val file = buildDirectory(*pathName)
        val folder = File(file)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return file
    }

    /**
     * 拷贝文件

     * @param fromFile
     * *
     * @param toFile
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyFile(fromFile: File, toFile: File) {
        if (!fromFile.exists() || fromFile.length() == 0L) return
        if (!toFile.exists()) {
            createNewFile(toFile)
        }
        val from = FileInputStream(fromFile)
        val to = FileOutputStream(toFile)
        val buffer = ByteArray(1024)
        var bytesRead: Int = -1
        while (run {bytesRead = from.read(buffer); bytesRead >= 0})
            to.write(buffer, 0, bytesRead) // write
        from.close()
        to.close()
    }

    @Throws(IOException::class)
    fun copyFile(fromFile: File, toFile: String) {
        copyFile(fromFile, File(toFile))
    }

    /**
     * generate the path by the dirname
     * @param DirName the hierarchy of the file directory
     * *
     * @return the full path of the application
     */
    private fun buildDirectory(vararg DirName: String): String {
        val builder = StringBuilder()
        builder.append(Environment.getExternalStorageDirectory())
        builder.append(File.separatorChar)
        for (s in DirName) {
            builder.append(File.separatorChar)
            builder.append(sanitizeName(s))
        }
        return builder.toString()
    }

    /**
     * A set of characters that are prohibited from being in file names.
     */
    private val PROHIBITED_CHAR_PATTERN = Pattern.compile("[^ A-Za-z0-9_.()]+")

    /**
     * The maximum length of a filename, as per the FAT32 specification.
     */
    private val MAX_FILENAME_LENGTH = 50

    /**
     * Normalizes the input string and make sure it is a valid fat32 file name.

     * @param name the name to normalize
     * *
     * @param overheadSize the number of additional characters that will be added
     * *        to the name after sanitization
     * *
     * @return the sanitized name
     */
    private fun sanitizeName(name: String): String {
        val cleaned = PROHIBITED_CHAR_PATTERN.matcher(name).replaceAll("")
        return if (cleaned.length > MAX_FILENAME_LENGTH)
            cleaned.substring(0, MAX_FILENAME_LENGTH)
        else
            cleaned.toString()
    }
}
/**
 * 向Text文件中写入内容
 * @param file
 * *
 * @param content
 * *
 * @return
 */
