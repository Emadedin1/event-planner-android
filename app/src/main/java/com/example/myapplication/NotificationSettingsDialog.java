package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationSettingsDialog extends DialogFragment {

    public interface OnReminderSetListener {
        void onReminderConfirmed(long reminderTimeMillis);
        void onReminderDisabled();
    }

    private OnReminderSetListener listener;
    private String selectedDate = "";
    private String selectedTime = "";

    public void setOnReminderSetListener(OnReminderSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.notification_settings, null);

        Switch switchEnableReminder = view.findViewById(R.id.switchEnableReminder);
        LinearLayout layoutReminderOptions = view.findViewById(R.id.layoutReminderOptions);
        Button btnSelectDate = view.findViewById(R.id.btnSelectDate);
        Button btnSelectTime = view.findViewById(R.id.btnSelectTime);
        TextView tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        TextView tvSelectedTime = view.findViewById(R.id.tvSelectedTime);
        Button btnConfirm = view.findViewById(R.id.btnConfirmReminder);
        Button btnCancel = view.findViewById(R.id.btnCancelReminder);

        // Toggle shows/hides date and time options
        switchEnableReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutReminderOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Date picker
        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(requireContext(),
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate = String.format(Locale.getDefault(),
                                "%02d/%02d/%04d",
                                selectedMonth + 1,
                                selectedDay,
                                selectedYear);
                        tvSelectedDate.setText(selectedDate);
                    }, year, month, day).show();
        });

        // Time picker
        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(requireContext(),
                    (timePicker, selectedHour, selectedMinute) -> {
                        selectedTime = String.format(Locale.getDefault(),
                                "%02d:%02d",
                                selectedHour,
                                selectedMinute);
                        tvSelectedTime.setText(selectedTime);
                    }, hour, minute, true).show();
        });

        // Confirm button
        btnConfirm.setOnClickListener(v -> {
            if (!switchEnableReminder.isChecked()) {
                if (listener != null) listener.onReminderDisabled();
                dismiss();
                return;
            }

            if (selectedDate.isEmpty()) {
                Toast.makeText(getContext(), "Please select a reminder date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTime.isEmpty()) {
                Toast.makeText(getContext(), "Please select a reminder time", Toast.LENGTH_SHORT).show();
                return;
            }

            long reminderMillis = convertToMillis(selectedDate, selectedTime);
            if (reminderMillis == -1) {
                Toast.makeText(getContext(), "Invalid date/time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (reminderMillis <= System.currentTimeMillis()) {
                Toast.makeText(getContext(), "Reminder time must be in the future", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) listener.onReminderConfirmed(reminderMillis);
            dismiss();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> dismiss());

        return new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    private long convertToMillis(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        try {
            return sdf.parse(date + " " + time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
