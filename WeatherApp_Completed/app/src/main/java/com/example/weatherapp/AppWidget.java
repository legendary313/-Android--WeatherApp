package com.example.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.InetAddress;


public class AppWidget extends AppWidgetProvider {
    // To identify the widget views click
    private static final String TEMPERATURE_CLICKED = "Temperature";
    private static final String HUMIDITY_CLICKED = "Humidity";

    // Construct the url to fetch weather JSON data from web
    private String mCity = "Thanh pho Ho Chi Minh";
    private String mCountry = "VN";
    // mURLString = "http://api.openweathermap.org/data/2.5/weather?q=khulna,bd&APPID=YourAppID";
    private String mURLRoot = "https://api.openweathermap.org/data/2.5/weather?q=";
    private String mAppID = "&units=metric&appid=19fbe52295faa788663dafa7fbcd92e0";
    private String mURLString = mURLRoot+mCity+","+mCountry+mAppID;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        /*
            AppWidgetManager
                Updates AppWidget state; gets information about installed AppWidget
                providers and other AppWidget related state.

            ComponentName
                Identifier for a specific application component (Activity, Service, BroadcastReceiver,
                or ContentProvider) that is available. Two pieces of information, encapsulated here,
                are required to identify a component: the package (a String) it exists in, and the
                class (a String) name inside of that package.

            RemoteViews
                A class that describes a view hierarchy that can be displayed in another process.
                The hierarchy is inflated from a layout resource file, and this class provides some
                basic operations for modifying the content of the inflated hierarchy.
        */
        AppWidgetManager appWidgetManager1= appWidgetManager;
        ComponentName watchWidget = new ComponentName(context, AppWidget.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        Intent intent = new Intent(context, AppWidget.class);
        /*
            PendingIntent
                A description of an Intent and target action to perform with it. Instances of this
                class are created with getActivity(Context, int, Intent, int),
                getActivities(Context, int, Intent[], int), getBroadcast(Context, int, Intent, int),
                and getService(Context, int, Intent, int); the returned object can be handed to other
                applications so that they can perform the action you described on your behalf at a later time.
        */
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tv_temperature, pendingIntent);

        remoteViews.setOnClickPendingIntent(
                R.id.tv_temperature,
                getPendingSelfIntent(context, TEMPERATURE_CLICKED)
        );

        remoteViews.setOnClickPendingIntent(
                R.id.tv_humidity,
                getPendingSelfIntent(context, HUMIDITY_CLICKED)
        );
        appWidgetManager1.updateAppWidget(watchWidget, remoteViews);
    }

    // Catch the click on widget views
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        //Toast.makeText(context, "Intent Received: "+action, Toast.LENGTH_SHORT).show();
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Allow the network operation on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager= AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        ComponentName watchWidget = new ComponentName(context, AppWidget.class);

        Toast.makeText(context, "Requested", Toast.LENGTH_SHORT).show();

        // Check the internet connection availability
        if(isInternetConnected()){
            Toast.makeText(context, "Fetching Data", Toast.LENGTH_SHORT).show();
            // Update the widget weather data
            // Execute the AsyncTask
            new ProcessJSONData(appWidgetManager,watchWidget,remoteViews).execute(mURLString);
            remoteViews.setInt(R.id.tv_temperature, "setBackgroundResource", R.drawable.bg_green);
        }else {
            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            remoteViews.setInt(R.id.tv_temperature, "setBackgroundResource", R.drawable.bg_red);
        }

        // If the temperature text clicked
        if (TEMPERATURE_CLICKED.equals(intent.getAction())) {
            // Do something
        }

        // If the humidity text clicked
        if(HUMIDITY_CLICKED.equals(intent.getAction())){
            // Do something
        }

        // Apply the changes
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    // AsyncTask to fetch, process and display weather data
    private class ProcessJSONData extends AsyncTask<String, Void, String> {
        private AppWidgetManager appWidgetManager;
        private ComponentName watchWidget;
        private RemoteViews remoteViews;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        public ProcessJSONData(AppWidgetManager appWidgetManager, ComponentName watchWidget, RemoteViews remoteViews){
            // Do something
            this.appWidgetManager = appWidgetManager;
            this.watchWidget = watchWidget;
            this.remoteViews = remoteViews;
        }

        @Override
        protected String doInBackground(String... strings){
            String stream;
            String urlString = strings[0];

            // Get jason data from web
            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute(String stream){
            super.onPostExecute(stream);

            if(stream !=null){
                try{
                    // Process JSON data
                    // Get the full HTTP Data as JSONObject
                    JSONObject reader= new JSONObject(stream);
                    // Get the JSONObject "coord"...........................
                    JSONObject coord = reader.getJSONObject("main");
                    // Get the value of key "temp" under JSONObject "main"
                    String temperature = coord.getString("temp");
                    // Get the value of key "humidity" under JSONObject "main"
                    String humidity = coord.getString("humidity");
                    String city = reader.getString("name");
                    double celsius = Math.round((getCelsiusFromKelvin(temperature)+273.15)*10)/10;
                    temperature ="" + celsius + " " + (char) 0x00B0+"C";
                    Log.d("Temp",temperature);

                    // Display weather data on widget
                    remoteViews.setTextViewText(R.id.tv_temperature, temperature);
                    remoteViews.setTextViewText(R.id.tv_humidity, "H: " + humidity + " %");

                    // Apply the changes
                    appWidgetManager.updateAppWidget(watchWidget, remoteViews);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        } // onPostExecute() end
    } // ProcessJSON class end

    // Method to get celsius value from kelvin
    public Double getCelsiusFromKelvin(String kelvinString){
        Double kelvin = Double.parseDouble(kelvinString);
        Double numberToMinus = 273.15;
        Double celsius = kelvin - numberToMinus;
        // Rounding up the double value
        // Each zero (0) return 1 more precision
        // Precision means number of digits after dot
        celsius = (double)Math.round(celsius * 10) / 10;
        return celsius;
    }

    // Custom method to check internet connection
    public Boolean isInternetConnected(){
        boolean status = false;
        try{
            InetAddress address = InetAddress.getByName("google.com");

            if(address!=null)
            {
                status = true;
            }
        }catch (Exception e) // Catch the exception
        {
            e.printStackTrace();
        }
        return status;
    }
}
