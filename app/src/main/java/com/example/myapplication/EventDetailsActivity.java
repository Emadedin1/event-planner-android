package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Event;
import com.example.myapplication.data.EventRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AlertDialog;

public class EventDetailsActivity extends AppCompatActivity {

    private EventRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Event Details");
        }

        repository = new EventRepository(AppDatabase.getInstance(this).eventDao());

        int eventId = getIntent().getIntExtra("event_id", -1);

        Button btnBackDetails = findViewById(R.id.btnBackDetails);
        Button btnEditEvent = findViewById(R.id.btnEditEvent);
        Button btnDeleteEvent = findViewById(R.id.btnDeleteEvent);

        btnBackDetails.setOnClickListener(v -> finish());

        btnEditEvent.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, EditEventActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
            finish();
        });

        btnDeleteEvent.setOnClickListener(v -> {
            new AlertDialog.Builder(EventDetailsActivity.this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        repository.getEventById(eventId, event -> {
                            if (event != null) {
                                ReminderScheduler.cancelReminder(EventDetailsActivity.this, eventId);
                                repository.delete(event);
                                runOnUiThread(() -> {
                                    Toast.makeText(EventDetailsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        final TextView title = findViewById(R.id.detailTitle);
        final TextView desc = findViewById(R.id.detailDesc);
        final TextView date = findViewById(R.id.detailDate);
        final TextView category = findViewById(R.id.detailCategory);
        final TextView priority = findViewById(R.id.detailPriority);
        final TextView location = findViewById(R.id.detailLocation);
        final TextView status = findViewById(R.id.detailStatus);

        repository.getEventById(eventId, new EventRepository.EventCallback() {
            @Override
            public void onResult(final Event event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (event != null) {
                            title.setText(event.title);

                            if (event.description != null) {
                                desc.setText(event.description);
                            } else {
                                desc.setText("No description");
                            }

                            date.setText("Date: " + formatDate(event.dateTime));
                            category.setText("Category: " + event.category);
                            priority.setText("Priority: " + getPriorityLabel(event.priority));

                            if (event.location != null) {
                                location.setText("Location: " + event.location);
                            } else {
                                location.setText("Location: N/A");
                            }

                            if (event.isCompleted) {
                                status.setText("Status: Completed");
                            } else {
                                status.setText("Status: Pending");
                            }
                        }
                    }
                });
            }
        });
    }

    private String formatDate(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        Date date = new Date(timeMillis);
        return sdf.format(date);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
