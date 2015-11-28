package com.example.murakoshi.animationcalendarsplash;

/**
 * Created by murakoshi on 15/11/04.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 2000);
    }

    class splashHandler implements Runnable {
        public void run() {
            SharedPreferences data_account = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
            String name = data_account.getString("AccountName", null);
            //カレンダーのユーザー情報有無の確認
            if(!TextUtils.isEmpty(name)) {
                Log.d("bbbbb", name);
                Intent inte = new Intent(getApplication(), MainActivity.class);
                startActivity(inte);
                SplashActivity.this.finish();
            }
            else{
                Intent inte = new Intent(getApplication(), StartActivity.class);
                startActivity(inte);
                SplashActivity.this.finish();

            }
        }
    }
}
