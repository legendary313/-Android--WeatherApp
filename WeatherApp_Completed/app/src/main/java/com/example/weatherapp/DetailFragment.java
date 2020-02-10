package com.example.weatherapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class DetailFragment extends Fragment {
    MainActivity main;
    Context context = null;
    Button btnShow;
    FragmentTransaction ft;
    int countButtonPress = 0;

    public DetailFragment() {

    }


    public static DetailFragment newInstance(String strArg) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnShow = (Button) view.findViewById(R.id.btnShow);
        TextView tvWeather = (TextView)view.findViewById(R.id.tvWeatherFm);
        TextView tvPoP = (TextView)view.findViewById(R.id.tvPrecipProbabilityFm);
        TextView tvDate = (TextView)view.findViewById(R.id.tvDateFm);
        TextView tvTempMin = (TextView)view.findViewById(R.id.tvTeperatureMinFm);
        TextView tvTempMax = (TextView)view.findViewById(R.id.tvTeperatureMaxFm);
        TextView tvHumidity = (TextView)view.findViewById(R.id.tvHumidityFm);
        TextView tvWindSpeed= (TextView)view.findViewById(R.id.tvWindSpeedFm);
        TextView tvWindGust = (TextView)view.findViewById(R.id.tvWindGustFm);
        TextView tvAirPressure = (TextView)view.findViewById(R.id.tvAirPressureFm);
        TextView tvVisibility = (TextView)view.findViewById(R.id.tvVisibilityFm);
        TextView tvOzoneDensity = (TextView)view.findViewById(R.id.tvOzoneDensityFm);
        TextView tvUvIndex= (TextView)view.findViewById(R.id.tvUvIndexFm);
        TextView tvCloudCover = (TextView)view.findViewById(R.id.tvCloudCoverFm);


        final String fragmentIndex = getArguments().getString("fragmentTag");
        String weather = getArguments().getString("weather");
        String date = getArguments().getString("date");
        String temperatureMin = getArguments().getString("tempMin");;
        String temperatureMax = getArguments().getString("tempMax");
        String humidity = getArguments().getString("humidity");
        String windSpeed = getArguments().getString("windSpeed");
        String windGust = getArguments().getString("winGust");
        String airPressure = getArguments().getString("airPressure");
        String visibility = getArguments().getString("visibility");
        String ozoneDensity = getArguments().getString("ozoneDensity");
        String uvIndex = getArguments().getString("uvIndex");
        String cloudCover = getArguments().getString("cloudCover");
        String precipProbability = getArguments().getString("PoP");

        tvWeather.setText(weather);
        tvPoP.setText(precipProbability);
        tvDate.setText(date);
        tvTempMax.setText(temperatureMax);
        tvTempMin.setText(temperatureMin);
        tvHumidity.setText(humidity);
        tvWindSpeed.setText(windSpeed);
        tvWindGust.setText(windGust);
        tvAirPressure.setText(airPressure);
        tvVisibility.setText(visibility);
        tvOzoneDensity.setText(ozoneDensity);
        tvUvIndex.setText(uvIndex);
        tvCloudCover.setText(cloudCover);


        btnShow.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Fragment fragment = getActivity().getFragmentManager().findFragmentByTag("detail-fragment-tag-"+fragmentIndex);
                                           Log.i("fragment-tag", fragmentIndex);
                                           ft = getActivity().getFragmentManager().beginTransaction();
                                           ft.hide(fragment);
                                           ft.commit();
                                           countButtonPress++;
                                           if(fragment.isHidden())
                                           {
                                               Log.i("hidden", "hidden");
                                           }
                                           else
                                           {
                                               Log.i("visible", "visible");
                                           }
                                           if(countButtonPress%2 == 0)
                                           {
                                               Fragment fm = getFragmentManager().findFragmentByTag("detail-fragment-tag-"+fragmentIndex);
                                               if(fm != null) {
                                                   getFragmentManager().beginTransaction().remove(fm).commit();
                                               }
                                           }
                                       }
                                   }
        );

    }
}
