package com.example.batteryremind;

import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

//    WorkRequest MyWorkRequest;
    Context myContext;
    PeriodicWorkRequest MyWorkRequest;

    int cnt = 0;
    UUID id;

    ListenableWorker info;

//    TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String str;




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
                WorkManager
                        .getInstance(getApplicationContext())
                        .cancelUniqueWork("battery remind");
                Toast.makeText(MainActivity.this,"已取消任务",Toast.LENGTH_LONG).show();
            }
        });
    }
}