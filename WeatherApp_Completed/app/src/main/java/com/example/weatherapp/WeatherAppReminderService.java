package com.example.weatherapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherAppReminderService extends IntentService {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "WEATHER_APP_REMINDER_CHANNEL_ID";

    String temp="";
    String weatherDescription="";
    String address="";

    public WeatherAppReminderService() {
        super("Empty");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context =this;
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);

        builder.setContentTitle(MainActivity.desciptionService.toUpperCase());
        builder.setContentText("Nhiệt độ: " + MainActivity.tempService + "          "
                                + "Độ ẩm: " + MainActivity.humidityService);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Weather App Reminder Channel";
            String description = "Alo Alo !!!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(NOTIFICATION_ID, builder.build());

//        NotificationManager notificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification();
//
//        Intent notificationIntent = new Intent(context, HomeActivity.class);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent intent = PendingIntent.getActivity(context, 0,
//                notificationIntent, 0);
//
//        notification.setLatestEventInfo(context, title, message, intent);
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(0, notification);

        }
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId)
//    {
//        temp = intent.getStringExtra("temp");
//        weatherDescription = intent.getStringExtra("weatherDescription");
//        address = intent.getStringExtra("address");
//        return START_STICKY;
//    }
}
