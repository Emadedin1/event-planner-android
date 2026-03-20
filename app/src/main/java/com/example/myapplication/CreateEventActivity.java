package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventTitle, etEventDescription, etEventLocation, etEventCategory, etEventPriority;
    private Button btnSelectDate, btnSelectTime, btnSaveEvent, btnCancelEvent;
    private TextView tvSelectedDate, tvSelectedTime;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventCategory = findViewById(R.id.etEventCategory);
        etEventPriority = findViewById(R.id.etEventPriority);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnCancelEvent = findViewById(R.id.btnCancelEvent);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSaveEvent.setOnClickListener(v -> saveEvent());
        btnCancelEvent.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            selectedMonth + 1,
                            selectedDay,
                            selectedYear
                    );
                    tvSelectedDate.setText(selectedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                    );
                    tvSelectedTime.setText(selectedTime);
                },
                hour,
                minute,
                false
        );

        timePickerDialog.show();
    }

    private void saveEvent() {
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String category = etEventCategory.getText().toString().trim();
        String priority = etEventPriority.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etEventTitle.setError("Title is required");
            etEventTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedTime)) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        // For now, just confirm the event was "saved"
        // Later, replace this with database/repository insert code
        String message = "Event Saved:\n"
                + "Title: " + title + "\n"
                + "Description: " + description + "\n"
                + "Location: " + location + "\n"
                + "Category: " + category + "\n"
                + "Priority: " + priority + "\n"
                + "Date: " + selectedDate + "\n"
                + "Time: " + selectedTime;

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Optional: close screen after save
        finish();
    }
}