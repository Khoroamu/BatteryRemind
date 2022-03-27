package com.example.batteryremind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

//import androidx.work.testing.TestDriver;
//import androidx.work.testing.WorkManagerTestInitHelper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements StartGameDialogFragment.NoticeDialogListener {

//    WorkRequest MyWorkRequest;
    Context myContext;
    PeriodicWorkRequest MyWorkRequest;

    int cnt = 0;
    UUID id;

    ListenableWorker info;

//    TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver();


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

        String str;

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        SharedPreferences lastBatteryPct = getSharedPreferences("lastBatteryPct", MODE_PRIVATE);
        SharedPreferences.Editor editor = lastBatteryPct.edit();
        editor.putFloat("batteryPct", batteryPct);
        editor.commit();

        myContext = getApplicationContext();

        Button button=(Button)findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                MyWorkRequest =
                        new PeriodicWorkRequest.Builder(MyWorker.class,
//                                1, TimeUnit.HOURS,
                                15, TimeUnit.MINUTES,
                                5, TimeUnit.MINUTES)
//                                .addTag("cleanup" + cnt)
                                .build();

                id = MyWorkRequest.getId();
                Log.d("id", "id"+id);

                cnt++;

//                TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver();

//                info = (ListenableWorker) WorkManager
//                        .getInstance(myContext)
//                        .getWorkInfosForUniqueWork("battery remind");
//
//                if (info.isStopped()) {
//
//                }

                WorkManager
                        .getInstance(myContext)
//                        .enqueue(MyWorkRequest);
                        .enqueueUniquePeriodicWork(
                                "battery remind",
                                ExistingPeriodicWorkPolicy.KEEP,
                                MyWorkRequest);

                Toast.makeText(MainActivity.this,"已开始任务",Toast.LENGTH_LONG).show();

//                WorkInfo.State.isFinished()
            }
        });



        Button button4=(Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                StartGameDialogFragment dialog = new StartGameDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                dialog.show(fm, "what");
//                WorkManager
//                        .getInstance(getApplicationContext())
//                        .cancelUniqueWork("battery remind");
//                Toast.makeText(MainActivity.this,"已取消任务",Toast.LENGTH_LONG).show();
            }
        });
    }
}