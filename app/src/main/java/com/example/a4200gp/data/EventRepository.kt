package com.example.a4200gp.data

import androidx.lifecycle.LiveData

class EventRepository(private val dao: EventDao) {

    val allEvents: LiveData<List<Event>> = dao.getAllEvents()

    suspend fun insert(event: Event) = dao.insert(event)

    suspend fun update(event: Event) = dao.update(event)

    suspend fun delete(event: Event) = dao.delete(event)

    suspend fun getEventById(id: Int) = dao.getEventById(id)

    fun getUpcomingEvents(currentTime: Long) = dao.getUpcomingEvents(currentTime)

    fun getEventsByCategory(category: String) = dao.getEventsByCategory(category)
}
