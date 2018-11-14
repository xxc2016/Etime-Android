package com.student.xxc.etime;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;

public class SetTraceActivity extends Activity {

    static  final  int requestCode =0x000011;
    int traceId;
    Button button_confirm;
    EditText time;
    EditText event;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_trace);
        Intent intent = getIntent();
        traceId = intent.getIntExtra("traceId",0);
        button_confirm = this.findViewById(R.id.button_confirm);
        time=(EditText)this.findViewById(R.id.editText_time);
        time.setInputType(InputType.TYPE_NULL);
        event=(EditText)this.findViewById(R.id.editText_activity);
        initial();
    }


    private void initial()
    {

        time.setText(getIntent().getStringExtra("time"));
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime();
            }
        });
        event.setText(getIntent().getStringExtra("event"));


        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("time",((EditText)SetTraceActivity.this.findViewById(R.id.editText_time)).getText().toString());
                bundle.putString("event",((EditText)SetTraceActivity.this.findViewById(R.id.editText_activity)).getText().toString());
                bundle.putBoolean("finish",((Switch)SetTraceActivity.this.findViewById(R.id.switch_isfinish)).isChecked());
                bundle.putBoolean("isdel",((Switch)SetTraceActivity.this.findViewById(R.id.switch_delete)).isChecked());
                bundle.putInt("traceId",traceId);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(SetTraceActivity.this, MainActivity.class);
                SetTraceActivity.this.setResult(1,intent);
                finish();
            }
        });
    }

    private void showTime() {
        Calendar calendar;
        calendar=Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour1;
                String min1;
                if(hourOfDay<10)
                    hour1= '0'+Integer.toString(hourOfDay);
                else
                    hour1= Integer.toString(hourOfDay);
                if(minute<10)
                    min1= '0'+Integer.toString(minute);
                else
                    min1= Integer.toString(minute);
                String time1 = hour1 + ":" + min1;
                time.setText(time1);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

}
