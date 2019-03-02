package com.student.xxc.etime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserHomePageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        TextView textView=findViewById(R.id.homepage);
        Intent intent = getIntent();
        String account = intent.getStringExtra("account_data");
        textView.setText(account);


    }


}
