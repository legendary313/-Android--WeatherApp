package com.example.weatherapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class Coordinate extends AsyncTask<String, Void, String>
        //  String: URL String
        //  Void
        //  String: return String
{

    private String longitude;
    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String value) {
        longitude = value;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String value) {
        latitude = value;
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
