package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Event;
import com.example.myapplication.data.EventRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EventRepository repository;
    private RecyclerView recyclerView;
    private EventAdapter adapter;

    private List<Event> allEvents = new ArrayList<>();

    private EditText searchInput;
    private TextView emptyState;
    private TextView dashboardText;

    private Spinner categorySpinner;
    private Spinner statusSpinner;
    private Spinner sortSpinner;
    private Button btnAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new EventRepository(AppDatabase.getInstance(this).eventDao());

        recyclerView = findViewById(R.id.recyclerView);
        searchInput = findViewById(R.id.searchInput);
        emptyState = findViewById(R.id.emptyState);
        dashboardText = findViewById(R.id.dashboardText);

        categorySpinner = findViewById(R.id.categorySpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        sortSpinner = findViewById(R.id.sortSpinner);

        adapter = new EventAdapter(new ArrayList<Event>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // SPINNERS
        String[] categories = {"All", "Work", "School", "Personal"};
        categorySpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories));

        String[] statuses = {"All", "Completed", "Pending"};
        statusSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses));

        String[] sorts = {"Upcoming", "Newest", "Priority"};
        sortSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sorts));

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        btnAddEvent = findViewById(R.id.btnAddEvent);

        btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        categorySpinner.setOnItemSelectedListener(spinnerListener);
        statusSpinner.setOnItemSelectedListener(spinnerListener);
        sortSpinner.setOnItemSelectedListener(spinnerListener);

        // LIVE DATA OBSERVE
        repository.allEvents.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                allEvents = events;
                applyFilters();
            }
        });

        // SEARCH
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {

        String query = searchInput.getText().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        String selectedSort = sortSpinner.getSelectedItem().toString();

        List<Event> filtered = new ArrayList<Event>();

        int upcomingCount = 0;
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < allEvents.size(); i++) {

            Event e = allEvents.get(i);

            boolean matchesSearch = false;
            if (e.title.toLowerCase().contains(query)) {
                matchesSearch = true;
            }

            boolean matchesCategory = false;
            if (selectedCategory.equals("All") || e.category.equals(selectedCategory)) {
                matchesCategory = true;
            }

            boolean matchesStatus = false;
            if (selectedStatus.equals("All")) {
                matchesStatus = true;
            } else if (selectedStatus.equals("Completed") && e.isCompleted) {
                matchesStatus = true;
            } else if (selectedStatus.equals("Pending") && !e.isCompleted) {
                matchesStatus = true;
            }

            if (e.dateTime >= currentTime) {
                upcomingCount++;
            }

            if (matchesSearch && matchesCategory && matchesStatus) {
                filtered.add(e);
            }
        }

        // SIMPLE SORT (no lambda)
        if (selectedSort.equals("Upcoming")) {
            sortByDate(filtered);
        } else if (selectedSort.equals("Newest")) {
            sortByCreated(filtered);
        } else if (selectedSort.equals("Priority")) {
            sortByPriority(filtered);
        }

        dashboardText.setText("Upcoming Events: " + upcomingCount);

        if (filtered.size() == 0) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }

        adapter.updateList(filtered);
    }

    // SIMPLE SORT METHODS

    private void sortByDate(List<Event> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).dateTime > list.get(j).dateTime) {
                    Event temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }

    private void sortByCreated(List<Event> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).createdAt < list.get(j).createdAt) {
                    Event temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }

    private void sortByPriority(List<Event> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).priority < list.get(j).priority) {
                    Event temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }
}
