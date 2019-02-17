package com.student.xxc.etime;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.student.xxc.etime.helper.PermissionHelper;
import com.student.xxc.etime.util.ImageUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SetPostActivity extends AppCompatActivity {

    private static final int SELECT_PIC = 544;//选择图片
    private List<Bitmap> bitmapList=new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences=getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night",getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_set_post);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        ImageButton button=(ImageButton)findViewById(R.id.setPic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImageView pic= (ImageView)findViewById(R.id.upPic);
//                pic.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_lock));
                getImage();
            }
        });

        Button upPic=(Button)findViewById(R.id.send);
        upPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void insertPic(){//imagespan图文混合
        EditText et_detail=(EditText)findViewById(R.id.editPost);
        Bitmap bitmap=bitmapList.get(bitmapList.size()-1);
        ImageSpan imageSpan = new ImageSpan(SetPostActivity.this, bitmap);
        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = "[pic:"+(bitmapList.size()-1)+"]";
        SpannableString spannableString = new SpannableString(tempUrl);
        //用ImageSpan对象替换你指定的字符串
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //将选择的图片追加到EditText中光标所在位置
        int index = et_detail.getSelectionStart(); //获取光标所在位置
        Editable edit_text = et_detail.getEditableText();
        if(index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        }
        else
        {
            edit_text.insert(index, spannableString);
        }
        System.out.println("插入的图片：" + spannableString.toString());
    }

    public Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    public void getImage() {
        PermissionHelper.checkPermission(SetPostActivity.this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_PIC) {
            if (data != null) {
                ContentResolver cr = this.getContentResolver();
                Uri selectedImage = data.getData();
                // 获取到图片的路径
                Bitmap originalBitmap = null;
                try {
                    originalBitmap = BitmapFactory.decodeStream(cr.openInputStream(selectedImage));
                    Bitmap bitmap = ImageUtil.resizeImage(originalBitmap);//屏幕比例缩放，质量压缩
                    bitmapList.add(bitmap);
                    insertPic();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }
    }

}
