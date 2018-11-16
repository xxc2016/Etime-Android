package com.student.xxc.etime.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class SelectIconHelper {
    public static Bitmap toRoundBitmap(Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int r=0;
        //取最短边做边长
        if(width<height){
            r=width;
        }else{
            r=height;
        }
        //构建一个bitmap
        Bitmap backgroundBm= Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas=new Canvas(backgroundBm);
        Paint p=new Paint();
        //设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect=new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }
    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    public static void showInputDialog(final TextView username, final Context context) {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(context);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(context);
        inputDialog.setTitle("输入您的昵称").setView(editText);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user_name=editText.getText().toString();
                if(user_name.equals(""))
                    return;
                username.setText(user_name);
                SharedPreferences sharedPreferences = context.getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name", user_name);
                editor.apply();
            }
        }).show();
    }

    public static void setIcon(ImageView imageView,String imagePath){
        Bitmap bm=BitmapFactory.decodeFile(imagePath);
        Matrix matrix = new Matrix();
        int h = bm.getHeight();
        int w = bm.getWidth();

        float h1 = (float) 3500 / h;
        float w1 = (float) 3500 / w;
        if (h > w) {
            matrix.postScale(w1, w1);
        } else {
            matrix.postScale(h1, h1);
        }

        Bitmap bm2 = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
//        Log.i("bm", "_____" + bm2);
        bm = toRoundBitmap(bm2);
        imageView.setImageBitmap(bm);
    }
}
