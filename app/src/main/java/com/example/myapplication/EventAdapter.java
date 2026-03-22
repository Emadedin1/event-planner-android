package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;
    private Context context;

    public EventAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    public void updateList(List<Event> newList) {
        events = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_event, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        final Event currentEvent = events.get(position);

        holder.title.setText(currentEvent.title);
        holder.category.setText("Category: " + currentEvent.category);
        holder.date.setText("Date: " + formatDate(currentEvent.dateTime));

        holder.priority.setText("Priority: " + getPriorityLabel(currentEvent.priority));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("event_id", currentEvent.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView category;
        TextView date;
        TextView priority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
            priority = itemView.findViewById(R.id.priority);
        }
    }

    private String formatDate(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        Date date = new Date(timeMillis);
        return sdf.format(date);
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
