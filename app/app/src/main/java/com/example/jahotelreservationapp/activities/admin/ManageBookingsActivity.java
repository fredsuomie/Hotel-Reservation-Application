package com.example.jahotelreservationapp.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.adapters.BookingAdapter;
import com.example.jahotelreservationapp.models.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBookings;
    private TextView emptyView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;

    private static final String TAG = "ManageBookingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        emptyView = findViewById(R.id.emptyView);

        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList, new BookingAdapter.OnBookingActionListener() {
            @Override
            public void onApprove(Booking booking) {
                updateBookingStatus(booking, "approved");
            }

            @Override
            public void onReject(Booking booking) {
                updateBookingStatus(booking, "rejected");
            }
        });
        recyclerViewBookings.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Instead of assigning admin role via Cloud Functions,
        // we now verify admin privileges by checking Firestore user data.
        verifyAdminAndLoadBookings();
    }

    /**
     * Verifies that the logged-in user is an admin by checking their document in the "Users" collection.
     * Only if the user's "role" field is "Admin" will the bookings be loaded.
     */
    private void verifyAdminAndLoadBookings() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("Users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("Admin".equalsIgnoreCase(role)) {
                                Log.d("AdminCheck", "Admin status verified, loading bookings.");
                                loadBookings();
                            } else {
                                Toast.makeText(this, "Admin privileges required", Toast.LENGTH_SHORT).show();
                                Log.e("AdminCheck", "User does not have admin privileges");
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                            Log.e("AdminCheck", "User document not found");
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to verify admin status", Toast.LENGTH_SHORT).show();
                        Log.e("AdminCheck", "Error verifying admin status", e);
                        finish();
                    });
        } else {
            Log.e("AdminCheck", "User is not authenticated");
            finish();
        }
    }

    /**
     * Loads bookings from the "Bookings" collection.
     */
    private void loadBookings() {
        if (db == null) {
            Log.e(TAG, "Firestore database is not initialized.");
            return;
        }

        CollectionReference bookingsRef = db.collection("Bookings");
        Query query = bookingsRef.orderBy("startDate");

        query.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(ManageBookingsActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading bookings", task.getException());
                return;
            }

            QuerySnapshot result = task.getResult();
            if (result == null || result.isEmpty()) {
                Log.d(TAG, "No bookings found.");
                updateUIVisibility(true);
                return;
            }

            bookingList.clear();
            for (DocumentSnapshot document : result) {
                Booking booking = document.toObject(Booking.class);
                if (booking != null) {
                    booking.setId(document.getId());
                    bookingList.add(booking);
                }
            }

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                updateUIVisibility(bookingList.isEmpty());
            });
        });
    }

    // Helper method to update UI visibility
    private void updateUIVisibility(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerViewBookings.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerViewBookings.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the booking status in Firestore by calling the appropriate cloud function.
     * In this example, the cloud functions are still used for processing the update.
     */
    private void updateBookingStatus(Booking booking, String newStatus) {
        if (booking == null || booking.getId() == null) {
            Toast.makeText(this, "Invalid booking", Toast.LENGTH_SHORT).show();
            return;
        }

        // Directly update the booking status in Firestore
        db.collection("Bookings").document(booking.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ManageBookingsActivity.this, "Booking " + newStatus, Toast.LENGTH_SHORT).show();
                    booking.setStatus(newStatus);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManageBookingsActivity.this, "Failed to update booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating booking status", e);
                });
    }
}