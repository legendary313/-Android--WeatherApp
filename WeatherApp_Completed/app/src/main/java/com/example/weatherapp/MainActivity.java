package com.example.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    String CITY = "Thanh pho Ho Chi Minh, VN";
    String API = "19fbe52295faa788663dafa7fbcd92e0";

    public static String tempService ="";
    public static String desciptionService ="";
    public static String humidityService ="";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            cloudTxt, sunsetTxt, windTxt, pressureTxt, humidityTxt;

    ImageButton share;
    File imagePath;
    String id;

    SearchView locationSearchView;
    Context content = getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
        else {
            setContentView(R.layout.activity_main);


            RelativeLayout kayout = findViewById(R.id.rl);
            kayout.setBackgroundResource(BgImage.getInstance().getImageName());

            share = findViewById(R.id.shareButton);
            addressTxt = findViewById(R.id.address);
            updated_atTxt = findViewById(R.id.updated_at);
            statusTxt = findViewById(R.id.status);
            tempTxt = findViewById(R.id.temp);
            temp_minTxt = findViewById(R.id.temp_min);
            temp_maxTxt = findViewById(R.id.temp_max);
            sunriseTxt = findViewById(R.id.sunrise);
            cloudTxt = findViewById(R.id.cloud);
            sunsetTxt = findViewById(R.id.sunset);
            windTxt = findViewById(R.id.wind);
            pressureTxt = findViewById(R.id.pressure);
            humidityTxt = findViewById(R.id.humidity);
            locationSearchView = findViewById(R.id.simpleSearchView);

            new weatherTask().execute();

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "https://openweathermap.org/city/" + id;
                    Intent intent = new Intent(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, url);

                    boolean facebookAppFound = false;
                    List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                    for (ResolveInfo info : matches) {
                        if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana") ||
                            info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.lite")){
                            intent.setPackage(info.activityInfo.packageName);
                            facebookAppFound = true;
                            break;
                        }
                    }

                    if (facebookAppFound) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Require facebook app", Toast.LENGTH_LONG).show();
                    }


                }
            });

            locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    CITY = s;
                    new weatherTask().execute();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject cloud = jsonObj.getJSONObject("clouds");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Cập nhập : " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                tempService =temp;
                String tempMin = "Nhiệt độ thấp nhất: " + main.getString("temp_min") + "°C";
                String tempMax = "Nhiệt độ cao nhất: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");
                humidityService = humidity;
                id = jsonObj.getString("id");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String cloudClass = cloud.getString("all");
                String weatherDescription = weather.getString("description");
                desciptionService = weatherDescription;

                String address = jsonObj.getString("name") + ", " + sys.getString("country");


                /* Populating extracted data into our views */
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                cloudTxt.setText(cloudClass);
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Không tìm thấy !!!", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.WeatherMap:
                        Intent weatherMapIntent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(weatherMapIntent);
                        return true;
                    case R.id.Comment:
                        Intent SignIn = new Intent(MainActivity.this, SignIn.class);
                        startActivity(SignIn);
                        return true;
                    case R.id.WeatherForecast:
                        Intent WeatherForecast = new Intent(MainActivity.this, WeatherForecast.class);
                        startActivity(WeatherForecast);
                        return true;
                    case R.id.Reminder:
                        Intent Reminder = new Intent(MainActivity.this, ReminderActivity.class);
                        startActivity(Reminder);
                        return true;
                    case R.id.ChangeBackGround:
                        Intent ChangeBackGround = new Intent(MainActivity.this, ChangeBackgroundActivity.class);
                        finish();
                        startActivity(ChangeBackGround);
                        return true;
                    case R.id.FeedBack:
                        String emailSubject = "Feed back to weather app (nhóm 15)";
                        String[] emailReceiverList = {"nhommuoilam@gmail.com"};
                        Intent FeedBack = new Intent(Intent.ACTION_SEND);
                        FeedBack.setType("vnd.android.cursor.dir/email");
                        FeedBack.putExtra(Intent.EXTRA_EMAIL, emailReceiverList);
                        FeedBack.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                        startActivity(FeedBack);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

}
