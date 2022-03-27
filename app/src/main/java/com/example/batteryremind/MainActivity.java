package com.example.batteryremind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingPeriodicWorkPolicy;

import androidx.work.PeriodicWorkRequest;

import androidx.work.WorkManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ConfirmDialogFragment.NoticeDialogListener {
    PeriodicWorkRequest MyWorkRequest;

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        WorkManager
                .getInstance(getApplicationContext())
                .cancelUniqueWork("battery remind");
        Toast.makeText(MainActivity.this,"已取消任务",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "进程被销毁");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        SharedPreferences lastBatteryPct = getSharedPreferences("lastBatteryPct", MODE_PRIVATE);
        SharedPreferences.Editor editor = lastBatteryPct.edit();
        editor.putFloat("batteryPct", batteryPct);
        editor.commit();

        Button button=(Button)findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                MyWorkRequest =
                        new PeriodicWorkRequest.Builder(MyWorker.class,
                                1, TimeUnit.HOURS,
                                5, TimeUnit.MINUTES)
                                .build();

                WorkManager
                        .getInstance(getApplicationContext())
                        .enqueueUniquePeriodicWork(
                                "battery remind",
                                ExistingPeriodicWorkPolicy.KEEP,
                                MyWorkRequest);

                Toast.makeText(MainActivity.this,"已开始任务",Toast.LENGTH_LONG).show();
            }
        });

        Button button4= (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConfirmDialogFragment dialog = new ConfirmDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                dialog.show(fm, "what");
            }
        });
    }
}