package com.example.jahotelreservationapp.activities.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.activities.LoginActivity;
import com.example.jahotelreservationapp.activities.user.BookingActivity;
import com.example.jahotelreservationapp.activities.user.PaymentActivity;
import com.example.jahotelreservationapp.activities.user.RoomManagementActivity;
import com.example.jahotelreservationapp.activities.user.NotificationActivity;
import com.example.jahotelreservationapp.websocket.WebSocketManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDashboardActivity extends AppCompatActivity {
    private static final String TAG = "UserDashboardActivity";
    private static final int POST_NOTIFICATIONS_REQUEST_CODE = 1001;
    private WebSocketManager webSocketManager;

    private TextView tvWelcomeUser;
    private LinearLayout llViewRooms, llBooking, llPayment, llNotification;
    private TextView btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize WebSocketManager and connect to your WebSocket server
        webSocketManager = new WebSocketManager();
        webSocketManager.connect(this);

        // Get references to UI elements
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        llViewRooms = findViewById(R.id.llViewRooms);
        llBooking = findViewById(R.id.llBooking);
        llPayment = findViewById(R.id.llPayment);
        llNotification = findViewById(R.id.llNotification);
        btnLogout = findViewById(R.id.btnLogout);

        // Request POST_NOTIFICATIONS permission for Android 13+ if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        POST_NOTIFICATIONS_REQUEST_CODE);
            }
        }

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Fetch user details from Firestore to display the name
        String userId = currentUser.getUid();
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        String name = documentSnapshot.getString("name");

                        if (role != null && role.equalsIgnoreCase("admin")) {
                            Toast.makeText(this, "Access denied: Admin detected!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        tvWelcomeUser.setText("Welcome " + name);
                    } else {
                        Toast.makeText(this, "User data not found in Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Set up click listeners for dashboard items
        llViewRooms.setOnClickListener(v -> startActivity(new Intent(this, RoomManagementActivity.class)));
        llBooking.setOnClickListener(v -> startActivity(new Intent(this, BookingActivity.class)));
        llPayment.setOnClickListener(v -> startActivity(new Intent(this, PaymentActivity.class)));
        llNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));

        // Logout button
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        // Disconnect WebSocket to clean up resources
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
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
