package com.student.xxc.etime.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class BitmapTranslateHelper {//Bitmap变换工具类

    static public Bitmap zoomImage(Bitmap original, double newWidth, double newHeight)//缩放bitmap
    {
        float width = original.getWidth();
        float height = original.getHeight();
        Matrix matrix =new Matrix();
        float scaleWidth = ((float)newWidth)/width;
        float scaleHeight = ((float)newHeight)/height;

        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(original,0,0,(int)width,(int)height,matrix,true);
        return bitmap;
    }

    static public Bitmap circleCrop(Bitmap source)//裁圆bitmap
    {
        if(source == null)  return null;

        Log.i("glide","h:"+source.getHeight()+"w:"+source.getWidth());

        int size = Math.min(source.getWidth(),source.getHeight());
        int x= (source.getWidth()-size)/2;
        int y =(source.getHeight()-size)/2;

        Bitmap squared  = Bitmap.createBitmap(source,x,y,size,size);//从长方形截成正方形

//        int newSize = 100;
//        float ratio =  ((float) newSize)/size;
//        Matrix matrix =new Matrix();
//        matrix.preScale(ratio,ratio);
//        Bitmap squared = Bitmap.createBitmap(squared_1,0,0,size,size,matrix,false);

        Bitmap result = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(result);//画布
        Paint paint = new Paint();

        paint.setShader(new BitmapShader(squared,BitmapShader.TileMode.CLAMP,BitmapShader.TileMode.CLAMP));

        paint.setAntiAlias(true);//抗锯齿
//        float r =size/2f;
//
//        canvas.drawCircle(r,r,r,paint);

        RectF rectF = new RectF(0f,0f,size,size);
        canvas.drawRoundRect(rectF,size/2,size/2,paint);
        return  result;
    }
}
