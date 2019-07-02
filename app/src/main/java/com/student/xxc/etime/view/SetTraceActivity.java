package com.student.xxc.etime.view;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import com.student.xxc.etime.entity.Trace;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.student.xxc.etime.R;
import com.student.xxc.etime.helper.MapTimeHelper;
import com.student.xxc.etime.util.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SetTraceActivity extends AppCompatActivity {

    static  final  int requestCode =0x000011;
    private static final int PLACE_SELECT = 0x000022;
    //int traceId;
    Trace trace;//建立Trace对象用于接受细节信息
    Button button_confirm;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String , String>();
    private static final String TAG = MainActivity.class .getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_trace);
        initStyle();

        Intent intent = getIntent();
        initialTraceByIntent(intent);//通过Intent信息初始化Trace 6.30
        initial();//Trace初始化界面

        checkRecordPermission();//语音模块初始化
        initView() ;
        initSpeech() ;

        initialPlace();
    }

    private void initialPlace() {
        final EditText place=findViewById(R.id.editText_predict2);
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MapListActivity.class);
                startActivityForResult(intent, MapListActivity.REQUEST_SUC);
            }
        });
    }


    private  void initStyle()
    {
        SharedPreferences preferences=getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night",getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MapListActivity.REQUEST_SUC && resultCode==MapListActivity.RESULT_CODE_INPUTTIPS){
            EditText place =findViewById(R.id.editText_predict2);
            String poiName=data.getStringExtra("poiName") ;
            String poiId=data.getStringExtra("poiId");
            Log.i("poiId",poiId);
            place.setText(poiName);
            trace.setSiteId(poiId);
            trace.setSiteText(poiName);
//            Toast.makeText(getApplicationContext(),poiId,Toast.LENGTH_SHORT);
        }
    }

    private void checkRecordPermission() {
        int result = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},0);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }{
                Toast.makeText(getApplicationContext(), "权限未同意,无法下载", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void initView() {
        FloatingActionButton btn_startspeech = (FloatingActionButton) findViewById(R.id.btn_startspeech);
        btn_startspeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechDialog();
            }
        }) ;
    }

    private void initSpeech() {

        SpeechUtility. createUtility( this, SpeechConstant. APPID + "=58a6bd74" );
    }

    private void initialTraceByIntent(Intent intent) {
        int traceId = intent.getIntExtra("traceId", 0);
        String time = intent.getStringExtra("time");
        String event = intent.getStringExtra("event");
        String date = intent.getStringExtra("date");
        boolean hasESTime = intent.getBooleanExtra("hasESTime", false);
        boolean hasLETime = intent.getBooleanExtra("hasLETime", false);
        String ESTime = intent.getStringExtra("ESTime");
        String LETime = intent.getStringExtra("LETime");
        boolean isfinish = intent.getBooleanExtra("isfinish", false);
        String siteId = intent.getStringExtra("siteId");
        String siteText = intent.getStringExtra("siteText");
        int predict = intent.getIntExtra("predict", 30);


        this.trace = new Trace(traceId, time, event, date, hasESTime, hasLETime,
                ESTime, LETime, isfinish, siteId, siteText, predict);

    }

    private  void initial()
    {
        button_confirm = this.findViewById(R.id.button_confirm);
        final EditText time=(EditText)this.findViewById(R.id.editText_time);
        time.setInputType(InputType.TYPE_NULL);
        EditText event=(EditText)this.findViewById(R.id.editText_activity);

        Switch switch_finish = (Switch) this.findViewById(R.id.switch_isfinish);
        switch_finish.setChecked(trace.getFinish());
        final EditText editText_predict =  (EditText)this.findViewById(R.id.editText_predict);
        editText_predict.setText(""+trace.getPredict());

        CheckBox checkBox_hasESTime = this.findViewById(R.id.checkBox_hasESTime);//最早时间  最晚时间初始化
        final EditText editText_ESTime = this.findViewById(R.id.editText_ESTime);
        checkBox_hasESTime.setChecked(trace.isHasESTime());
        editText_ESTime.setInputType(InputType.TYPE_NULL);
        editText_ESTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime(editText_ESTime);
            }
        });
        if(trace.isHasESTime())
        {
            editText_ESTime.setText(trace.getESTime());
            editText_ESTime.setVisibility(View.VISIBLE);
        }
        else
        {
            editText_ESTime.setVisibility(View.INVISIBLE);
        }

        checkBox_hasESTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//添加checkBox监听事件
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                trace.setHasESTime(isChecked);
                if(isChecked)
                {
                    editText_ESTime.setVisibility(View.VISIBLE);
                }
                else
                {
                    editText_ESTime.setVisibility(View.INVISIBLE);
                }
            }
        });

        CheckBox checkBox_hasLETime = this.findViewById(R.id.checkBox_hasLETime);
        checkBox_hasLETime.setChecked(trace.isHasLETime());
        final EditText editText_LETime = this.findViewById(R.id.editText_LETime);
        editText_LETime.setInputType(InputType.TYPE_NULL);
        editText_LETime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime(editText_LETime);
            }
        });
        if(trace.isHasLETime())
        {
            editText_LETime.setText(trace.getLETime());
            editText_LETime.setVisibility(View.VISIBLE);
        }
        else
        {
            editText_LETime.setVisibility(View.INVISIBLE);
        }
        checkBox_hasLETime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                trace.setHasLETime(isChecked);
                if(isChecked)
                {
                    editText_LETime.setVisibility(View.VISIBLE);
                }
                else
                {
                    editText_LETime.setVisibility(View.INVISIBLE);
                }
            }
        });



        if(trace.getFinish()){
            CardView cardView=(CardView)findViewById(R.id.cardTrace);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorFinish));
        }

        EditText editText_site = this.findViewById(R.id.editText_predict2);//地点初始化
        if(trace.hasSite())
        {
            editText_site.setText(trace.getSiteText());
        }
        else
        {
            editText_site.setText("未选定");
        }

        Switch switch_delete = (Switch)this.findViewById(R.id.switch_isdelete);
        switch_delete.setChecked(false);//日程设定不删除


        time.setText(trace.getTime());
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime(time);
            }
        });
        event.setText(trace.getEvent());


        button_confirm.setOnClickListener(new View.OnClickListener() { //提交
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("time",((EditText)SetTraceActivity.this.findViewById(R.id.editText_time)).getText().toString());
                bundle.putString("event",((EditText)SetTraceActivity.this.findViewById(R.id.editText_activity)).getText().toString());
                bundle.putBoolean("finish",((Switch)SetTraceActivity.this.findViewById(R.id.switch_isfinish)).isChecked());
                bundle.putBoolean("isdel",((Switch)SetTraceActivity.this.findViewById(R.id.switch_isdelete)).isChecked());

                boolean hasESTime = ((CheckBox)SetTraceActivity.this.findViewById(R.id.checkBox_hasESTime)).isChecked();
                bundle.putBoolean("hasESTime",hasESTime);
                if(hasESTime)
                {
                    bundle.putString("ESTime",((EditText)SetTraceActivity.this.findViewById(R.id.editText_ESTime)).getText().toString());
                }

                boolean hasLETime = ((CheckBox)SetTraceActivity.this.findViewById(R.id.checkBox_hasLETime)).isChecked();
                bundle.putBoolean("hasLETime",hasLETime);
                if(hasLETime)
                {
                    bundle.putString("LETime",((EditText)SetTraceActivity.this.findViewById(R.id.editText_LETime)).getText().toString());
                }

                bundle.putInt("predict",Integer.parseInt(((EditText)SetTraceActivity.this.findViewById(R.id.editText_predict)).getText().toString()));
                bundle.putString("date",trace.getDate());

                if(trace.hasSite()) {//如果有地点参数
                    bundle.putString("siteId", trace.getSiteId());
                    bundle.putString("siteText", trace.getSiteText());
                }

                bundle.putInt("traceId",trace.getTraceId());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(SetTraceActivity.this, MainActivity.class);
                SetTraceActivity.this.setResult(1,intent);
                finish();
            }
        });
    }

    private void showTime(final EditText editText) {//修改变得通用
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
                editText.setText(time1);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /////////////////////////////////////////////////////////////
    private void startSpeechDialog() {
        //1. 创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener()) ;
        //2. 设置accent、 language等参数
        mDialog.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mDialog.setParameter(SpeechConstant. ACCENT, "mandarin" );
        // 若要将UI控件用于语义理解，必须添加以下参数设置，设置之后 onResult回调返回将是语义理解
        // 结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener( new MyRecognizerDialogListener()) ;
        //4. 显示dialog，接收语音输入
        mDialog.show() ;
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * @param results
         * @param isLast  是否说完了
         */
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            if(isLast)return ;
            String result = results.getResultString(); //为解析的
            showTip(result) ;
            System. out.println(" 没有解析的 :" + result);

            String text = JsonParser.parseIatResult(result) ;//解析过后的
            System. out.println(" 解析后的 :" + text);

            String sn = null;
            // 读取json结果中的 sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString()) ;
                sn = resultJson.optString("sn" );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults .put(sn, text) ;//没有得到一句，添加到

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults .get(key));
            }

//            event.setText(resultBuffer.toString());// 设置输入框的文本
            Log.e("sta",resultBuffer.toString());
            EditText event = SetTraceActivity.this.findViewById(R.id.editText_activity);
            event.append(resultBuffer.toString());
            event .setSelection(event.length()) ;//把光标定位末尾
        }

        //语音输入出错
        @Override
        public void onError(SpeechError speechError) {

        }
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败 ");
            }

        }
    }

    /**
     * 语音识别
     */
    private void startSpeech() {
        //1. 创建SpeechRecognizer对象，第二个参数： 本地识别时传 InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer( this, null); //语音识别器
        //2. 设置听写参数，详见《 MSC Reference Manual》 SpeechConstant类
        mIat.setParameter(SpeechConstant. DOMAIN, "iat" );// 短信和日常用语： iat (默认)
        mIat.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mIat.setParameter(SpeechConstant. ACCENT, "mandarin" );// 设置普通话
        //3. 开始听写
        //mIat.startListening(mRecoListener);
    }

    //////////////////////////////////////////////////////////
//    // 听写监听器
//    private RecognizerListener mRecoListener = new RecognizerListener() {
//        // 听写结果回调接口 (返回Json 格式结果，用户可参见附录 13.1)；
////一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
////关于解析Json的代码可参见 Demo中JsonParser 类；
////isLast等于true 时会话结束。
//        public void onResult(RecognizerResult results, boolean isLast) {
//            Log.e (TAG, results.getResultString());
//            System.out.println(results.getResultString()) ;
//            showTip(results.getResultString()) ;
//        }
//
//        // 会话发生错误回调接口
//        public void onError(SpeechError error) {
//            showTip(error.getPlainDescription(true)) ;
//            // 获取错误码描述
//            Log. e(TAG, "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
//        }
//
//        // 开始录音
//        public void onBeginOfSpeech() {
//            showTip(" 开始录音 ");
//        }
//
//        //volume 音量值0~30， data音频数据
//        public void onVolumeChanged(int volume, byte[] data) {
//            showTip(" 声音改变了 ");
//        }
//
//        // 结束录音
//        public void onEndOfSpeech() {
//            showTip(" 结束录音 ");
//        }
//
//        // 扩展用接口
//        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
//        }
//    };
//
    private void showTip (String data) {


    }
}
