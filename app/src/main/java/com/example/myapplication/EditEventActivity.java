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

public class EditEventActivity extends AppCompatActivity {

    private EditText etEventTitle, etEventDescription, etEventLocation, etEventCategory, etEventPriority;
    private Button btnSelectDate, btnSelectTime, btnUpdateEvent, btnCancelEdit;
    private TextView tvSelectedDate, tvSelectedTime;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventCategory = findViewById(R.id.etEventCategory);
        etEventPriority = findViewById(R.id.etEventPriority);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);

        loadExistingEventData();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnUpdateEvent.setOnClickListener(v -> updateEvent());
        btnCancelEdit.setOnClickListener(v -> finish());
    }

    private void loadExistingEventData() {
        if (getIntent() != null) {
            String title = getIntent().getStringExtra("event_title");
            String description = getIntent().getStringExtra("event_description");
            String location = getIntent().getStringExtra("event_location");
            String category = getIntent().getStringExtra("event_category");
            String priority = getIntent().getStringExtra("event_priority");
            String date = getIntent().getStringExtra("event_date");
            String time = getIntent().getStringExtra("event_time");

            if (title != null) etEventTitle.setText(title);
            if (description != null) etEventDescription.setText(description);
            if (location != null) etEventLocation.setText(location);
            if (category != null) etEventCategory.setText(category);
            if (priority != null) etEventPriority.setText(priority);

            if (date != null && !date.isEmpty()) {
                selectedDate = date;
                tvSelectedDate.setText(date);
            }

            if (time != null && !time.isEmpty()) {
                selectedTime = time;
                tvSelectedTime.setText(time);
            }
        }
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

    private void updateEvent() {
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

        // Later replace this with real database update code
        String message = "Event Updated:\n"
                + "Title: " + title + "\n"
                + "Description: " + description + "\n"
                + "Location: " + location + "\n"
                + "Category: " + category + "\n"
                + "Priority: " + priority + "\n"
                + "Date: " + selectedDate + "\n"
                + "Time: " + selectedTime;

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        finish();
    }
}