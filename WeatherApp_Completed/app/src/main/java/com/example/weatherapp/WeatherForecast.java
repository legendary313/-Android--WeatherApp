package com.example.weatherapp;

import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.round;



public class WeatherForecast extends ListActivity {
    Context context = this;

    FragmentTransaction ft;
    FragmentTransaction ft2;
    DetailFragment detailFragment;
    ChartFragment chartFragment;

    ListView list;
    EditText etCityName;
    Button btnChart;

    ArrayList<String> weathers;
    ArrayList<String> dates;
    ArrayList<String> times;
    ArrayList<String> descriptions;
    ArrayList<String> temperatures;
    ArrayList<String> winds;
    Integer[] images;
    //ArrayList<String> fragmentData;
    Bundle bundleFm;
    ArrayList<Bundle> bundleDailyArr;
    Bundle bundleHourlyArr;
    Bundle bundleData;
    Coordinate coord;
    ArrayList<String> location;
    //boolean isShown = true;
    LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherforecast);

        // get Latitude longitude
        etCityName = ((EditText)findViewById(R.id.etCity));
        btnChart = (Button)findViewById(R.id.btnChart);

        btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getFragmentManager().beginTransaction().add(R.id.main_holder,new LineFragment()).commit();
//                ft2 = getFragmentManager().beginTransaction();
//                lineChart = new LineChartActivity();
//                lineChart.setArguments(bundle2);
//                ft2.add(R.id.main_holder, lineFragment, "line-fragment-tag");
//                ft2.show(lineFragment);
//                ft2.commit();
//                Log.i("a", "b");

                ft2 = getFragmentManager().beginTransaction();
                chartFragment = ChartFragment.newInstance("chart-fragment");
                chartFragment.setArguments(bundleHourlyArr);
                ft2.add(R.id.main_holder, chartFragment, "chart-fragment-tag");
                ft2.show(chartFragment);
                ft2.commit();
            }
        });

//        btnChart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent myIntent = new Intent(MainActivity.this, LineChartActivity.class);
//                myIntent.putExtra("key", "123"); //Optional parameters
//                startActivity(myIntent);
//            }
//        });

        etCityName.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                Thread t = new Thread(){
                                    public void run(){
                                        try {
                                            coord = new Coordinate();
                                            String city = etCityName.getText().toString();
                                            location = getCoordinate(city);
                                            if(location != null) {
                                                Log.i("LONG", location.get(0));
                                                Log.i("LAT", location.get(1));
                                                String longtitude = location.get(0);
                                                String latitude = location.get(1);
                                                getWeatherProcess(longtitude, latitude);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        updateListView();
                                                    }
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if(location == null)
                                        {
                                            // Can't find the location of city
                                            Log.i("error", "Can't find the location of city");
                                            cancel();
                                        }
                                    }

                                    public void cancel() {
                                        interrupt();
                                    }
                                };
                                t.start();
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );


        //                              Saigon
        getWeatherProcess("106.660172","10.762622");

        updateListView();

        list = (ListView) findViewById(android.R.id.list);

        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ft = getFragmentManager().beginTransaction();
                detailFragment = DetailFragment.newInstance("detail-fragment");
                detailFragment.setArguments(bundleDailyArr.get(position));

                ft.add(R.id.main_holder, detailFragment, "detail-fragment-tag-"+position);
                ft.show(detailFragment);
                ft.commit();
            }
        });
    }


    public ArrayList<Weather> getDarkSkyWeather(String longitude, String latitude) {
        // Get weather in the next 7 days

        Log.i("long: ", longitude);
        Log.i("lat: ", latitude);

        Weather weather = new Weather();
        ArrayList<Weather> weatherArr = new ArrayList<Weather>();
        ArrayList<String> temp = new ArrayList<String>();
        String content;
        try {
            content = weather.execute("https://api.darksky.net/forecast/a4b289aa3abfbee48b4fc1df98208a34/"+ latitude + "," + longitude + "?lang=vi&exclude=minutely,flags,currently").get();

            if(content == null)
            {
                return null;
            }
            JSONObject obj = new JSONObject(content);

            JSONObject daily = obj.getJSONObject("daily");
            JSONArray dataDaily = daily.getJSONArray("data");

            String summary;
            String temperatureMin;
            String temperatureMax;
            String humidity;
            String windSpeed;
            String windGust;
            String airPressure;
            String visibility;
            String ozoneDensity;
            String uvIndex;
            String cloudCover;
            String precipProbability;
            String time;

            Bundle bundle2 = new Bundle();
            images = new Integer[dataDaily.length()];

            for(int i = 0; i < dataDaily.length(); i++)
            {
                Weather result = new Weather();

                JSONObject detail = dataDaily.getJSONObject(i);

                summary = detail.getString("summary");
                temperatureMin = detail.getString("temperatureMin");
                temperatureMax = detail.getString("temperatureMax");
                precipProbability = detail.getString("precipProbability");
                humidity = detail.getString("humidity");
                windSpeed = detail.getString("windSpeed");
                windGust = detail.getString("windGust");
                airPressure = detail.getString("pressure");
                visibility = detail.getString("visibility");
                ozoneDensity = detail.getString("ozone");
                uvIndex = detail.getString("uvIndex");
                cloudCover = detail.getString("cloudCover");
                time = unixTimeToDate(detail.getString("time"));


                String precipProb = String.valueOf(String.format("%.0f", (Float.parseFloat(precipProbability)*100))+ "%");

                // Update UI
                result.setDate(normalizeDate(time.substring(0, 10)));
                result.setWeather(summary);
                result.setDescription("Khả năng mưa: " + precipProb);
                result.setTemperature(celsiusToFahrenheit(temperatureMax) + "\u2103");
                result.setWind("Gió: " + windSpeed + "mph");
                weatherArr.add(result);

                if(summary.toLowerCase().contains("quang"))
                {
                    images[i] = R.drawable.sunny;
                }
                if(summary.toLowerCase().contains("mưa"))
                {
                    images[i] = R.drawable.rainy;
                }
                else if (summary.toLowerCase().contains("âm u"))
                {
                    images[i] = R.drawable.foggy;
                }
                else if (summary.toLowerCase().contains("mây"))
                {
                    images[i] = R.drawable.cloudy;
                }
                else
                {
                    images[i] = R.drawable.sunny;
                }


//                Bundle bundlee = new Bundle();
//                ArrayList<String> dailyData = new ArrayList<String>();
//
//                dailyData.add(summary);
//                dailyData.add(precipProb);
//                dailyData.add(normalizeDate(time.substring(0, 10)));
//                dailyData.add(temperatureMin +"\u2103");
//                dailyData.add(temperatureMax +"\u2103");
//                dailyData.add(humidity + "%");
//                dailyData.add(windSpeed + " mph");
//                dailyData.add(windGust + " mph");
//                dailyData.add(airPressure + " mb");
//                dailyData.add(visibility + " mi");
//                dailyData.add(ozoneDensity + " DU");
//                dailyData.add(uvIndex);
//                dailyData.add(cloudCover);
//                dailyData.add(String.valueOf(i));   // fragment-tag
//
//                bundlee.putStringArrayList("daily-data",dailyData);


                Bundle bundle = new Bundle();
                bundle.putString("weather", summary);
                bundle.putString("PoP", precipProb);
                bundle.putString("date", normalizeDate(time.substring(0, 10)));
                bundle.putString("tempMin", temperatureMin +"\u2103");
                bundle.putString("tempMax", temperatureMax +"\u2103");
                bundle.putString("humidity", humidity + "%");
                bundle.putString("windSpeed", windSpeed + " mph");
                bundle.putString("winGust", windGust + " mph");
                bundle.putString("airPressure", airPressure + " mb");
                bundle.putString("visibility", visibility + " mi");
                bundle.putString("ozoneDensity", ozoneDensity + " DU");
                bundle.putString("uvIndex", uvIndex);
                bundle.putString("cloudCover", cloudCover);
                bundle.putString("fragmentTag", String.valueOf(i));

                temp.add(temperatureMin);

                bundleDailyArr.add(bundle);
//                bundleDailyArr.add(bundlee);

//                Log.i("Index: ", String.valueOf(i));
//                Log.i("summary :",  summary);
//                Log.i("temperatureMin :",  temperatureMin);
//                Log.i("temperatureMax :",  temperatureMax);
//                Log.i("humidity :",  humidity);
//                Log.i("windSpeed :",  windSpeed);
//                Log.i("winGust :",  windGust);
//                Log.i("airPressure :",  airPressure);
//                Log.i("visibility :",  visibility);
//                Log.i("ozoneDensity :",  ozoneDensity);
//                Log.i("uvIndex :",  uvIndex);
//                Log.i("cloudCover :",  cloudCover);
//                Log.i("cloudCover :",  "\n");
//                Log.i("precipProbability :",  precipProbability);
            }

            for(int i = 0; i < temp.size(); i++)
            {
                bundle2.putString("temp"+String.valueOf(i), temp.get(i));
            }



//            Get weather hourly

            JSONObject hourly = obj.getJSONObject("hourly");
            JSONArray dataHourly = hourly.getJSONArray("data");
            String temperature;

            for(int i = 0; i < dataHourly.length(); i++)
            {
                JSONObject detail = dataHourly.getJSONObject(i);

                temperature = detail.getString("temperature");
                precipProbability = detail.getString("precipProbability");
                windSpeed = detail.getString("windSpeed");
                cloudCover = detail.getString("cloudCover");

                Bundle bundle = new Bundle();
                bundle.putString("PoP", precipProbability);
                //bundle.putString("temp", String.valueOf((int)(Float.parseFloat(temperatureMin) + Float.parseFloat(temperatureMax) / 2)));
                bundle.putString("temperature", temperature);
                bundle.putString("windSpeed", windSpeed);
                bundle.putString("cloudCover", cloudCover);
                bundle.putString("fragmentTagasd", String.valueOf(i));


                bundleHourlyArr.putBundle(String.valueOf(i),bundle);


                Log.i("Hourly Index: ", String.valueOf(i));
//                Log.i("summary :",  summary);
//                Log.i("temperatureMin :",  temperatureMin);
//                Log.i("temperatureMax :",  temperatureMax);
//                Log.i("humidity :",  humidity);
//                Log.i("windSpeed :",  windSpeed);
//                Log.i("winGust :",  windGust);
//                Log.i("airPressure :",  airPressure);
//                Log.i("visibility :",  visibility);
//                Log.i("ozoneDensity :",  ozoneDensity);
//                Log.i("uvIndex :",  uvIndex);
//                Log.i("cloudCover :",  cloudCover);
//                Log.i("cloudCover :",  "\n");
//                Log.i("precipProbability :",  precipProbability);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherArr;
    }

    public void getWeatherProcess(String longitude, String latitude)
    {
        weathers = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        dates = new ArrayList<String>();
        times = new ArrayList<String>();
        temperatures = new ArrayList<String>();
        winds = new ArrayList<String>();

        bundleFm = new Bundle();
        bundleData = new Bundle();
        bundleDailyArr = new ArrayList<Bundle>();
        bundleHourlyArr = new Bundle();


        // get daily weather
        ArrayList<Weather> s = getDarkSkyWeather(longitude,latitude);

        if(s == null)
        {
            return;
        }
        for (int i = 0; i < s.size(); i++) {
            dates.add(s.get(i).getDate());
            weathers.add(s.get(i).getWeather());
            descriptions.add(s.get(i).getDescription());
            temperatures.add(s.get(i).getTemperature());
            winds.add(s.get(i).getWind());
        }
    }

    public void updateListView()
    {

        CustomRowAdapter adapter = new CustomRowAdapter(
                this,
                R.layout.custom_row, weathers, dates, descriptions, temperatures, winds, images
        );

        setListAdapter(adapter);
    }

    public String unixTimeToDate(String unixSeconds)
    {
        //long unixSeconds = 1372339860;
        // convert seconds to milliseconds
        Date date = new java.util.Date(Long.parseLong(unixSeconds)*1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);

        return formattedDate;
    }

    public String celsiusToFahrenheit(String celsius)
    {
        int fahrenheit = round(Float.parseFloat(celsius) - 32)*5/9;
        return String.valueOf((fahrenheit));
    }

    public String normalizeDate(String d) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        String inputDateStr = d;
        Date date = inputFormat.parse(inputDateStr);
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

    public ArrayList<String> getCoordinate(String city) throws JSONException {
        String content;
        Coordinate c = null;
        ArrayList<String> t = null;
        try {
            content = coord.execute("https://api.opencagedata.com/geocode/v1/json?q="+ city + "&key=d114013aac5f4f48927a4daa37dce0fb&language=vi&pretty=1").get();

            if(content == null)
            {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Can't find the location of the city", Toast.LENGTH_LONG).show();

                    }
                });

                return t;
            }
            t = new ArrayList<String>();
            JSONObject obj = new JSONObject(content);

            JSONArray resultArr = obj.getJSONArray("results");
            JSONObject bounds = resultArr.getJSONObject(0).getJSONObject("bounds");
            JSONObject northeast = bounds.getJSONObject("northeast");

            String lng;
            String lat;
            lng = northeast.getString("lng");
            lat = northeast.getString("lat");

            t.add(lng);
            t.add(lat);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return t;
    }

}







