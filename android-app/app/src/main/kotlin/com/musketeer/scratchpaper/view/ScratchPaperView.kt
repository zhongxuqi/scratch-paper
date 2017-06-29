/**
 * @Title: ScratchPaperView.java
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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.utils.ImageUtils
import com.musketeer.scratchpaper.utils.LogUtils

import java.util.LinkedList

/**
 * @author zhongxuqi
 */
class ScratchPaperView : SurfaceView, SurfaceHolder.Callback {
    companion object {
        val TAG = "ScratchPaperView"
    }

    //app config
    /**
     * @return the max_undo
     */
    /**
     * @param max_undo the max_undo to set
     */
    var max_undo = 100

    private val boundX = 20
    private val boundY = 20
    private var minScale = 0.5f
    private val maxScale = 5f

    private var mHolder: SurfaceHolder? = null
    private val mDeskMatrix = Matrix()
    private var mDeskBackGround: Bitmap? = null
    private var mPaperBackGround: Bitmap? = null
    private val offsetXY = IntArray(2)
    private var mPaperId: Int = 0

    private var mEraserImage: Bitmap? = null
    private val mEraserMatrix = Matrix()

    private enum class State {
        NONE, DRAWING, DRAG_ZOOM, FLING, ANIMATE_ZOOM
    }

    private var state: State? = null

    //finger point notice
    var isErase = false
    private val mNoticePaint = Paint()

    //finger point location
    private var currFingerPoint: PointF? = null

    private val LastLocation = PointF(0f, 0f)
    private var LastScale = 1f

    /**
     * @return the mPaperWidth
     */
    var paperWidth: Int = 0
        private set
    /**
     * @return the mPaperHeight
     */
    var paperHeight: Int = 0
        private set
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

        //init desk background image
        mDeskBackGround = BitmapFactory.decodeResource(resources,
                R.mipmap.bg_desk_default)
        mPaperId = R.mipmap.bg_paper
        mPaperBackGround = BitmapFactory.decodeResource(resources,
                R.mipmap.bg_paper).copy(Bitmap.Config.ARGB_8888, true)
        paperWidth = mPaperBackGround!!.width
        paperHeight = mPaperBackGround!!.height

        mHolder = holder
        mHolder!!.addCallback(this)

        setOnTouchListener(PrivateOnTouchListener())

        mPaint.isAntiAlias = true
        mPaint.isDither = true

        mEraserImage = BitmapFactory.decodeResource(resources, R.mipmap.icon_eraser)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // TODO Auto-generated method stub
        initPaperPosition()

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

                    //draw desk background
                    mDeskMatrix.setScale(1.0f * width / mDeskBackGround!!.width.toFloat(),
                            1.0f * height / mDeskBackGround!!.height.toFloat())
                    canvas.drawBitmap(mDeskBackGround!!, mDeskMatrix, null)

                    //fix strokes
                    if (mStrokeList.size > max_undo) {
                        for (i in 0..mStrokeList.size - max_undo - 1) {
                            val mCanvas = Canvas(mPaperBackGround!!)
                            val realStartX = mStrokeList[0].startX
                            val realStartY = mStrokeList[0].startY
                            val realEndX = mStrokeList[0].endX
                            val realEndY = mStrokeList[0].endY

                            //check if inside of screen
                            mPaint.color = mStrokeList[0].color
                            mPaint.setStrokeWidth(if (mStrokeList[0].strokeWidth / maxScale >= 1)
                                mStrokeList[0].strokeWidth / maxScale
                            else
                                1F)
                            mCanvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint)
                            mStrokeList.removeAt(0)
                        }
                    }

                    // draw paper background
                    canvas.drawBitmap(mPaperBackGround!!, mMatrix, null)

                    // draw strokes
                    for (i in mStrokeList.indices) {
                        val realStartX = LastLocation.x + mStrokeList[i].startX * LastScale
                        val realStartY = LastLocation.y + mStrokeList[i].startY * LastScale
                        val realEndX = LastLocation.x + mStrokeList[i].endX * LastScale
                        val realEndY = LastLocation.y + mStrokeList[i].endY * LastScale

                        //check if inside of screen
                        if (realStartX > 0 && realStartX < width && realStartY > 0 && realStartY < height || realEndX > 0 && realEndX < width && realEndY > 0 && realEndY < height) {
                            mPaint.color = mStrokeList[i].color
                            // mPaint.setStrokeWidth(mStrokeList.get(i).strokeWidth);
                            mPaint.setStrokeWidth(if (mStrokeList[i].strokeWidth * LastScale / maxScale >= 1)
                                mStrokeList[i].strokeWidth * LastScale / maxScale
                            else
                                1F)
                            canvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint)
                        }
                    }

                    if (isErase && currFingerPoint != null) {
//                        mNoticePaint.setStrokeWidth(if (strokeWidth / maxScale >= 1)
//                            strokeWidth / maxScale
//                        else
//                            1F)
//                        mNoticePaint.color = Color.GREEN
//                        canvas.drawPoint(currFingerPoint!!.x, currFingerPoint!!.y, mNoticePaint)
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
        private var pointLastLoca2 = PointF(0f, 0f)
        private var centerLoca = PointF(0f, 0f)
        private var LastRange = 0f

        //
        // Remember last point position for dragging
        //

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            var pointCurrLoca1 = PointF(pointLastLoca1.x, pointLastLoca1.y)
            var pointCurrLoca2 = PointF(pointLastLoca2.x, pointLastLoca2.y)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {

                    //record last location
                    when (event.pointerCount) {
                        1 -> pointLastLoca1 = PointF(event.getX(0), event.getY(0))
                        2 -> {
                            pointLastLoca2 = PointF(event.getX(1), event.getY(1))
                            LastRange = Math.sqrt(Math.pow((pointLastLoca2.x - pointLastLoca1.x).toDouble(), 2.0) + Math.pow((pointLastLoca2.y - pointLastLoca1.y).toDouble(), 2.0)).toFloat()
                        }
                    }
                    setStateByPointerCount(event.pointerCount)
                }
                MotionEvent.ACTION_MOVE -> {
                    var CurrRange = LastRange

                    //record current location
                    when (event.pointerCount) {
                        1 -> {
                            pointCurrLoca1 = PointF(event.getX(0), event.getY(0))

                            //record finger location for point notice
                            currFingerPoint = pointCurrLoca1
                        }
                        2 -> {
                            pointCurrLoca1 = PointF(event.getX(0), event.getY(0))
                            pointCurrLoca2 = PointF(event.getX(1), event.getY(1))
                            CurrRange = Math.sqrt(Math.pow((pointLastLoca2.x - pointLastLoca1.x).toDouble(), 2.0) + Math.pow((pointLastLoca2.y - pointLastLoca1.y).toDouble(), 2.0)).toFloat()
                        }
                    }

                    when (state) {
                        ScratchPaperView.State.NONE -> {
                        }
                        ScratchPaperView.State.DRAWING -> {
                            drawStroke(pointLastLoca1, pointCurrLoca1)
                            pointLastLoca1.set(pointCurrLoca1.x, pointCurrLoca1.y)
                        }
                        ScratchPaperView.State.DRAG_ZOOM -> {

                            //calculate the scale and translation
                            val deltaX = (pointCurrLoca1.x - pointLastLoca1.x + pointCurrLoca2.x - pointLastLoca2.x) / 2
                            val deltaY = (pointCurrLoca1.y - pointLastLoca1.y + pointCurrLoca2.y - pointLastLoca2.y) / 2
                            pointLastLoca1.set(pointCurrLoca1.x, pointCurrLoca1.y)
                            pointLastLoca2.set(pointCurrLoca2.x, pointCurrLoca2.y)
                            centerLoca = PointF((pointCurrLoca1.x + pointCurrLoca2.x) / 2,
                                    (pointCurrLoca1.y + pointCurrLoca2.y) / 2)

                            //avoid NaN exception
                            if (LastRange > 0) {
                                trans(centerLoca, PointF(deltaX, deltaY), CurrRange / LastRange)
                                LastRange = CurrRange
                            }
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
            2 -> setState(State.DRAG_ZOOM)
            else -> setState(State.DRAG_ZOOM)
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
        stroke.startX = (startPoint.x - LastLocation.x) / LastScale
        stroke.startY = (startPoint.y - LastLocation.y) / LastScale
        stroke.endX = (endPoint.x - LastLocation.x) / LastScale
        stroke.endY = (endPoint.y - LastLocation.y) / LastScale
        if (stroke.startX > 0 - offsetXY[0] && stroke.startX <= paperWidth + offsetXY[0] &&
                stroke.startY > 0 - offsetXY[1] && stroke.startY <= paperHeight + offsetXY[1] &&
                stroke.endX > 0 - offsetXY[0] && stroke.endX <= paperWidth + offsetXY[0] &&
                stroke.endY > 0 - offsetXY[1] && stroke.endY <= paperHeight + offsetXY[1]) {
            stroke.color = color
            if (isErase) {
                stroke.strokeWidth = (strokeWidth.toFloat() / LastScale).toInt()
            } else {
                stroke.strokeWidth = strokeWidth
            }
            mStrokeList.add(stroke)
        }
    }

    private fun setState(state: State) {
        this.state = state
    }

    /**
     * 对图片进行移动与缩放
     * @param centerLoca
     * *
     * @param deltaPoint
     * *
     * @param currScale
     */
    fun trans(centerLoca: PointF, deltaPoint: PointF, currScale: Float) {
        // TODO Auto-generated method stub
        var lastScale = LastScale * currScale
        mMatrix.reset()

        //translation because of scale
        var ScaleDeltaX = 0f
        var ScaleDeltaY = 0f

        if (lastScale > maxScale) {
            lastScale = maxScale
        } else if (lastScale < minScale) {
            lastScale = minScale
        } else {
            ScaleDeltaX = -(currScale - 1) * (centerLoca.x - LastLocation.x)
            ScaleDeltaY = -(currScale - 1) * (centerLoca.y - LastLocation.y)
        }
        mMatrix.postScale(lastScale, lastScale)

        if (!isOutSide(LastLocation, deltaPoint.x + ScaleDeltaX, deltaPoint.y + ScaleDeltaY)) {
            LastLocation.offset(deltaPoint.x + ScaleDeltaX, deltaPoint.y + ScaleDeltaY)
        }
        mMatrix.postTranslate(LastLocation.x, LastLocation.y)
        LastScale = lastScale
    }

    /**
     * 判断是否超出边界
     * @param deltaX
     * *
     * @param deltaY
     * *
     * @return
     */
    fun isOutSide(location: PointF, deltaX: Float, deltaY: Float): Boolean {
        val resultX = location.x + deltaX
        val resultY = location.y + deltaY
        if (resultX > width - boundX ||
                resultX + paperWidth * LastScale < boundX ||
                resultY > height - boundY ||
                resultY + paperHeight * LastScale < boundY) {
            return true
        } else {
            return false
        }
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
     * 删除最后一次操作
     */
    fun undoLastAction() {
        if (mStrokeList.size > 0) {
            mStrokeList.removeAt(mStrokeList.size - 1)
        }
    }

    /**
     * 清除所有操作
     */
    fun clearAll() {
        mStrokeList.clear()
        mPaperBackGround = BitmapFactory.decodeResource(resources,
                mPaperId).copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * 截取草稿纸内容
     * @param canvas
     */
    fun doDrawForScreenShot(canvas: Canvas) {

        //draw paper background
        canvas.drawBitmap(mPaperBackGround!!, 0f, 0f, null)

        //draw strokes
        for (i in mStrokeList.indices) {
            val realStartX = mStrokeList[i].startX
            val realStartY = mStrokeList[i].startY
            val realEndX = mStrokeList[i].endX
            val realEndY = mStrokeList[i].endY

            //check if inside of screen
            mPaint.color = mStrokeList[i].color
            mPaint.setStrokeWidth(if (mStrokeList[0].strokeWidth / maxScale >= 1)
                mStrokeList[0].strokeWidth / maxScale
            else
                1F)
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
            val mCanvas = Canvas(mPaperBackGround!!)

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

            return mPaperBackGround
        }
        set(bitmap) {
            mPaperBackGround = Bitmap.createBitmap(bitmap)
            paperWidth = mPaperBackGround!!.width
            paperHeight = mPaperBackGround!!.height
        }

    /**
     * 设置桌面与草稿纸
     * @param paperId
     * *
     * @param deskId
     */
    fun setPaperAndDesk(paperId: Int, deskId: Int) {
        mPaperId = paperId
        mPaperBackGround = BitmapFactory.decodeResource(resources,
                paperId).copy(Bitmap.Config.ARGB_8888, true)
        mPaperBackGround = ImageUtils.drawImageDropShadow(mPaperBackGround!!, offsetXY)

        paperWidth = mPaperBackGround!!.width
        paperHeight = mPaperBackGround!!.height

        mDeskBackGround = BitmapFactory.decodeResource(resources,
                deskId)
    }

    /**
     * 准备书写
     */
    fun prepareForWrite() {
        LastLocation.set(boundX.toFloat(), boundY.toFloat())
        LastScale = maxScale
        mMatrix.reset()
        mMatrix.postScale(LastScale, LastScale)
        mMatrix.postTranslate(LastLocation.x, LastLocation.y)
    }

    /**
     * 初始化纸张位置
     */
    fun initPaperPosition() {
        val scaleX = 1.0f * (width - 2 * boundX) / mPaperBackGround!!.width.toFloat()
        val scaleY = 1.0f * (height - 2 * boundY) / mPaperBackGround!!.height.toFloat()
        mMatrix.reset()
        if (scaleX < scaleY) {
            mMatrix.postScale(scaleX, scaleX)
            LastLocation.set(boundX.toFloat(), (height - mPaperBackGround!!.height * scaleX) / 2)
            mMatrix.postTranslate(LastLocation.x, LastLocation.y)
            LastScale = scaleX
        } else {
            mMatrix.postScale(scaleY, scaleY)
            LastLocation.set((width - mPaperBackGround!!.width * scaleY) / 2, boundY.toFloat())
            mMatrix.postTranslate(LastLocation.x, LastLocation.y)
            LastScale = scaleY
        }
        minScale = LastScale
    }

    /**
     * 滚动屏幕
     * @param deltaPoint
     */
    fun scrollTo(deltaPoint: PointF) {
        mMatrix.reset()
        mMatrix.postScale(LastScale, LastScale)
        if (!isOutSide(LastLocation, deltaPoint.x, deltaPoint.y)) {
            LastLocation.offset(deltaPoint.x, deltaPoint.y)
            mMatrix.postTranslate(LastLocation.x, LastLocation.y)
        }
    }

    /**
     * 保存草稿纸内容
     * @param canvas
     */
    fun doDrawForSave(canvas: Canvas) {
        //draw paper background
        canvas.drawBitmap(mPaperBackGround!!, 0f, 0f, null)
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
