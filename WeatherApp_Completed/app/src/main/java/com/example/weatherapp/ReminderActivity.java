package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ReminderActivity extends AppCompatActivity {
    private static final int NOTIFICATION_REMINDER_NIGHT = 2;
    public ArrayList<Reminder> remindersArrayList;
    public ReminderAdapter remindersAdapter;
    public ListView remindersListView;

    private int selectedIndex = -1;
    private SharedPreferences remindersPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        RelativeLayout kayout=findViewById(R.id.rl);
        kayout.setBackgroundResource(BgImage.getInstance().getImageName());
        remindersArrayList = new ArrayList<>();
        remindersAdapter = new ReminderAdapter(this, R.layout.reminder_list_row, remindersArrayList);

        remindersPreferences = getApplicationContext().getSharedPreferences("WEATHER_APP_REMINDER_PREFERENCES", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = remindersPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String stringSavedReminder = (String) entry.getValue();

            String[] StrTokens = stringSavedReminder.split("-");
            int[] intTokens = new int[StrTokens.length];
            for (int i = 0; i < intTokens.length; i++)
            {
                intTokens[i] = Integer.parseInt(StrTokens[i]);
            }

            remindersArrayList.add(new Reminder(intTokens[3], intTokens[4], 0, intTokens[2], intTokens[1], intTokens[0]));
        }

        Button newBtn = findViewById(R.id.newBtn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedIndex = -1;
                Intent intent = new Intent(ReminderActivity.this, NewReminderActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        remindersListView = findViewById(R.id.remindersListView);
        remindersListView.setAdapter(remindersAdapter);
        remindersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                remindersListView.setSelection(i);
                selectedIndex = i;
            }
        });

        final Button editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex != -1)
                {
                    Reminder selected = remindersAdapter.reminderArrayList.get(selectedIndex);

                    Intent intent = new Intent(ReminderActivity.this, NewReminderActivity.class);
                    intent.putExtra("reminder", selected);
                    startActivityForResult(intent, 1);
                }
            }
        });

        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex != -1)
                {
                    remindersAdapter.reminderArrayList.remove(selectedIndex);
                    selectedIndex = -1;
                }
                remindersAdapter.notifyDataSetChanged();

                Intent notifyIntent = new Intent(ReminderActivity.this, WeatherAppReminderReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), selectedIndex, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences myPreferences = getApplicationContext().getSharedPreferences("WEATHER_APP_REMINDER_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.clear().apply();
        for (int i = 0; i < remindersArrayList.size(); i++)
        {
            Reminder currentReminder = remindersArrayList.get(i);
            String toStringReminder = currentReminder.date.getYear() + "-" + currentReminder.date.getMonthValue() + "-" + currentReminder.date.getDayOfMonth() + "-";
            toStringReminder += currentReminder.time.getHour() + "-" + currentReminder.time.getMinute();
            editor.putString(String.valueOf(i), toStringReminder);
        }
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Reminder newReminder = (Reminder) data.getSerializableExtra("new_reminder");
                if (newReminder != null) remindersArrayList.add(newReminder);
                else
                {
                    if (selectedIndex != -1)
                    {
                        remindersArrayList.set(selectedIndex, (Reminder) data.getSerializableExtra("edited_old_reminder"));
                    }
                }

                if (selectedIndex != -1)
                {
                    setAlarm(remindersArrayList.get(selectedIndex), selectedIndex);
                }
            }
            remindersAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAlarm(Reminder reminder, int selectedIndex)
    {
        Intent notifyIntent = new Intent(this, WeatherAppReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (getApplicationContext(), selectedIndex, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(reminder.date.getYear(), reminder.date.getMonthValue() - 1, reminder.date.getDayOfMonth(), reminder.time.getHour(), reminder.time.getMinute(), 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}

