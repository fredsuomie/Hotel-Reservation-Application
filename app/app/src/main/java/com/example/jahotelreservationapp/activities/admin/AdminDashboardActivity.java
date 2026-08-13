package com.example.jahotelreservationapp.activities.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.activities.LoginActivity;
import com.example.jahotelreservationapp.utils.NotificationHelper;
import com.example.jahotelreservationapp.websocket.WebSocketManager;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {
    private static final String TAG = "AdminDashboardActivity";
    private static final int POST_NOTIFICATIONS_REQUEST_CODE = 1001;

    private TextView tvWelcomeAdmin;
    private Button btnLogoutAdmin;

    // WebSocket manager instance
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);

        // Create the notification channel for system notifications
        NotificationHelper.createNotificationChannel(this);

        // (Optional) Request POST_NOTIFICATIONS for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        POST_NOTIFICATIONS_REQUEST_CODE);
            }
        }

        // Initialize and connect WebSocket with this context
        webSocketManager = new WebSocketManager();
        webSocketManager.connect(this);

        String adminName = getIntent().getStringExtra("ADMIN_NAME");
        if (adminName != null && !adminName.isEmpty()) {
            tvWelcomeAdmin.setText("Welcome Admin: " + adminName);
        } else {
            tvWelcomeAdmin.setText("Welcome Admin");
        }

        // Manage Rooms
        findViewById(R.id.llManageRooms).setOnClickListener(view -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageRoomsActivity.class));
        });

        // Manage Bookings
        findViewById(R.id.llManageBookings).setOnClickListener(view -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageBookingsActivity.class));
        });

        // View Payments
        findViewById(R.id.llViewPayments).setOnClickListener(view -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminViewPaymentsActivity.class));
        });

        // Send Notification (opens an activity to send custom notifications)
        findViewById(R.id.llSendNotification).setOnClickListener(view -> {
            startActivity(new Intent(AdminDashboardActivity.this, SendNotificationActivity.class));
        });

        // Logout
        btnLogoutAdmin.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect WebSocket when admin leaves the dashboard
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == POST_NOTIFICATIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "POST_NOTIFICATIONS permission granted");
            } else {
                Log.e(TAG, "POST_NOTIFICATIONS permission denied");
                Toast.makeText(this, "Notifications may not work properly without permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
