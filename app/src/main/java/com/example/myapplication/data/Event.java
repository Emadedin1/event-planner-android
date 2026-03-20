package com.example.myapplication.data;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    @Nullable
    public String description;
    public long dateTime;
    public String category;
    public int priority;
    @Nullable
    public String location;
    @Nullable
    public Long reminderTime;
    public boolean isCompleted;
    public long createdAt;

    public Event(String title,
                 @Nullable String description,
                 long dateTime,
                 String category,
                 int priority,
                 @Nullable String location,
                 @Nullable Long reminderTime,
                 boolean isCompleted,
                 long createdAt) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.category = category;
        this.priority = priority;
        this.location = location;
        this.reminderTime = reminderTime;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }
}
