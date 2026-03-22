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

public class EditEventActivity extends AppCompatActivity {

    private EditText etEventTitle, etEventDescription, etEventLocation, etEventCategory, etEventPriority;
    private Button btnSelectDate, btnSelectTime, btnUpdateEvent, btnCancelEdit, btnNotificationSettings;
    private TextView tvSelectedDate, tvSelectedTime;

    private String selectedDate = "";
    private String selectedTime = "";
    private Long selectedReminderTime = null;

    private EventRepository repository;
    private Event currentEvent;
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        repository = new EventRepository(AppDatabase.getInstance(this).eventDao());

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventCategory = findViewById(R.id.etEventCategory);
        etEventPriority = findViewById(R.id.etEventPriority);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnNotificationSettings = findViewById(R.id.btnNotificationSettings);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);

        eventId = getIntent().getIntExtra("event_id", -1);

        if (eventId == -1) {
            Toast.makeText(this, "Invalid event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadExistingEventData();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnUpdateEvent.setOnClickListener(v -> updateEvent());
        btnCancelEdit.setOnClickListener(v -> finish());
        btnNotificationSettings.setOnClickListener(v -> notificationSettings());
    }

    private void loadExistingEventData() {
        repository.getEventById(eventId, event -> runOnUiThread(() -> {
            if (event == null) {
                Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            currentEvent = event;
            selectedReminderTime = event.reminderTime;

            etEventTitle.setText(event.title);
            etEventDescription.setText(event.description != null ? event.description : "");
            etEventLocation.setText(event.location != null ? event.location : "");
            etEventCategory.setText(event.category != null ? event.category : "");
            etEventPriority.setText(getPriorityLabel(event.priority));

            selectedDate = formatDateOnly(event.dateTime);
            selectedTime = formatTimeOnly(event.dateTime);

            tvSelectedDate.setText(selectedDate);
            tvSelectedTime.setText(selectedTime);
        }));
    }

    private void notificationSettings() {
        NotificationSettingsDialog dialog = new NotificationSettingsDialog();
        dialog.setOnReminderSetListener(new NotificationSettingsDialog.OnReminderSetListener() {
            @Override
            public void onReminderConfirmed(long reminderTimeMillis) {
                selectedReminderTime = reminderTimeMillis;
                Toast.makeText(EditEventActivity.this, "Reminder updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReminderDisabled() {
                selectedReminderTime = null;
                Toast.makeText(EditEventActivity.this, "Reminder disabled", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "notif_settings");
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (!TextUtils.isEmpty(selectedDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                calendar.setTime(sdf.parse(selectedDate));
            } catch (Exception ignored) {
            }
        }

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

        if (!TextUtils.isEmpty(selectedTime)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                calendar.setTime(sdf.parse(selectedTime));
            } catch (Exception ignored) {
            }
        }

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

    private void updateEvent() {
        if (currentEvent == null) {
            Toast.makeText(this, "Event not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

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

        currentEvent.title = title;
        currentEvent.description = TextUtils.isEmpty(description) ? null : description;
        currentEvent.location = TextUtils.isEmpty(location) ? null : location;
        currentEvent.category = category;
        currentEvent.priority = priority;
        currentEvent.dateTime = dateTimeMillis;
        currentEvent.reminderTime = selectedReminderTime;

        repository.update(currentEvent);
        ReminderScheduler.rescheduleReminder(this, currentEvent);

        Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
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

    private String formatDateOnly(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return sdf.format(timeMillis);
    }

    private String formatTimeOnly(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(timeMillis);
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

    private String getPriorityLabel(int priority) {
        if (priority == 3) {
            return "High";
        } else if (priority == 2) {
            return "Medium";
        } else if (priority == 1) {
            return "Low";
        } else {
            return String.valueOf(priority);
        }
    }
}
