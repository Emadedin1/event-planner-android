package com.example.a4200gp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Int): Event?

    @Query("SELECT * FROM events WHERE dateTime > :currentTime ORDER BY dateTime ASC")
    fun getUpcomingEvents(currentTime: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE category = :category ORDER BY dateTime ASC")
    fun getEventsByCategory(category: String): LiveData<List<Event>>

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int
}
