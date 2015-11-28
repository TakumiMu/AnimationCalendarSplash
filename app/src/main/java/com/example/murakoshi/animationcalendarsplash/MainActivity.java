package com.example.murakoshi.animationcalendarsplash;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "MainActivity";
    private String name = null;
    private int eventCount = 0;
    private long startMem = 0;
    private ProgressBar progress;
    private ListView list;
    private int MAX_ITEM ;
    private CustomAdapter mAdapter;
    int REQUEST_CODE;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(R.id.listView);
        progress = new ProgressBar(this);


        // リストアイテムのラベルを格納するArrayListをインスタンス化
        ArrayList<String> labelList = new ArrayList<String>();

        //外部ファイルからカレンダーのアカウント名を取得
        SharedPreferences data_account = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        name = data_account.getString("AccountName", null);

        if(!TextUtils.isEmpty(name)) {
            Log.d("bbbbb", name);
            mAdapter = new CustomAdapter(this, 0, new ArrayList<String>());
            getEvent();
            list.addFooterView(progress);
            // リストにAdapterをセット
            list.setAdapter(mAdapter);
            // リストアイテムの間の区切り線を非表示にする
            //list.setDivider(null);

            // リスナ登録
            list.setOnScrollListener(new OnScrollListener() {
                boolean isloading = false;
                boolean isAllLoad = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, final int firstVisibleItem,
                        final int visibleItemCount,
                        final int totalItemCount) {
                    // ロード中の場合は新たにロードしない
                    if (isloading) {
                        return;
                    }
                    // 全て読み込んだら新たにロードしない
                    if (isAllLoad) {
                        return;
                    }

                    // (item総数 - 表示されているitem総数) =  表示されているitemのindex
                    //  で最下部の検知
                    if ((totalItemCount - visibleItemCount) == firstVisibleItem) {
                        isloading = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // DB読み込みとかitemを作成する時間.
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {

                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 全て読み込んだらプログレスバーを削除
                                        Log.i("TAG", "listSize" + list.getCount());
                                        if (MAX_ITEM <= list.getCount()) {

                                            list.removeFooterView(progress);
                                            isAllLoad = true;
                                        }
                                        else{
                                            Log.i("TAG", "GETEVENTTTTT");
                                            getEvent();

                                            // list.invalidateViews();


                                        }
                                    }
                                });

                                isloading = false;
                            }
                        }).start();
                    }
                }
            });

        }
        else {
            // "List Item + ??"を20個リストに追加
            for (int i = 1; i <= 20; i++) {
                labelList.add("List Item " + i);
            }
            // Adapterのインスタンス化
            // 第三引数にlabelListを渡す
            CustomAdapter mAdapter = new CustomAdapter(this, 0, labelList);
            // リストにAdapterをセット
            list.setAdapter(mAdapter);
            // リストアイテムの間の区切り線を非表示にする
            //list.setDivider(null);
        }

        list.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent = AccountManager.newChooseAccountIntent(null, null, new String[]{
                                "com.google"
                        }, null,
                        null, null, null);
            } else {
                intent = AccountManager.newChooseAccountIntent(null, null, new String[]{
                                "com.google"
                        }, false, null,
                        null, null, null);
            }

            Log.d("aaaaa", "hosi");
            startActivityForResult(intent, REQUEST_CODE);

            /*Intent intent = new Intent(this, SettingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);*/
        }
        else if(id == R.id.action_refresh){
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();

            overridePendingTransition(0, 0);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data1) {
        super.onActivityResult(requestCode, resultCode, data1);
        {
            Log.d("aaaaa", "hosi");
            if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

                String accountName = data1.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                Toast.makeText(this, accountName, Toast.LENGTH_SHORT).show();

                SharedPreferences data_account = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = data_account.edit();
                editor.putString("AccountName", accountName);
                editor.apply();

                String name = data_account.getString("AccountName", null);
                Log.d("aaaaa", name);

                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();

                overridePendingTransition(0, 0);
                startActivity(intent);

            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //==== キーコード判定 ====//
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //-==- Backキー -==-//
            // 以降の処理をキャンセルする。
            //
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
        Intent intent = new Intent(this, TimeLimit.class);
        /*String selectedEventPath = event.get(position);
        intent.putExtra("eventPath", selectedEventPath);*/
        intent.putExtra("eventList", mAdapter.getList());
        intent.putExtra("eventPath", position);
        startActivity(intent);
	}

    public void getEvent(){

        long start;
        if(startMem == 0) {
            start = System.currentTimeMillis();
        }
        else{
            start = startMem;
        }

        long end = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7 ;

        ContentResolver cr = getContentResolver();
        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
        };

        String selection = "(" +
                "(" + CalendarContract.Events.ACCOUNT_NAME + " = ?) AND " +
                "(" + CalendarContract.Events.ACCOUNT_TYPE + " = ?) AND " +
                "(" + CalendarContract.Events.DTSTART + " >= ?) AND " +
                "(" + CalendarContract.Events.DTEND  + " <= ?) AND" +
                "(" + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " = ?)" +
        ")";


        String[] selectionArgs = new String[]{name, "com.google", Long.toString(start), Long.toString(end), "700"};
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, CalendarContract.Events.DTSTART);
        MAX_ITEM =cursor.getCount();
        for (boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext()) {


            long startSec = 0;

            if(eventCount < 10) {

                long eventID = cursor.getLong(0);
                String title = cursor.getString(1);
                startSec = cursor.getLong(2);
                long endSec = cursor.getLong(3);

                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN);
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm", Locale.JAPAN);

                Log.i(TAG, eventID + ":" + title);
                Log.i(TAG, format.format(startSec) + " - " + format1.format(endSec));
                Log.i(TAG, startSec + "-" + endSec + ":" + cursor.getCount());
                Log.i(TAG, "-----------------------------------");
                Log.d("★", "Cursor");

                mAdapter.add(title + "\n" + format.format(startSec) + " ~ " + format1.format(endSec));
                mAdapter.notifyDataSetChanged();
                eventCount++;
            }

            else{
                startSec = cursor.getLong(2);
                eventCount = 0;
                startMem = startSec;
                break;
            }
        }
        cursor.close();

    }

}