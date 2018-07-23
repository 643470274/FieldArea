package com.fieldarea.whon.fieldarea;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Whon on 2018/6/28.
 */

public class FieldView extends View{

    private static final String TAG = FieldView.class.getSimpleName();

    private PointF                      lbTemp;
    private PointF                      canvasPoint;
    private ArrayList<Shape>            shapes;
    private Stack<Float>                heightPXs;

    private int                         drawCount = 0;
    private int                         height = 0;
    private int                         width = 0;
    private int                         color = 0;
    private int                         firstPointerID,secondPointerID;
    private float                       firstPointerX,firstPointerY;
    private float                       sScale = 1,scale=1,sd = 1,ld = 1,centerX = 0,centerY = 0;
    private float                       sx=0,sy=0,dx=0,dy=0;
    private float                       offset = 0;
    private float                       fieldHeight = 0;
    private float                       fieldDownBottom = 0;
    private float                       fieldUpBottom = 0;
    private float                       fieldArea = 0;
    private float                       downBottomTemp;
    public FieldView(Context context) {
        super(context);
        init();
    }

    public FieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        shapes = new ArrayList<Shape>();
        heightPXs = new Stack<Float>();
        canvasPoint = new PointF(0,0);
    }

    public void setParams(float fieldHeight,float fieldDownBottom,float fieldUpBottom,float fieldArea){
        this.fieldHeight = fieldHeight;
        this.fieldDownBottom = fieldDownBottom;
        this.fieldUpBottom = fieldUpBottom;
        this.fieldArea = fieldArea;
        postInvalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(sx+dx,sy+dy);
        Log.d(TAG, "onDraw: drawCount:"+drawCount);
        height = getHeight();
        width = getWidth();
        if(drawCount == 0)createShape(Color.GREEN,fieldHeight,fieldDownBottom,fieldUpBottom,fieldArea);
        color = getResources().getColor(R.color.fieldColor);
        canvas.drawColor(color);
        int i = 0;
        for(Shape shape:shapes){
            float[] lines = scaleShape(sScale*scale,shape.lines);
            canvas.drawLines(lines,shape.paint);
            shape.setTextPoint(lines,i == 0? true:false);
            canvas.drawText(shape.heightText,shape.heightPoint.x,shape.heightPoint.y,shape.paint);
            canvas.drawText(shape.downBottomText,shape.downBottomPoint.x,shape.downBottomPoint.y,shape.paint);
            if(shape.lt.x != shape.rt.x)
            canvas.drawText(shape.upBottomText,shape.upBottomPoint.x,shape.upBottomPoint.y,shape.paint);
            if(i!=0)
                canvas.drawText(shape.areaText,shape.areaPoint.x,shape.areaPoint.y,shape.paint);
            i++;
        }
        if(drawCount < 10)
        drawCount++;
    }
    public void createShape(int color,float fieldHeight,float fieldDownBottom,float fieldUpBottom,float fieldArea){
        Shape shape;
        PointF lb,rb,lt,rt;
        float height,downBottom,upBottom;
        if(drawCount == 0) {
            if (fieldDownBottom >= fieldHeight || fieldUpBottom >= fieldHeight) {
                if (fieldDownBottom >= fieldUpBottom) {
                    downBottom = 0.7f * this.width;
                    height = downBottom * fieldHeight / fieldDownBottom;
                    upBottom = downBottom * fieldUpBottom / fieldDownBottom;
                } else {
                    upBottom = 0.7f * this.width;
                    height = upBottom * fieldHeight / fieldUpBottom;
                    downBottom = upBottom * fieldDownBottom / fieldUpBottom;
                }
                lbTemp = new PointF( 0.15f * this.width,  0.5f * (this.height + height));
            } else {
                height = 0.7f * this.height;
                downBottom = height * fieldDownBottom / fieldHeight;
                upBottom = height * fieldUpBottom / fieldHeight;
                if (fieldDownBottom >= fieldUpBottom) {
                    lbTemp = new PointF( 0.5f * (this.width - downBottom),  0.85f * this.height);
                } else {
                    lbTemp = new PointF( 0.5f * (this.width - upBottom),  0.85f * this.height);
                }
            }
            downBottomTemp = downBottom;
        }else {
            height = downBottomTemp*fieldHeight/fieldDownBottom;
            downBottom = downBottomTemp;
            upBottom = downBottomTemp*fieldUpBottom/fieldDownBottom;
            downBottomTemp = upBottom;
        }
        lb = new PointF(lbTemp.x,lbTemp.y -  offset);
        rb = new PointF(lb.x+downBottom,lb.y);
        lt = new PointF(lb.x,lb.y-height);
        rt = new PointF(lt.x+upBottom,lt.y);
        shape = new Shape(lb,rb,lt,rt,fieldArea,fieldHeight,fieldDownBottom,fieldUpBottom);
        shape.setPaint(color,4);
        shapes.add(shape);
        if(drawCount != 0){
            offset += heightPXs.push(height);
        }
    }
    public Shape deleteShape(){
        Shape shape;
        if(shapes.size() > 1){
            shape = shapes.get(shapes.size()-1);
            shapes.remove(shapes.size()-1);
            downBottomTemp = shape.downBottom*heightPXs.peek()/shape.height;
            offset -= heightPXs.pop();
            postInvalidate();
            return shape;
        }else {
            return null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                final float x = event.getX();
                final float y = event.getY();
                firstPointerX = x;
                firstPointerY = y;
                firstPointerID = event.getPointerId(0);
                Log.d(TAG, "dispatchTouchEvent: ACTION_DOWN");
                break;}
            case MotionEvent.ACTION_MOVE:{
                if(event.getPointerCount()>=2){
                    final float firstX = event.getX();
                    final float firstY = event.getY();
                    final float secondX = event.getX(1);
                    final float secondY = event.getY(1);
                    ld = (float) Math.sqrt((firstX - secondX)*(firstX - secondX)+(firstY - secondY)*(firstY - secondY));
                    scale = ld/sd;
                    dx = (1 - scale)*centerX;
                    dy = (1 - scale)*centerY;
                    postInvalidate();
                    Log.d(TAG, "dispatchTouchEvent: TWO_ACTION_MOVE");
                }else {
                    dx = event.getX() - firstPointerX;
                    dy = event.getY() - firstPointerY;
                    postInvalidate();
                    Log.d(TAG, "dispatchTouchEvent: ONE_ACTION_MOVE");
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                resetDistance();
                postInvalidate();
                Log.d(TAG, "dispatchTouchEvent: ACTION_UP sx:"+sx+" sy:"+sy);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                if(event.getPointerCount() <= 2) {
                    final float firstX = event.getX();
                    final float firstY = event.getY();
                    final float secondX = event.getX(1);
                    final float secondY = event.getY(1);
                    centerX = (firstX + secondX)/2-sx;
                    centerY = (firstY + secondY)/2-sy;
                    sd = (float) Math.sqrt((firstX - secondX)*(firstX - secondX)+(firstY - secondY)*(firstY - secondY));
                    secondPointerID = event.getPointerId(1);
                    resetDistance();
                }
                Log.d(TAG, "dispatchTouchEvent:");
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                if(event.getPointerCount() <= 2){
                    if(secondPointerID == event.getPointerId(event.getActionIndex())) {
                        firstPointerX = event.getX();
                        firstPointerY = event.getY();
                    }else if(firstPointerID == event.getPointerId(event.getActionIndex())){
                        firstPointerX = event.getX(event.findPointerIndex(secondPointerID));
                        firstPointerY = event.getY(event.findPointerIndex(secondPointerID));
                    }
                    sScale = sScale * scale;
                    scale = 1;
                    resetDistance();
                    Log.d(TAG, "dispatchTouchEvent: ACTION_POINTER_UP ");
                }
                break;
            }
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }
    private void resetDistance(){
        sx = sx+dx;
        sy = sy+dy;
        dx = 0;
        dy = 0;
    }
    private float[] scaleShape(float scale,float[] lines){
        float[] linesScaled = new float[16];
        for (int i = 0;i<16;i++){
            linesScaled[i] = scale*lines[i];
        }
        return linesScaled;
    }
}
