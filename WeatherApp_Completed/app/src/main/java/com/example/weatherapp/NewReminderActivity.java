package com.example.weatherapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class NewReminderActivity extends AppCompatActivity {
    public Reminder newReminder;

    public TextView editTime;
    public TextView editDate;

    private boolean isEditing = false;
    Context context = this;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        LinearLayout kayout=findViewById(R.id.rl);
        kayout.setBackgroundResource(BgImage.getInstance().getImageName());

        final Intent intent = getIntent();

        final Reminder oldReminder = (Reminder)intent.getSerializableExtra("reminder");

        editTime = findViewById(R.id.editTime);
        Button selectTimeBtn = findViewById(R.id.selectTimeBtn);
        selectTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourOfDay = 0, minute = 0;
                final boolean is24HourView = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(NewReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        editTime.setText(hourOfDay + ":" + minute);
                    }
                }, hourOfDay, minute, is24HourView);
                timePickerDialog.show();
            }
        });

        editDate = findViewById(R.id.editDate);
        Button selectDateBtn = findViewById(R.id.selectDateBtn);
        selectDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year, month, day;

                if (!isEditing) {
                    year = 2019;
                    month = 12;
                    day = 1;
                }
                else {
                    year = oldReminder.date.getYear();
                    month = oldReminder.date.getMonthValue();
                    day = oldReminder.date.getDayOfMonth();
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        editDate.setText(year + "-" + (month + 1) + "-" + day);
                    }
                }, year, month - 1, day);
                datePickerDialog.show();
            }
        });

        if (oldReminder != null)
        {
            editTime.setText(oldReminder.time.toString());
            editDate.setText(oldReminder.date.toString());
            isEditing = true;
        }

        final Intent returnIntent = new Intent();

        Button okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String time = ((TextView) findViewById(R.id.editTime)).getText().toString();
                String[] timeTokens = time.split(":");

                String date = ((TextView) findViewById(R.id.editDate)).getText().toString();
                String[] dateTokens = date.split("-");

                Log.i("dsdfs",Integer.toString(timeTokens.length));
                Log.i("dsdfs",Integer.toString(dateTokens.length));

                if(timeTokens.length != 1 && dateTokens.length != 1) {
                    if (!isEditing) {
                        newReminder = new Reminder(Integer.parseInt(timeTokens[0]), Integer.parseInt(timeTokens[1]), 0, Integer.parseInt(dateTokens[2]), Integer.parseInt(dateTokens[1]), Integer.parseInt(dateTokens[0]));
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        String formattedDate = newReminder.date.format(dateTimeFormatter);
                        newReminder.date = LocalDate.parse(formattedDate, dateTimeFormatter);
                        returnIntent.putExtra("new_reminder", newReminder);
                    }
                    else {
                        oldReminder.time = LocalTime.of(Integer.parseInt(timeTokens[0]), Integer.parseInt(timeTokens[1]), 0);
                        oldReminder.date = LocalDate.of(Integer.parseInt(dateTokens[0]), Integer.parseInt(dateTokens[1]), Integer.parseInt(dateTokens[2]));

                        Reminder editedOldReminder = new Reminder(1, 1, 1, 1, 1, 1);
                        editedOldReminder.time = oldReminder.time;
                        editedOldReminder.date = oldReminder.date;

                        returnIntent.putExtra("edited_old_reminder", editedOldReminder);
                        isEditing = false;
                    }
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                else
                {
                    Toast.makeText(context,"Dữ liệu sai !!!",Toast.LENGTH_LONG);
                }
            }
        });
    }
}
