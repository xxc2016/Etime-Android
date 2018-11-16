package com.student.xxc.etime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetDateActivity extends AppCompatActivity {
    //查找按钮
    private Button seek;

    private Calendar calendar;
    private String nowDate="";//魔改增加时间变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button showdailog = (Button) findViewById(R.id.showdailog);
        Button time = (Button) findViewById(R.id.time);

        Button seek = (Button)this.findViewById(R.id.seek);//测试使用  可删
        seek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nowDate.equals("")) {
                    Intent intent = new Intent();
                    intent.putExtra("Date", SetDateActivity.this.nowDate);
                    intent.setClass(SetDateActivity.this, MainActivity.class);
                    SetDateActivity.this.setResult(1, intent);//11.14 测试使用  新建主界面
                    SetDateActivity.this.finish();//还是用到回到老界面  使用新界面没有太好的方法
                }
                else{
                    Toast.makeText(SetDateActivity.this,"查询日期  为空",Toast.LENGTH_SHORT).show();
                }
            }
        });


        showdailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDailog();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime();
            }
        });


        calendar = Calendar.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//菜单栏返回键功能
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void showDailog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthofYear, int dayofMonth) {
                String time = String.valueOf(year)+ "-" ;
                if(monthofYear<10)
                {
                    time+="0";
                }
                time+=(monthofYear + 1) + "-" ;
                if(dayofMonth<10)
                {
                    time+="0";
                }
                time+= dayofMonth;
                SetDateActivity.this.nowDate = time;
                //坤坤时间不和规矩  需要YYYY-HH-DD
                TextView day = (TextView) findViewById(R.id.day);
                //显示到文本框里
                day.setText(time);
                //Log.i("SatDateActivity","----------------------"+SetDateActivity.this.nowDate);
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
        //自动弹出键盘问题解决
        datePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    private void showTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("测试", Integer.toString(hourOfDay));
                Log.d("测试", Integer.toString(minute));
                String hour1 = Integer.toString(hourOfDay);
                String min1 = Integer.toString(minute);
                String time1 = hour1 + ":" + min1;
                TextView day_time = (TextView) findViewById(R.id.day_time);
                day_time.setText(time1);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }




}
