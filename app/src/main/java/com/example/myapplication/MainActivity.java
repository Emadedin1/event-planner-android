package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnOpenCreateEvent;
    private Button btnOpenEditEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenCreateEvent = findViewById(R.id.btnOpenCreateEvent);
        btnOpenEditEvent = findViewById(R.id.btnOpenEditEvent);

        btnOpenCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        btnOpenEditEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditEventActivity.class);

            // temporary sample data so Edit screen can open with filled fields
            intent.putExtra("event_title", "Group Meeting");
            intent.putExtra("event_description", "Discuss final project tasks");
            intent.putExtra("event_location", "Library");
            intent.putExtra("event_category", "School");
            intent.putExtra("event_priority", "High");
            intent.putExtra("event_date", "03/25/2026");
            intent.putExtra("event_time", "02:30");

            startActivity(intent);
        });
    }
}