package com.example.murakoshi.animationcalendarsplash;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by murakoshi on 15/11/05.
 */
public class SettingActivity extends AppCompatActivity {

    int REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button deleteAccountButton = (Button)findViewById(R.id.button_res);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {

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

            }
        });

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
            Intent intent2 = new Intent(this, MainActivity.class);
            startActivityForResult(intent2, 0);
        }
        return super.onKeyDown(keyCode, event);
    }


}
