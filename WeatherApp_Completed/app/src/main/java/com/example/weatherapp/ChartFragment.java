package com.example.weatherapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class ChartFragment extends Fragment {
    MainActivity main;
    Context context = null;
    Button btnShow;
    FragmentTransaction ft;
    int countButtonPress = 0;

    LineChart tempChart;
    LineChart windChart;
    LineChart popChart;
    LineChart cloudChart;

    ArrayList<Float> tempArr;
    ArrayList<Float> windArr;
    ArrayList<Float> popArr;
    ArrayList<Float> cloudArr;
    Fragment fragment;

    public ChartFragment() {

    }

    public static ChartFragment newInstance(String strArg) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        btnShow = (Button) view.findViewById(R.id.btnShowChart);

        tempChart = view.findViewById(R.id.chart1);
        tempChart.setTouchEnabled(true);
        tempChart.setPinchZoom(true);

        windChart = view.findViewById(R.id.chart2);
        windChart.setTouchEnabled(true);
        windChart.setPinchZoom(true);

        popChart = view.findViewById(R.id.chart3);
        popChart.setTouchEnabled(true);
        popChart.setPinchZoom(true);

        cloudChart = view.findViewById(R.id.chart4);
        cloudChart.setTouchEnabled(true);
        cloudChart.setPinchZoom(true);


        String temperature;
        String precipProbability;
        String windSpeed;
        String cloudCover;

        tempArr = new ArrayList<Float>();
        windArr = new ArrayList<Float>();
        popArr = new ArrayList<Float>();
        cloudArr = new ArrayList<Float>();

        for(int i = 0; i < 49; i++)
        {
            Bundle b =  getArguments().getBundle(String.valueOf(i));
            temperature = b.getString("temperature");
            precipProbability = b.getString("PoP");
            windSpeed = b.getString("windSpeed");
            cloudCover = b.getString("cloudCover");


            tempArr.add(Float.parseFloat(temperature));
            windArr.add(Float.parseFloat(windSpeed));
            popArr.add(Float.parseFloat(precipProbability));
            cloudArr.add(Float.parseFloat(cloudCover));
        }

        for(int i = 0; i < 48; i++) {
            Log.i("asd", String.valueOf(tempArr.get(i)));
        }


        renderData(tempChart, tempArr, "temperature");
        renderData(windChart, windArr, "wind");
        renderData(popChart, popArr, "pop");
        renderData(cloudChart, cloudArr, "cloud");


        btnShow.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Fragment fragment = getActivity().getFragmentManager().findFragmentByTag("chart-fragment-tag" );
                                            ft = getActivity().getFragmentManager().beginTransaction();
                                           ft.hide(fragment);
                                           ft.commit();
                                           countButtonPress++;
                                           if (countButtonPress % 2 == 0) {

                                               if (fragment != null) {
                                                   getFragmentManager().beginTransaction().remove(fragment).commit();
                                               }
                                           }
                                       }
                                   }
        );

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void renderData(LineChart chart, ArrayList<Float> tempArr, String type) {
        float maxY = Collections.max(tempArr);
        int maxX = tempArr.size();

        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(maxX);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum(maxY);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        chart.getAxisRight().setEnabled(false);
        setChartData(chart, tempArr, type);
    }

    public void setChartData(LineChart chart, ArrayList<Float> dataArr, String type)
    {

        ArrayList<Entry> values = new ArrayList<>();

        for(int i = 0; i < dataArr.size(); i++)
        {
            values.add(new Entry(i, dataArr.get(i)));
        }

        int color = 0;
        int lineColor = 0;
        int circleColor = 0;
        String description = "";
        circleColor = Color.BLACK;

        if(type == "temperature")
        {
            color = R.drawable.fade_red;
            lineColor = Color.RED;
            description = "Nhiệt độ";
        }

        if(type == "pop")
        {
            color = R.drawable.fade_blue;
            lineColor = Color.BLUE;
            description = "Khả năng mưa";
        }

        if(type == "wind")
        {
            color = R.drawable.fade_gray;
            lineColor = Color.GRAY;
            description = "Tốc độ gió";
        }

        if(type == "cloud")
        {
            color = R.drawable.fade_purple;
            lineColor = Color.parseColor("#C331EA");
            description = "Mây che phủ";
        }


        LineDataSet set;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set.setValues(values);

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        }
        else {
            set = new LineDataSet(values, description);
            set.setDrawIcons(false);
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set.setColor(lineColor);
            set.setCircleColor(circleColor);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setDrawCircleHole(false);
            set.setValueTextSize(0f);
            set.setDrawFilled(true);
            set.setFormLineWidth(1f);
            set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set.setFormSize(15.f);
            set.setDrawCircles(false);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(getActivity(), color);
                set.setFillDrawable(drawable);

            }
            else {
                set.setFillColor(Color.RED);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }


}
