package com.cader831.ahmed.enther.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import com.cader831.ahmed.enther.AsyncTasks.DownloadCoins;
import com.cader831.ahmed.enther.R;
import com.cader831.ahmed.enther.Utility;

public class FirstTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean coinFileCreated = preferences.getBoolean(Utility.PREFS_COINS_FILE_CREATED, false);
        boolean exchangeFileCreated = preferences.getBoolean(Utility.PREFS_EXCHANGES_FILE_CREATED, false);

        if (!(coinFileCreated && exchangeFileCreated)) {
            setContentView(R.layout.activity_first_time);
            getSupportActionBar().hide();
            new DownloadCoins(this, preferences).execute("https://min-api.cryptocompare.com/data/all/coinlist");
        } else {
            Intent mainActivity = new Intent(this, MainActivity.class);
            this.startActivity(mainActivity);
            this.finish();
        }
    }
}
