package com.fieldarea.whon.fieldarea;

import android.graphics.Paint;
import android.graphics.PointF;

import java.text.DecimalFormat;

/**
 * Created by Whon on 2018/6/29.
 * 四边形
 */

public class Shape {


    public PointF lb,rb,lt,rt;
    public PointF heightPoint,downBottomPoint,upBottomPoint,areaPoint;
    public Paint paint;
    public String heightText,downBottomText,upBottomText,areaText;
    public float height,downBottom,upBottom,fieldArea;
    public float[] lines = new float[16];
    public Shape(){
        lb = new PointF(0,0);
        rb = new PointF(0,0);
        lt = new PointF(0,0);
        rt = new PointF(0,0);
        for (int i = 0;i < 16;i ++){
            lines[i] = 0;
        }
    }
    public Shape(PointF lb,PointF rb,PointF lt,PointF rt,float fieldArea,float height,float downBottom,float upBottom){
        this.lb = lb;
        this.rb = rb;
        this.lt = lt;
        this.rt = rt;
        this.fieldArea = fieldArea;
        this.height = height;
        this.downBottom = downBottom;
        this.upBottom = upBottom;
        heightText = Constants.decimalFormat.format(height)+"m";
        downBottomText =  Constants.decimalFormat.format(downBottom)+"m";
        upBottomText =  Constants.decimalFormat.format(upBottom)+"m";
        areaText = Constants.decimalFormat.format(fieldArea/1000*1.5)+"亩";

        setPoint();
    }
    private void setPoint(){
        //下底
        lines[0] = lb.x;
        lines[1] = lb.y;
        lines[2] = rb.x;
        lines[3] = rb.y;
        //高
        lines[4] = lb.x;
        lines[5] = lb.y;
        lines[6] = lt.x;
        lines[7] = lt.y;
        //上底
        lines[8] = lt.x;
        lines[9] = lt.y;
        lines[10] = rt.x;
        lines[11] = rt.y;
        //斜边
        lines[12] = rt.x;
        lines[13] = rt.y;
        lines[14] = rb.x;
        lines[15] = rb.y;
    }
    public void setPaint(int color,int strokeWidth){
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setTextSize(20);
    }
    public void setTextPoint(float[] lines,boolean out){
        float textLenght;
        if(out) {
            textLenght = paint.measureText(heightText);
            heightPoint = new PointF(lines[0] - textLenght - 10, (lines[1] + lines[7]) / 2);
            textLenght = paint.measureText(downBottomText);
            downBottomPoint = new PointF( (lines[0] + lines[2] - textLenght) / 2, lines[1] + 30);
            textLenght = paint.measureText(upBottomText);
            upBottomPoint = new PointF((lines[6] + lines[10] - textLenght) / 2, lines[7] - 10);
            textLenght = paint.measureText(areaText);
            areaPoint = new PointF((lines[6]+lines[2]-textLenght)/2,(lines[7]+lines[3])/2);
        }else {
            heightPoint = new PointF(lines[0] + 10,  (lines[1] + lines[7]) / 2);
            textLenght = paint.measureText(downBottomText);
            downBottomPoint = new PointF( (lines[0] + lines[2] - textLenght) / 2,  lines[1] - 10);
            textLenght = paint.measureText(upBottomText);
            upBottomPoint = new PointF( (lines[6] + lines[10] - textLenght) / 2,  lines[7] + 30);
            textLenght = paint.measureText(areaText);
            areaPoint = new PointF((lines[6]+lines[2]-textLenght)/2,(lines[7]+lines[3])/2);
        }
    }
}
