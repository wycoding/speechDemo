package com.example.speechdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_yuyin;
    private Button btn_wenben;
    private EditText mResultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=58d8b422");

        FlowerCollector.onResume(this);
        FlowerCollector.onPause(this);

        btn_yuyin = (Button) findViewById(R.id.btn_yuyin);
        btn_wenben = (Button) findViewById(R.id.btn_wenben);
        mResultText = ((EditText) findViewById(R.id.result));

        btn_yuyin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        btnVoice();
    }
    private void btnVoice() {
        //1.创建RecognizerDialog对象
        RecognizerDialog dialog = new RecognizerDialog(this,null);

        //2.设置accent、language等参数
        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");

        //3.设置回调接口
        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
            }
            @Override
            public void onError(SpeechError speechError) {
            }
        });

        //4.显示dialog，接收语音输入
        dialog.show();
        Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();
    }

    private void printResult(RecognizerResult recognizerResult) {
        String text = parseIatResult(recognizerResult.getResultString());
        // 自动填写地址
        mResultText.append(text);
    }


    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

}
