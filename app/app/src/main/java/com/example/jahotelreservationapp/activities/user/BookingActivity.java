package com.example.jahotelreservationapp.activities.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.adapters.RoomAdapter;
import com.example.jahotelreservationapp.models.Room;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView tvRoomDetails;
    private RecyclerView recyclerViewRooms;
    private Button btnCheckInDate, btnCheckOutDate, btnBookNow;
    private FirebaseFirestore db;
    private RoomAdapter adapter;
    private List<Room> roomList;
    private Room selectedRoom = null;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    // String variables for button text (for display only)
    private String checkInDate = "";
    private String checkOutDate = "";

    // Date objects for actual date values to be stored as Timestamps
    private Date checkInDateObj = null;
    private Date checkOutDateObj = null;

    private static final String TAG = "BookingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking); // Ensure this matches your new UI layout file

        // Initialize views from the layout
        tvRoomDetails = findViewById(R.id.tvRoomDetails);
        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);
        btnCheckInDate = findViewById(R.id.btnCheckInDate);
        btnCheckOutDate = findViewById(R.id.btnCheckOutDate);
        btnBookNow = findViewById(R.id.btnBookNow);

        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Setup RecyclerView for available rooms
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();
        adapter = new RoomAdapter(roomList, room -> {
            // When a room is selected, update tvRoomDetails and store the selected room
            selectedRoom = room;
            String details = "Room " + room.getRoomNumber() + " (" + room.getType() + ") - $"
                    + room.getPrice() + " per night";
            tvRoomDetails.setText(details);
        });
        recyclerViewRooms.setAdapter(adapter);

        loadAvailableRooms();

        // Set up DatePicker for Check-In Date
        btnCheckInDate.setOnClickListener(v -> showDatePickerDialog(true));

        // Set up DatePicker for Check-Out Date
        btnCheckOutDate.setOnClickListener(v -> showDatePickerDialog(false));

        // Book Now button: Validate input and create booking, then launch PaymentActivity
        btnBookNow.setOnClickListener(v -> bookRoom());
    }

    // Show a DatePickerDialog; isCheckIn==true for check-in, false for check-out
    private void showDatePickerDialog(final boolean isCheckIn) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(BookingActivity.this,
                (android.widget.DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    Date selectedDate = calendar.getTime();
                    String formattedDate = dateFormat.format(selectedDate);
                    if (isCheckIn) {
                        checkInDate = formattedDate;
                        checkInDateObj = selectedDate;
                        btnCheckInDate.setText(formattedDate);
                    } else {
                        checkOutDate = formattedDate;
                        checkOutDateObj = selectedDate;
                        btnCheckOutDate.setText(formattedDate);
                    }
                }, year, month, day);
        dialog.show();
    }

    // Load available rooms from Firestore into the RecyclerView
    private void loadAvailableRooms() {
        CollectionReference roomsRef = db.collection("Rooms");
        Query query = roomsRef.whereEqualTo("status", "available");
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                roomList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Room room = document.toObject(Room.class);
                    if (room != null) {
                        room.setId(document.getId());
                        roomList.add(room);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(BookingActivity.this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate inputs and create a booking record in Firestore using Timestamps,
    // then launch PaymentActivity with the new booking's ID and cost.
    private void bookRoom() {
        if (selectedRoom == null) {
            Toast.makeText(this, "Please select a room", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkInDateObj == null || checkOutDateObj == null) {
            Toast.makeText(this, "Please select both check-in and check-out dates", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID from Firebase Auth
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("userId", userId);
        bookingData.put("roomId", selectedRoom.getId());
        bookingData.put("startDate", new Timestamp(checkInDateObj));
        bookingData.put("endDate", new Timestamp(checkOutDateObj));
        bookingData.put("status", "pending"); // initial booking status

        // Create booking document in Firestore
        db.collection("Bookings").add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    // Retrieve the new booking document ID
                    String newBookingId = documentReference.getId();
                    // For demonstration, use the room's price as the booking cost.
                    double bookingCost = selectedRoom.getPrice();

                    Log.d(TAG, "Booking created with ID: " + newBookingId + " and cost: " + bookingCost);
                    Toast.makeText(BookingActivity.this, "Room booked successfully!", Toast.LENGTH_SHORT).show();

                    // Launch PaymentActivity with the booking ID and booking cost as extras
                    Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
                    intent.putExtra("BOOKING_ID", newBookingId);
                    intent.putExtra("AMOUNT", bookingCost);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookingActivity.this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error creating booking", e);
                });
    }
}
