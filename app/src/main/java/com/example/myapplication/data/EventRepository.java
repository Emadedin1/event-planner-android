package com.example.myapplication.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {
    private final EventDao dao;
    private final ExecutorService ioExecutor;
    public final LiveData<List<Event>> allEvents;

    public interface EventCallback {
        void onResult(Event event);
    }

    public EventRepository(EventDao dao) {
        this.dao = dao;
        this.ioExecutor = Executors.newSingleThreadExecutor();
        this.allEvents = dao.getAllEvents();
    }

    public void insert(Event event) {
        ioExecutor.execute(() -> dao.insert(event));
    }

    public void update(Event event) {
        ioExecutor.execute(() -> dao.update(event));
    }

    public void delete(Event event) {
        ioExecutor.execute(() -> dao.delete(event));
    }

    public void getEventById(int id, EventCallback callback) {
        ioExecutor.execute(() -> callback.onResult(dao.getEventById(id)));
    }

    public LiveData<List<Event>> getUpcomingEvents(long currentTime) {
        return dao.getUpcomingEvents(currentTime);
    }

    public LiveData<List<Event>> getEventsByCategory(String category) {
        return dao.getEventsByCategory(category);
    }
}
