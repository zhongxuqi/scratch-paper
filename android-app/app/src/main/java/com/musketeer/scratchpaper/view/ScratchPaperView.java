/**   
 * @Title: ScratchPaperView.java 
 * @Package com.musketeer.scratchpaper.view 
 *
 * @author musketeer zhongxuqi@163.com  
 * @date 2014-11-12 下午1:40:06 
 * @version V1.0   
 */
package com.musketeer.scratchpaper.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.utils.ImageUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhongxuqi
 * 
 */
public class ScratchPaperView extends SurfaceView implements
		SurfaceHolder.Callback {

	//app config
	private int max_undo=100;
	
	private final int boundX=20;
	private final int boundY=20;
	private float minScale=0.5f;
	private final float maxScale=5f;
	
	private SurfaceHolder mHolder;
	private Matrix mDeskMatrix=new Matrix();
	private Bitmap mDeskBackGround;
	private Bitmap mPaperBackGround;
	private int[] offsetXY=new int[2];
	private int mPaperId;
	
	private enum State { NONE, DRAWING, DRAG_ZOOM, FLING, ANIMATE_ZOOM };
    private State state;

	//finger point notice
	private boolean isPointNotice = false;
	private final int pointNoticeRadius = 30;
	private Paint mNoticePaint=new Paint();

	//finger point location
	private PointF currFingerPoint = null;
    
    private PointF LastLocation = new PointF(0,0);
    private float LastScale=1;
    
    private int mPaperWidth,mPaperHeight;
    private Matrix mMatrix=new Matrix();
    
    private List<DrawStroke> mStrokeList=new LinkedList<DrawStroke>();
    
    //stroke attribute
    private Paint mPaint=new Paint();
    private int mStrokeWidth=5;
    private int mColor=Color.BLACK;
    
    //thread pool
    private boolean isRun;
    private Thread MainDrawThread;

	/**
	 * @param context
	 * @param attrs
	 */
	public ScratchPaperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 */
	public ScratchPaperView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ScratchPaperView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init() {
		
		//init desk background image
		mDeskBackGround=BitmapFactory.decodeResource(getResources(),
				R.mipmap.bg_desk_default);
		mPaperId=R.mipmap.bg_paper;
		mPaperBackGround = BitmapFactory.decodeResource(getResources(),
				R.mipmap.bg_paper).copy(Bitmap.Config.ARGB_8888, true);
		mPaperWidth=mPaperBackGround.getWidth();
		mPaperHeight=mPaperBackGround.getHeight();

		mHolder = getHolder();
		mHolder.addCallback(this);
		
		setOnTouchListener(new PrivateOnTouchListener());
		
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		initPaperPosition();
		
		//启动主绘制线程
		isRun=true;
		MainDrawThread=new Thread(new MainDrawRunable());
		MainDrawThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		return super.performClick();
	}
	
	class MainDrawRunable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Canvas canvas=null;
			while (isRun) {
				try {
					canvas = mHolder.lockCanvas();
					//get canvas
					if (canvas==null) {
						return;
					}
					
					//draw desk background
					mDeskMatrix.setScale(1.0f*getWidth()/(float)mDeskBackGround.getWidth(),
							1.0f*getHeight()/(float)mDeskBackGround.getHeight());
					canvas.drawBitmap(mDeskBackGround, mDeskMatrix, null);
					
					//fix strokes
					if (mStrokeList.size()>max_undo) {
						for (int i=0;i<mStrokeList.size()-max_undo;i++) {
							Canvas mCanvas=new Canvas(mPaperBackGround);
							float realStartX=mStrokeList.get(0).startX;
							float realStartY=mStrokeList.get(0).startY;
							float realEndX=mStrokeList.get(0).endX;
							float realEndY=mStrokeList.get(0).endY;
							
							//check if inside of screen
							mPaint.setColor(mStrokeList.get(0).color);
							mPaint.setStrokeWidth(mStrokeList.get(0).strokeWidth/maxScale>=1?
									mStrokeList.get(0).strokeWidth/maxScale:1);
							mCanvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint);
							mStrokeList.remove(0);
						}
					}
					
					//draw paper background
					canvas.drawBitmap(mPaperBackGround, mMatrix, null);
					
					//draw strokes
					for (int i=0;i<mStrokeList.size();i++) {
						if (mStrokeList.get(i)==null) {
							continue;
						}
						float realStartX=LastLocation.x+mStrokeList.get(i).startX*LastScale;
						float realStartY=LastLocation.y+mStrokeList.get(i).startY*LastScale;
						float realEndX=LastLocation.x+mStrokeList.get(i).endX*LastScale;
						float realEndY=LastLocation.y+mStrokeList.get(i).endY*LastScale;
						
						//check if inside of screen
						if ((realStartX>0&&realStartX<getWidth()&&realStartY>0&&realStartY<getHeight())||
								(realEndX>0&&realEndX<getWidth()&&realEndY>0&&realEndY<getHeight())) {
							mPaint.setColor(mStrokeList.get(i).color);
//							mPaint.setStrokeWidth(mStrokeList.get(i).strokeWidth);
							mPaint.setStrokeWidth(mStrokeList.get(i).strokeWidth*LastScale/maxScale>=1?
									mStrokeList.get(i).strokeWidth*LastScale/maxScale:1);
							canvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint);
						}
					}

					if (isPointNotice && currFingerPoint != null) {
						mNoticePaint.setStrokeWidth(pointNoticeRadius * LastScale);
						mNoticePaint.setColor(getResources().getColor(R.color.app_theme_color));
						canvas.drawCircle(currFingerPoint.x, currFingerPoint.y, pointNoticeRadius, mNoticePaint);
					}

					// point canvas
					if (canvas!=null) {
						mHolder.unlockCanvasAndPost(canvas);
					}
					canvas=null;
					Thread.sleep(16);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
     * 触控操作监听
     */
    private class PrivateOnTouchListener implements OnTouchListener {
    	private PointF pointLastLoca1 = new PointF(0,0);
    	private PointF pointLastLoca2 = new PointF(0,0);
    	private PointF centerLoca=new PointF(0,0);
    	private float LastRange=0;
    	
    	//
        // Remember last point position for dragging
        //
    	
    	@Override
        public boolean onTouch(View v, MotionEvent event) {
            PointF pointCurrLoca1=new PointF(pointLastLoca1.x,pointLastLoca1.y);
            PointF pointCurrLoca2=new PointF(pointLastLoca2.x,pointLastLoca2.y);
            
            switch (event.getAction()&MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:

					//record last location
					switch (event.getPointerCount()) {
					case 1:
						pointLastLoca1=new PointF(event.getX(0),event.getY(0));
						break;
					case 2:
						pointLastLoca2=new PointF(event.getX(1),event.getY(1));
						LastRange=(float)Math.sqrt(Math.pow(pointLastLoca2.x-pointLastLoca1.x,2)+
								Math.pow(pointLastLoca2.y-pointLastLoca1.y,2));
						break;
					}
					setStateByPointerCount(event.getPointerCount());
					break;
				case MotionEvent.ACTION_MOVE:
					float CurrRange=LastRange;

					//record current location
					switch (event.getPointerCount()) {
					case 1:
						pointCurrLoca1=new PointF(event.getX(0),event.getY(0));

						//record finger location for point notice
						currFingerPoint = pointCurrLoca1;
						break;
					case 2:
						pointCurrLoca1=new PointF(event.getX(0),event.getY(0));
						pointCurrLoca2=new PointF(event.getX(1),event.getY(1));
						CurrRange=(float)Math.sqrt(Math.pow(pointLastLoca2.x-pointLastLoca1.x,2)+
								Math.pow(pointLastLoca2.y-pointLastLoca1.y,2));
						break;
					}

					switch (state) {
						case NONE:

							break;
						case DRAWING:
							drawStroke(pointLastLoca1,pointCurrLoca1);
							pointLastLoca1.set(pointCurrLoca1.x, pointCurrLoca1.y);
							break;
						case DRAG_ZOOM:

							//calculate the scale and translation
							float deltaX=(pointCurrLoca1.x - pointLastLoca1.x+
									pointCurrLoca2.x - pointLastLoca2.x)/2;
							float deltaY=(pointCurrLoca1.y - pointLastLoca1.y+
									pointCurrLoca2.y - pointLastLoca2.y)/2;
							pointLastLoca1.set(pointCurrLoca1.x, pointCurrLoca1.y);
							pointLastLoca2.set(pointCurrLoca2.x, pointCurrLoca2.y);
							centerLoca=new PointF((pointCurrLoca1.x+pointCurrLoca2.x)/2,
									(pointCurrLoca1.y+pointCurrLoca2.y)/2);

							//avoid NaN exception
							if (LastRange>0) {
								trans(centerLoca,new PointF(deltaX,deltaY), CurrRange/LastRange);
								LastRange=CurrRange;
							}
							break;
						default:
							break;
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					currFingerPoint = null;
					v.performClick();
					switch (event.getPointerCount()) {
					case 2:
						setStateByPointerCount(0);
						break;
					}
					break;
            }
            return true;
        }
    }

	/**
	 * @param pointerCount
	 */
	public void setStateByPointerCount(int pointerCount) {
		// TODO Auto-generated method stub
		switch (pointerCount) {
		case 0:
			setState(State.NONE);
			break;
		case 1:
			setState(State.DRAWING);
			break;
		case 2:
			setState(State.DRAG_ZOOM);
			break;
		default:
			setState(State.DRAG_ZOOM);
			break;
		}
	}
    
    /**
     * draw the strokes
	 * @param startPoint
	 * @param endPoint
	 */
	public void drawStroke(PointF startPoint, PointF endPoint) {
		// TODO Auto-generated method stub
		final DrawStroke stroke=new DrawStroke();
		stroke.startX=(startPoint.x-LastLocation.x)/LastScale;
		stroke.startY=(startPoint.y-LastLocation.y)/LastScale;
		stroke.endX=(endPoint.x-LastLocation.x)/LastScale;
		stroke.endY=(endPoint.y-LastLocation.y)/LastScale;
		if ((stroke.startX>0-offsetXY[0]&&stroke.startX<=mPaperWidth+offsetXY[0])&&
				(stroke.startY>0-offsetXY[1]&&stroke.startY<=mPaperHeight+offsetXY[1])&&
				(stroke.endX>0-offsetXY[0]&&stroke.endX<=mPaperWidth+offsetXY[0])&&
				(stroke.endY>0-offsetXY[1]&&stroke.endY<=mPaperHeight+offsetXY[1])) {
			stroke.color=mColor;
			stroke.strokeWidth=mStrokeWidth;
			mStrokeList.add(stroke);
		}
	}

	private void setState(State state) {
    	this.state = state;
    }

	/**
	 * 对图片进行移动与缩放
	 * @param centerLoca
	 * @param deltaPoint
	 * @param currScale
	 */
	public void trans(PointF centerLoca,PointF deltaPoint, float currScale) {
		// TODO Auto-generated method stub
		float lastScale=LastScale*currScale;
		mMatrix.reset();
		
		//translation because of scale
		float ScaleDeltaX=0;
		float ScaleDeltaY=0;
		
		if (lastScale>maxScale) {
			lastScale=maxScale;
		} else if (lastScale<minScale) {
			lastScale=minScale;
		} else {
			ScaleDeltaX=-(currScale-1)*(centerLoca.x-LastLocation.x);
			ScaleDeltaY=-(currScale-1)*(centerLoca.y-LastLocation.y);
		}
		mMatrix.postScale(lastScale, lastScale);

		if (!isOutSide(LastLocation, deltaPoint.x+ScaleDeltaX, deltaPoint.y+ScaleDeltaY)) {
			LastLocation.offset(deltaPoint.x+ScaleDeltaX, deltaPoint.y+ScaleDeltaY);
		}
		mMatrix.postTranslate(LastLocation.x, LastLocation.y);
		LastScale=lastScale;
	}

	/**
	 * 判断是否超出边界
	 * @param deltaX
	 * @param deltaY
	 * @return
	 */
	public boolean isOutSide(PointF location,float deltaX, float deltaY) {
		float resultX=location.x+deltaX,resultY=location.y+deltaY;
		if ((resultX>getWidth()-boundX)||
				(resultX+mPaperWidth*LastScale<boundX)||
				(resultY>getHeight()-boundY)||
				(resultY+mPaperHeight*LastScale<boundY)) {
			return true;
		} else {
			return false;
		}
	}
	
	//record stroke entity
	public class DrawStroke {
		float startX;
		float startY;
		float endX;
		float endY;
		int color;
		int strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.mStrokeWidth = strokeWidth;
	}

	public int getStrokeWidth() {
		return mStrokeWidth;
	}

	public void setColor(int color) {
		this.mColor = color;
	}

	public int getColor() {
		return mColor;
	}

	public void setIsPointNotice(boolean isPointNotice) {
		this.isPointNotice = isPointNotice;
	}
	
	/**
	 * 删除最后一次操作
	 */
	public void undoLastAction() {
		if (mStrokeList.size()>0) {
			mStrokeList.remove(mStrokeList.size()-1);
		}
	}
	
	/**
	 * 清除所有操作
	 */
	public void clearAll() {
		mStrokeList.clear();
		mPaperBackGround = BitmapFactory.decodeResource(getResources(),
				mPaperId).copy(Bitmap.Config.ARGB_8888, true);
	}
	
	/**
	 * 截取草稿纸内容
	 * @param canvas
	 */
	public void doDrawForScreenShot(Canvas canvas) {
		
		//draw paper background
		canvas.drawBitmap(mPaperBackGround, 0, 0, null);
		
		//draw strokes
		for (int i=0;i<mStrokeList.size();i++) {
			float realStartX=mStrokeList.get(i).startX;
			float realStartY=mStrokeList.get(i).startY;
			float realEndX=mStrokeList.get(i).endX;
			float realEndY=mStrokeList.get(i).endY;
			
			//check if inside of screen
			mPaint.setColor(mStrokeList.get(i).color);
			mPaint.setStrokeWidth(mStrokeList.get(0).strokeWidth/maxScale>=1?
					mStrokeList.get(0).strokeWidth/maxScale:1);
			canvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint);
		}
	}
	
	/**
	 * 设置草稿纸背景
	 * @param bitmap
	 */
	public void setPaperBackGround(Bitmap bitmap) {
		mPaperBackGround=Bitmap.createBitmap(bitmap);
		mPaperWidth=mPaperBackGround.getWidth();
		mPaperHeight=mPaperBackGround.getHeight();
	}
	
	/**
	 * 获取草稿纸内容
	 * @return
	 */
	public Bitmap getPaperBackGround() {
		Canvas mCanvas=new Canvas(mPaperBackGround);
		
		for (int i=0;i<mStrokeList.size();i++) {
			if (mStrokeList.get(i)==null) {
				continue;
			}
			float realStartX=mStrokeList.get(i).startX;
			float realStartY=mStrokeList.get(i).startY;
			float realEndX=mStrokeList.get(i).endX;
			float realEndY=mStrokeList.get(i).endY;
			
			//check if inside of screen
			if ((realStartX>0&&realStartX<getWidth()&&realStartY>0&&realStartY<getHeight())||
					(realEndX>0&&realEndX<getWidth()&&realEndY>0&&realEndY<getHeight())) {
				mPaint.setColor(mStrokeList.get(i).color);
				mPaint.setStrokeWidth(mStrokeList.get(i).strokeWidth);
				mCanvas.drawLine(realStartX, realStartY, realEndX, realEndY, mPaint);
			}
		}
		
		return mPaperBackGround;
	}
	
	/**
	 * 设置桌面与草稿纸
	 * @param paperId
	 * @param deskId
	 */
	public void setPaperAndDesk(int paperId, int deskId) {
		mPaperId=paperId;
		mPaperBackGround = BitmapFactory.decodeResource(getResources(),
				paperId).copy(Bitmap.Config.ARGB_8888, true);
		mPaperBackGround= ImageUtils.drawImageDropShadow(mPaperBackGround, offsetXY);
		
		mPaperWidth=mPaperBackGround.getWidth();
		mPaperHeight=mPaperBackGround.getHeight();
		
		mDeskBackGround=BitmapFactory.decodeResource(getResources(),
				deskId);
	}
	
	/**
	 * 准备书写
	 */
	public void prepareForWrite() {
		LastLocation.set(boundX, boundY);
		LastScale=maxScale;
		mMatrix.reset();
		mMatrix.postScale(LastScale, LastScale);
		mMatrix.postTranslate(LastLocation.x, LastLocation.y);
	}
	
	/**
	 * 初始化纸张位置
	 */
	public void initPaperPosition() {
		float scaleX=1.0f*(getWidth()-2*boundX)/(float)mPaperBackGround.getWidth();
		float scaleY=1.0f*(getHeight()-2*boundY)/(float)mPaperBackGround.getHeight();
		mMatrix.reset();
		if (scaleX<scaleY) {
			mMatrix.postScale(scaleX, scaleX);
			LastLocation.set(boundX, (getHeight()-mPaperBackGround.getHeight()*scaleX)/2);
			mMatrix.postTranslate(LastLocation.x, LastLocation.y);
			LastScale=scaleX;
		} else {
			mMatrix.postScale(scaleY, scaleY);
			LastLocation.set((getWidth()-mPaperBackGround.getWidth()*scaleY)/2, boundY);
			mMatrix.postTranslate(LastLocation.x, LastLocation.y);
			LastScale=scaleY;
		}
		minScale=LastScale;
	}
	
	/**
	 * 滚动屏幕
	 * @param deltaPoint
	 */
	public void scrollTo(PointF deltaPoint) {
		mMatrix.reset();
		mMatrix.postScale(LastScale, LastScale);
		if (!isOutSide(LastLocation, deltaPoint.x, deltaPoint.y)) {
			LastLocation.offset(deltaPoint.x, deltaPoint.y);
			mMatrix.postTranslate(LastLocation.x, LastLocation.y);
		}
	}

	/**
	 * @return the max_undo
	 */
	public int getMax_undo() {
		return max_undo;
	}

	/**
	 * @param max_undo the max_undo to set
	 */
	public void setMax_undo(int max_undo) {
		this.max_undo = max_undo;
	}

	/**
	 * @return the mPaperWidth
	 */
	public int getPaperWidth() {
		return mPaperWidth;
	}

	/**
	 * @return the mPaperHeight
	 */
	public int getPaperHeight() {
		return mPaperHeight;
	}
	
	/**
	 * 保存草稿纸内容
	 * @param canvas
	 */
	public void doDrawForSave(Canvas canvas) {
		//draw paper background
		canvas.drawBitmap(mPaperBackGround, 0, 0, null);
	} 

	/**
	 * @return the mStrokeList
	 */
	public List<DrawStroke> getStrokeList() {
		return mStrokeList;
	}

	/**
	 * @param mStrokeList the mStrokeList to set
	 */
	public void setStrokeList(List<DrawStroke> mStrokeList) {
		this.mStrokeList = mStrokeList;
	}
	
	public void clearStrokeList() {
		this.mStrokeList=new LinkedList<DrawStroke>();
	}

	public void startDraw() {
		if (MainDrawThread==null) {
			//启动主绘制线程
			isRun=true;
			MainDrawThread=new Thread(new MainDrawRunable());
			MainDrawThread.start();
		}
	}
	
	public void stopDraw() {
		isRun = false;
		MainDrawThread=null;
	}

}
