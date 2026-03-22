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

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Event;
import com.example.myapplication.data.EventRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventTitle, etEventDescription, etEventLocation, etEventCategory, etEventPriority;
    private Button btnSelectDate, btnSelectTime, btnSaveEvent, btnCancelEvent, btnNotificationSettings;
    private TextView tvSelectedDate, tvSelectedTime;

    private String selectedDate = "";
    private String selectedTime = "";
    private Long selectedReminderTime = null;

    private EventRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        repository = new EventRepository(AppDatabase.getInstance(this).eventDao());

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventCategory = findViewById(R.id.etEventCategory);
        etEventPriority = findViewById(R.id.etEventPriority);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnCancelEvent = findViewById(R.id.btnCancelEvent);
        btnNotificationSettings = findViewById(R.id.btnNotificationSettings);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSaveEvent.setOnClickListener(v -> saveEvent());
        btnCancelEvent.setOnClickListener(v -> finish());
        btnNotificationSettings.setOnClickListener(v -> notificationSettings());
    }

    private void notificationSettings() {
        NotificationSettingsDialog dialog = new NotificationSettingsDialog();
        dialog.setOnReminderSetListener(new NotificationSettingsDialog.OnReminderSetListener() {
            @Override
            public void onReminderConfirmed(long reminderTimeMillis) {
                selectedReminderTime = reminderTimeMillis;
                Toast.makeText(CreateEventActivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReminderDisabled() {
                selectedReminderTime = null;
                Toast.makeText(CreateEventActivity.this, "Reminder disabled", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "notif_settings");
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
                true
        );

        timePickerDialog.show();
    }

    private void saveEvent() {
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String category = etEventCategory.getText().toString().trim();
        String priorityText = etEventPriority.getText().toString().trim();

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

        long dateTimeMillis = convertToMillis(selectedDate, selectedTime);
        if (dateTimeMillis == -1) {
            Toast.makeText(this, "Invalid date/time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            category = "Personal";
        }

        int priority = getPriorityValue(priorityText);

        Event event = new Event(
                title,
                TextUtils.isEmpty(description) ? null : description,
                dateTimeMillis,
                category,
                priority,
                TextUtils.isEmpty(location) ? null : location,
                selectedReminderTime,
                false,
                System.currentTimeMillis()
        );

        if (selectedReminderTime != null) {
            repository.insertWithCallback(event, id -> {
                event.id = (int) id;
                ReminderScheduler.scheduleReminder(getApplicationContext(), event);
            });
        } else {
            repository.insert(event);
        }

        Toast.makeText(this, "Event saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private long convertToMillis(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        try {
            return sdf.parse(date + " " + time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getPriorityValue(String priorityText) {
        if (priorityText.equalsIgnoreCase("High")) {
            return 3;
        } else if (priorityText.equalsIgnoreCase("Medium")) {
            return 2;
        } else if (priorityText.equalsIgnoreCase("Low")) {
            return 1;
        }

        try {
            return Integer.parseInt(priorityText);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
