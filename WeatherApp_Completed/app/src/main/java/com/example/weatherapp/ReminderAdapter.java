package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ReminderAdapter extends ArrayAdapter {
    public ArrayList<Reminder> reminderArrayList;

    public ReminderAdapter(Context context, int resource, ArrayList<Reminder> reminderArrayList) {
        super(context, resource);
        this.reminderArrayList = reminderArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Reminder reminder = null;
        if (reminderArrayList.size() > 0) {
            reminder = reminderArrayList.get(position);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_list_row, parent, false);
        }
        TextView reminderTextView = convertView.findViewById(R.id.reminderTextView);
        reminderTextView.setText("Date: " + reminder.date.toString() + " Time: " + reminder.time.toString());
        return convertView;
    }

    @Override
    public int getCount() {
        return reminderArrayList.size();
    }
}
