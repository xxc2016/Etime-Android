package com.student.xxc.etime.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class GlideCirlceTransHelper extends BitmapTransformation {

    public GlideCirlceTransHelper(Context context)
    {
        super(context);
    }

    protected Bitmap transform(BitmapPool pool,Bitmap toTransform,int outWidth,int outHeight)
    {
         return circleCrop(pool,toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool,Bitmap source)
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



        Bitmap result = pool.get(size,size,Bitmap.Config.ARGB_8888);
        if(result == null)
        {
            result = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
        }

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

    @Override
    public String getId() {
        return getClass().getName();
    }
}
