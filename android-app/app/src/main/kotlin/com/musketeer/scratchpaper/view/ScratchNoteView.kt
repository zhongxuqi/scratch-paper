/**
 * @Title: ScratchNoteView.java
 * *
 * @Package com.musketeer.scratchpaper.view
 * *
 * *
 * @author musketeer zhongxuqi@163.com
 * *
 * @date 2014-11-12 下午1:40:06
 * *
 * @version V1.0
 */
package com.musketeer.scratchpaper.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.utils.AppPreferenceUtils
import com.musketeer.scratchpaper.utils.ImageUtils
import com.musketeer.scratchpaper.utils.LogUtils

import java.util.LinkedList


/**
 * @author zhongxuqi
 */
class ScratchNoteView : SurfaceView, SurfaceHolder.Callback {
    companion object {
        val TAG = "ScratchNoteView"
    }

    //app config
    /**
     * @return the max_undo
     */
    /**
     * @param max_undo the max_undo to set
     */
    var max_undo = 100

    var isEdited : Boolean = false

    private var mHolder: SurfaceHolder? = null
    var mNoteBackGround: Bitmap = BitmapFactory.decodeResource(resources, AppPreferenceUtils.getPaperChoose(context)).copy(Bitmap.Config.ARGB_8888, true)
    private val mNoteMatrix = Matrix()
    private var mPaperId: Int = 0

    private var mEraserImage: Bitmap? = null
    private val mEraserMatrix = Matrix()

    private enum class State {
        NONE, DRAWING
    }

    private var state: State? = null

    //finger point notice
    var isErase = false

    //finger point location
    private var currFingerPoint: PointF? = null

    private val LastLocation = PointF(0f, 0f)

    private val mMatrix = Matrix()

    private var mStrokeList: MutableList<DrawStroke> = LinkedList()

    //stroke attribute
    private val mPaint = Paint()
    var strokeWidth = 5
    var color = Color.BLACK

    //thread pool
    private var isRun: Boolean = false
    private var MainDrawThread: Thread? = null

    /**
     * @param context
     * *
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // TODO Auto-generated constructor stub
        init()
    }

    /**
     * @param context
     */
    constructor(context: Context) : super(context) {
        // TODO Auto-generated constructor stub
        init()
    }

    /**
     * @param context
     * *
     * @param attrs
     * *
     * @param defStyle
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        // TODO Auto-generated constructor stub
        init()
    }

    fun init() {
        mPaperId = R.mipmap.paper_medium
        mNoteBackGround = BitmapFactory.decodeResource(resources,
                R.mipmap.bg_paper).copy(Bitmap.Config.ARGB_8888, true)

        mHolder = holder
        mHolder!!.addCallback(this)

        setOnTouchListener(PrivateOnTouchListener())

        mPaint.isAntiAlias = true
        mPaint.isDither = true

        mEraserImage = BitmapFactory.decodeResource(resources, R.mipmap.icon_eraser)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

        //启动主绘制线程
        isRun = true
        MainDrawThread = Thread(MainDrawRunable())
        MainDrawThread!!.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {
        // TODO Auto-generated method stub

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // TODO Auto-generated method stub

    }

    override fun performClick(): Boolean {
        // TODO Auto-generated method stub
        return super.performClick()
    }

    internal inner class MainDrawRunable : Runnable {

        override fun run() {
            // TODO Auto-generated method stub
            var canvas: Canvas? = null
            while (isRun) {
                try {
                    canvas = mHolder!!.lockCanvas()
                    //get canvas
                    if (canvas == null) {
                        return
                    }

                    //fix strokes
                    if (mStrokeList.size > max_undo) {
                        for (i in 0..mStrokeList.size - max_undo - 1) {
                            val mCanvas = Canvas(mNoteBackGround)
                            val realStartX = mStrokeList[0].startX
                            val realStartY = mStrokeList[0].startY
                            val realEndX = mStrokeList[0].endX
                            val realEndY = mStrokeList[0].endY

                            //check if inside of screen
                            mPaint.color = mStrokeList[0].color
                            mPaint.setStrokeWidth(mStrokeList[0].strokeWidth.toFloat())
                            mCanvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint)
                            mStrokeList.removeAt(0)
                        }
                    }

                    // draw note background
                    checkAndInitNoteSize()
                    mNoteMatrix.reset()
                    canvas.drawBitmap(mNoteBackGround, mNoteMatrix, null)

                    // draw strokes
                    for (i in mStrokeList.indices) {
                        val realStartX = LastLocation.x + mStrokeList[i].startX
                        val realStartY = LastLocation.y + mStrokeList[i].startY
                        val realEndX = LastLocation.x + mStrokeList[i].endX
                        val realEndY = LastLocation.y + mStrokeList[i].endY

                        //check if inside of screen
                        if (realStartX > 0 && realStartX < width && realStartY > 0 && realStartY < height || realEndX > 0 && realEndX < width && realEndY > 0 && realEndY < height) {
                            mPaint.color = mStrokeList[i].color
                            // mPaint.setStrokeWidth(mStrokeList.get(i).strokeWidth);
                            mPaint.setStrokeWidth(mStrokeList[i].strokeWidth.toFloat())
                            canvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint)
                        }
                    }

                    if (isErase && currFingerPoint != null) {
                        mEraserMatrix.reset()
                        mEraserMatrix.setTranslate(currFingerPoint!!.x - mEraserImage!!.width / 2, currFingerPoint!!.y - mEraserImage!!.height / 2)
                        canvas.drawBitmap(mEraserImage, mEraserMatrix, null)
                    }

                    // point canvas
                    mHolder!!.unlockCanvasAndPost(canvas)
                    canvas = null
                    Thread.sleep(16)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    /**
     * 触控操作监听
     */
    private inner class PrivateOnTouchListener : View.OnTouchListener {
        private var pointLastLoca1 = PointF(0f, 0f)

        //
        // Remember last point position for dragging
        //

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            var pointCurrLoca1 = PointF(pointLastLoca1.x, pointLastLoca1.y)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {

                    //record last location
                    when (event.pointerCount) {
                        1 -> pointLastLoca1 = PointF(event.getX(0), event.getY(0))
                    }
                    setStateByPointerCount(event.pointerCount)
                }
                MotionEvent.ACTION_MOVE -> {

                    //record current location
                    when (event.pointerCount) {
                        1 -> {
                            pointCurrLoca1 = PointF(event.getX(0), event.getY(0))

                            //record finger location for point notice
                            currFingerPoint = pointCurrLoca1
                        }
                    }

                    when (state) {
                        ScratchNoteView.State.NONE -> {
                        }
                        ScratchNoteView.State.DRAWING -> {
                            drawStroke(pointLastLoca1, pointCurrLoca1)
                            pointLastLoca1.set(pointCurrLoca1.x, pointCurrLoca1.y)
                        }
                        else -> {
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    currFingerPoint = null
                    v.performClick()
                    LogUtils.d(TAG, "event.pointerCount: "+event.pointerCount)
                    when (event.pointerCount) {
                        1 -> setStateByPointerCount(0)
                        2 -> setStateByPointerCount(1)
                    }
                }
            }
            return true
        }
    }

    /**
     * @param pointerCount
     */
    fun setStateByPointerCount(pointerCount: Int) {
        // TODO Auto-generated method stub
        when (pointerCount) {
            0 -> setState(State.NONE)
            1 -> setState(State.DRAWING)
            else -> setState(State.DRAWING)
        }
    }

    /**
     * draw the strokes
     * @param startPoint
     * *
     * @param endPoint
     */
    fun drawStroke(startPoint: PointF, endPoint: PointF) {
        // TODO Auto-generated method stub
        val stroke = DrawStroke()
        stroke.startX = (startPoint.x - LastLocation.x)
        stroke.startY = (startPoint.y - LastLocation.y)
        stroke.endX = (endPoint.x - LastLocation.x)
        stroke.endY = (endPoint.y - LastLocation.y)
        stroke.color = color
        stroke.strokeWidth = strokeWidth
        mStrokeList.add(stroke)
        isEdited = true
    }

    private fun setState(state: State) {
        this.state = state
    }

    //record stroke entity
    inner class DrawStroke {
        internal var startX: Float = 0.toFloat()
        internal var startY: Float = 0.toFloat()
        internal var endX: Float = 0.toFloat()
        internal var endY: Float = 0.toFloat()
        internal var color: Int = 0
        internal var strokeWidth: Int = 0
    }

    /**
     * 初始化bitmap size
     */
    fun checkAndInitNoteSize() {
        if (mNoteBackGround.width != width || mNoteBackGround.height != height) {
            val matrix = Matrix()
            matrix.postScale(width.toFloat()/mNoteBackGround.width.toFloat(), height.toFloat()/mNoteBackGround.height.toFloat())
            mNoteBackGround = Bitmap.createBitmap(mNoteBackGround, 0, 0, mNoteBackGround.width, mNoteBackGround.height, matrix, true)
        }
    }

    /**
     * 删除最后一次操作
     */
    fun undoLastAction() {
        if (mStrokeList.size > 0) {
            var lastStroke = mStrokeList.get(mStrokeList.size - 1)
            mStrokeList.removeAt(mStrokeList.size - 1)
            while (mStrokeList.size > 0 && mStrokeList.get(mStrokeList.size - 1).endX == lastStroke.startX &&
                    mStrokeList.get(mStrokeList.size - 1).endY == lastStroke.startY) {
                lastStroke = mStrokeList.get(mStrokeList.size - 1)
                mStrokeList.removeAt(mStrokeList.size - 1)
            }
        }
    }

    /**
     * 清除所有操作
     */
    fun clearAll() {
        mStrokeList.clear()
        mNoteBackGround = BitmapFactory.decodeResource(resources,
                mPaperId).copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * 截取草稿纸内容
     * @param canvas
     */
    fun doDrawForScreenShot(canvas: Canvas) {

        //draw paper background
        canvas.drawBitmap(mNoteBackGround!!, 0f, 0f, null)

        //draw strokes
        for (i in mStrokeList.indices) {
            val realStartX = mStrokeList[i].startX
            val realStartY = mStrokeList[i].startY
            val realEndX = mStrokeList[i].endX
            val realEndY = mStrokeList[i].endY

            //check if inside of screen
            mPaint.color = mStrokeList[i].color
            mPaint.setStrokeWidth(mStrokeList[i].strokeWidth.toFloat())
            canvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint)
        }
    }

    /**
     * 获取草稿纸内容
     * @return
     */
    /**
     * 设置草稿纸背景
     * @param bitmap
     */
    //check if inside of screen
    var paperBackGround: Bitmap?
        get() {
            val mCanvas = Canvas(mNoteBackGround)

            for (i in mStrokeList.indices) {
                val realStartX = mStrokeList[i].startX
                val realStartY = mStrokeList[i].startY
                val realEndX = mStrokeList[i].endX
                val realEndY = mStrokeList[i].endY
                if (realStartX > 0 && realStartX < width && realStartY > 0 && realStartY < height || realEndX > 0 && realEndX < width && realEndY > 0 && realEndY < height) {
                    mPaint.color = mStrokeList[i].color
                    mPaint.strokeWidth = mStrokeList[i].strokeWidth.toFloat()
                    mCanvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint)
                }
            }

            return mNoteBackGround
        }
        set(bitmap) {
            if (bitmap == null) return
            if (width > 0 && height > 0) {
                mNoteBackGround = Bitmap.createScaledBitmap(bitmap, width, height, false)
            } else {
                mNoteBackGround = bitmap
            }
        }

    /**
     * 设置便签
     * @param paperId
     * *
     * @param deskId
     */
    fun setPaper(paperId: Int) {
        mPaperId = paperId
//        if (width > 0 && height > 0) {
//            mNoteBackGround = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,
//                    paperId).copy(Bitmap.Config.ARGB_8888, true), width, height, false)
//        } else {
//            mNoteBackGround = BitmapFactory.decodeResource(resources, paperId).copy(Bitmap.Config.ARGB_8888, true)
//        }
        mNoteBackGround = BitmapFactory.decodeResource(resources, paperId).copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * 保存草稿纸内容
     * @param canvas
     */
    fun doDrawForSave(canvas: Canvas) {
        //draw paper background
        canvas.drawBitmap(mNoteBackGround!!, 0f, 0f, null)
    }

    /**
     * @return the mStrokeList
     */
    /**
     * @param mStrokeList the mStrokeList to set
     */
    var strokeList: MutableList<DrawStroke>
        get() = mStrokeList
        set(mStrokeList) {
            this.mStrokeList = mStrokeList
        }

    fun clearStrokeList() {
        this.mStrokeList = LinkedList<DrawStroke>()
    }

    fun startDraw() {
        if (MainDrawThread == null) {
            //启动主绘制线程
            isRun = true
            MainDrawThread = Thread(MainDrawRunable())
            MainDrawThread!!.start()
        }
    }

    fun stopDraw() {
        isRun = false
        MainDrawThread = null
    }

}
