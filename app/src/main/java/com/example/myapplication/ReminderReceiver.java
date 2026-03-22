package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int eventId = intent.getIntExtra("event_id", -1);
        String eventTitle = intent.getStringExtra("event_title");

        if (eventId == -1 || eventTitle == null) return;

        NotificationHelper.sendNotification(context, eventId, eventTitle);
    }
}
