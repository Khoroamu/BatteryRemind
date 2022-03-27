package com.example.batteryremind;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class MyWorker extends Worker {
    public MyWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        context = appContext;
    }

    Context context;

    public static final CharSequence VERBOSE_NOTIFICATION_CHANNEL_NAME =
            "Verbose WorkManager Notifications";
    public static String VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
            "Shows notifications whenever work starts";
    public static final String CHANNEL_ID = "VERBOSE_NOTIFICATION";

    public static int NOTIFICATION_ID = 233;

    @NonNull
    @Override
    public Result doWork() {
        int currentHour = getCurrentHour();
        Log.d("currentHour", Integer.toString(currentHour));
        if (!(currentHour >= 0 && currentHour <= 8)) {
            makeStatusNotification(getApplicationContext());
            Log.d("Result", "success");
            return Result.success();
        } else {
            Log.d("Result", "failure");
            return Result.failure();
        }
    }

    int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(
                TimeZone.getTimeZone("GMT+8"));
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        return currentHour;
    }

    static void makeStatusNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        Log.d("battery", "scale: " + scale);

        float batteryPct = level * 100 / (float)scale;

        SharedPreferences lastBatteryPct = context.getSharedPreferences("lastBatteryPct", MODE_PRIVATE);
        float historyBatteryPct = lastBatteryPct.getFloat("batteryPct", 0);
        float historyCost = historyBatteryPct - batteryPct;

        SharedPreferences.Editor editor = lastBatteryPct.edit();
        editor.putFloat("batteryPct", batteryPct);
        editor.commit();

        Log.d("battery", "batteryPct: " + batteryPct);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("当前电量为 " + batteryPct)
                .setContentText("历史耗电为 " + historyCost)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }
}

