package com.example.myapplication.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(int id);

    @Query("SELECT * FROM events WHERE dateTime > :currentTime ORDER BY dateTime ASC")
    LiveData<List<Event>> getUpcomingEvents(long currentTime);

    @Query("SELECT * FROM events WHERE category = :category ORDER BY dateTime ASC")
    LiveData<List<Event>> getEventsByCategory(String category);

    @Query("SELECT COUNT(*) FROM events")
    int getEventCount();
}
