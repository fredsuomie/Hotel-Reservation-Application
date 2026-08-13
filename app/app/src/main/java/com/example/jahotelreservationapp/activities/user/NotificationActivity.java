package com.example.jahotelreservationapp.activities.user;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.adapters.NotificationAdapter;
import com.example.jahotelreservationapp.models.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // For demonstration, initialize an empty list.
        // In a real implementation, you would retrieve notifications from a local database,
        // SharedPreferences, or a singleton class where your WebSocket listener stores messages.
        notificationList = new ArrayList<>();

        // Example: Add dummy notification for testing
        // notificationList.add(new NotificationItem("Booking Confirmed", "Your booking for Room A has been confirmed."));

        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);
    }

    // Optionally, create a method to update the list when a new notification is received.
    public void addNotification(NotificationItem notification) {
        notificationList.add(0, notification); // add at the top
        adapter.notifyItemInserted(0);
    }
}
