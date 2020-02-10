package com.example.weatherapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//Class Weather
class Weather extends AsyncTask<String, Void, String>
        //  String: URL String
        //  Void
        //  String: return String
{

    private String weather;
    private String description;
    private String date;
    private String temperature;
    private String wind;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String value) {
        weather = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        description = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String value) {
        date = value;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String value) {
        temperature = value;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String value) {
        wind = value;
    }

    @Override
    protected String doInBackground(String... address) {
        try {
            URL url = new URL(address[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Establish connection with address
            connection.connect();

            // Retrieve data from url
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            // Retrieve data and return it as string
            int data = isr.read();
            String content = "";
            char c;
            while (data != -1) {
                c = (char) data;
                content += c;
                data = isr.read();
            }

            return content;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


