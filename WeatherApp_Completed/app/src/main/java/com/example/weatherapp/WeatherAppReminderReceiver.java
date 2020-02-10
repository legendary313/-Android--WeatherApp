package com.example.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherAppReminderReceiver extends BroadcastReceiver {
//
//    String CITY = "Thanh pho Ho Chi Minh, VN";
//    String API = "19fbe52295faa788663dafa7fbcd92e0";
//
//    String temp="";
//    String weatherDescription="";
//    String address="";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, WeatherAppReminderService.class);
//        new weatherTask().execute();
//        intent.putExtra("temp",temp);
//        intent.putExtra("weatherDescription",weatherDescription);
//        intent.putExtra("address",address);
        context.startService(intent1);
    }

//    class weatherTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        protected String doInBackground(String... args) {
//            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//
//            try {
//                JSONObject jsonObj = new JSONObject(result);
//                JSONObject main = jsonObj.getJSONObject("main");
//                JSONObject sys = jsonObj.getJSONObject("sys");
//                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
//
//                temp = main.getString("temp") + "°C";
//                weatherDescription = weather.getString("description");
//                address = jsonObj.getString("name") + ", " + sys.getString("country");
//
//            } catch (JSONException e) {
//            }
//
//        }
//    }
}
