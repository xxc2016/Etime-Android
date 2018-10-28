package com.student.xxc.etime.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.student.xxc.etime.MainActivity;
import com.student.xxc.etime.R;

public class SetTraceActivity extends Activity {

    static  final  int requestCode =0x000011;
    int traceId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trace_setter);
        Intent intent = getIntent();
        traceId = intent.getIntExtra("traceId",0);
        initial();
    }


    private void initial()
    {
        Button button_confirm = this.findViewById(R.id.button_confirm);
        ((EditText)this.findViewById(R.id.editText_time)).setText(getIntent().getStringExtra("time"));
        ((EditText)this.findViewById(R.id.editText_activity)).setText(getIntent().getStringExtra("event"));
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

}
