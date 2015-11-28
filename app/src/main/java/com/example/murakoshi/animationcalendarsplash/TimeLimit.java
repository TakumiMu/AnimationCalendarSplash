package com.example.murakoshi.animationcalendarsplash;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by murakoshi on 15/11/09.
 */
public class TimeLimit extends AppCompatActivity implements View.OnClickListener{

    private static MyCountDownTimer myCountDownTimer;
    Date formatDate = null;
    private static long countMillis;
    private long startSec;
    private static final String TAG = "TimeLimit";
    int path;
    ArrayList<String> eventList;
    private NumberPicker np;
    private String eventPath;
    private int notificationId = 0;

    class MyCountDownTimer extends CountDownTimer {

        // 秒を表示するテキストビュー、トグルボタンのビューを取得
        TextView textView = (TextView)findViewById(R.id.timeLimit);

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // カウントダウン処理
        @Override
        public void onTick(long millisUntilFinished) {

            textView.setText(String.valueOf(millisUntilFinished / 1000 / 3600) + ":" +
            String.valueOf(millisUntilFinished / 1000 % 3600 / 60) + ":" + String.valueOf(millisUntilFinished / 1000 % 60)); // ミリ秒→秒に変換して）残り時間を表示

            countMillis = millisUntilFinished; // 残り時間をcountMillisに代入
        }

        // カウントダウン終了後の処理
        @Override
        public void onFinish() {
            //toggleButton.setChecked(false); // toggleボタンをオフにする
            textView.setText("0");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit);

        Button prevEventButton = (Button)findViewById(R.id.btnLeft);
        Button nextEventButton = (Button)findViewById(R.id.btnRight);
        Button alertEventButton = (Button)findViewById(R.id.alert);

        prevEventButton.setOnClickListener(this);
        nextEventButton.setOnClickListener(this);
        alertEventButton.setOnClickListener(this);

        Intent intent = getIntent();
        path = intent.getIntExtra("eventPath",999);

        if(path == 999){
            TextView textView = (TextView)findViewById(R.id.textView3);
            textView.setText(path);
        }

        else{
            eventSet(path);
        }
    }

    @Override
	public void onClick(View v) {
        // TODO 自動生成されたメソッド・スタブ
        switch (v.getId()) {
            case R.id.btnLeft:

                if(path == 0) {
                    //Toast.makeText(getApplicationContext(),  "これ以上過去を振り返らないでください", Toast.LENGTH_SHORT).show();
                    break;
                }
                else{
                    path--;
                    myCountDownTimer.cancel();
                    eventSet(path);
                    break;
                }
            case R.id.btnRight:

                if(path == eventList.size()-1){
                    //Toast.makeText(getApplicationContext(),  "これがあなたの一週間です", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    path++;
                    myCountDownTimer.cancel();
                    eventSet(path);
                    break;
                }


            case R.id.alert:
                showDialog();

            default:
                // 通常のonClick()の時の処理
                break;
        }
    }

    private void showDialog() {
         class MainFragmentDialog extends DialogFragment {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog, null, false);

                np = (NumberPicker) view.findViewById(R.id.numberPicker);
                np.setMinValue(5 / 5 - 1);
                np.setMaxValue((60 / 5) - 1);
                final String[] valueSet = new String[60 / 5];
                for (int i = 5; i <= 60; i += 5) {
                    valueSet[(i / 5) - 1] = String.valueOf(i);
                }
                np.setDisplayedValues(valueSet);
                np.setDescendantFocusability(np.FOCUS_BLOCK_DESCENDANTS);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("何分前にお知らせしますか？");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OKクリック時の処理
                        Intent bootIntent = new Intent(TimeLimit.this, AlarmReceiver.class);
                        String message = eventPath;
                        String messageHead;
                        String messageTail;
                        int index = message.indexOf("~");
                        long alarmTime;

                        message = message.substring(0, index);
                        bootIntent.putExtra("notificationId", notificationId);
                        bootIntent.putExtra("todo", message);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(TimeLimit.this, 0,
                                bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        /* 入力された時間をmsに変換する */
                        alarmTime = ((np.getValue() + 1) * 5) * 60 * 1000;

                        long setTime = System.currentTimeMillis();
                        long alarmStartTime = setTime + alarmTime;

                        if (alarmStartTime < startSec) {
                            alarmStartTime = startSec - alarmTime;
                            alarm.set(
                                    AlarmManager.RTC_WAKEUP,
                                    alarmStartTime,
                                    alarmIntent
                            );
                            Toast.makeText(TimeLimit.this, "通知をセットしました" + alarmTime, Toast.LENGTH_SHORT).show();
                            notificationId++;
                        }
                        else{
                            Toast.makeText(TimeLimit.this, "すでに時刻が経過しています", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.setView(view);
                return builder.create();
            }
        }

        // Dialogの表示
        MainFragmentDialog dialog = new MainFragmentDialog();
        dialog.show(getFragmentManager(), "span_setting_dialog");
    }

    public void eventSet(int position){
        // レイアウトの表示に関する処理

        Intent intent = getIntent();
        eventList = intent.getStringArrayListExtra("eventList");

        eventPath = eventList.get(position);
        int index = eventPath.indexOf("~");
        String testText = eventPath.substring(index - 17, index);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        // Date型変換
        try {
            formatDate = sdf.parse(testText);
        }
        catch (java.text.ParseException e){
            System.out.println("例外が発生しているようです。");
            return;
        }
        catch (Exception e){
            return;
        }

        startSec = formatDate.getTime();
        long start = System.currentTimeMillis();
        countMillis = startSec - start;

        TextView txt = (TextView)findViewById(R.id.first);
        TextView textView = (TextView)findViewById(R.id.textView3);

        Log.i(TAG,"now" + start + "---" + "event" + startSec);
        Log.i(TAG,"立ち位置" + position);
        Log.i(TAG,"イベントの数" + eventList.size());
        Log.d("★", testText);

        myCountDownTimer = new MyCountDownTimer(countMillis, 1000);
        myCountDownTimer.start();

        if(countMillis < 1000*60*60){
            txt.setTextColor(Color.RED);
            myCountDownTimer.textView.setTextColor(Color.RED);
        }
        else{
            txt.setTextColor(Color.rgb(254,255,30));
            myCountDownTimer.textView.setTextColor(Color.rgb(254,255,30));
        }

        textView.setText(eventPath);

    }

}
