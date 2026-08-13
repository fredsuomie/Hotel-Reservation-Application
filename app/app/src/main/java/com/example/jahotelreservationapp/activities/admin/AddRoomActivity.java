package com.example.jahotelreservationapp.activities.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jahotelreservationapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddRoomActivity extends AppCompatActivity {

    private EditText etRoomNumber, etRoomType, etRoomPrice, etImageName;
    private Button btnAddRoom;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        etRoomNumber = findViewById(R.id.etRoomNumber);
        etRoomType = findViewById(R.id.etRoomType);
        etRoomPrice = findViewById(R.id.etRoomPrice);
        etImageName = findViewById(R.id.etImageName);
        btnAddRoom = findViewById(R.id.btnAddRoom);

        db = FirebaseFirestore.getInstance();

        btnAddRoom.setOnClickListener(v -> addRoom());
    }

    private void addRoom() {
        String roomNumber = etRoomNumber.getText().toString().trim();
        String roomType = etRoomType.getText().toString().trim();
        String roomPriceStr = etRoomPrice.getText().toString().trim();
        String imageName = etImageName.getText().toString().trim();

        if (TextUtils.isEmpty(roomNumber) || TextUtils.isEmpty(roomType)
                || TextUtils.isEmpty(roomPriceStr) || TextUtils.isEmpty(imageName)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double roomPrice;
        try {
            roomPrice = Double.parseDouble(roomPriceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create room data
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("roomNumber", roomNumber);
        roomData.put("type", roomType);
        roomData.put("price", roomPrice);
        roomData.put("status", "available"); // New room is available by default
        roomData.put("imageName", imageName);

        // Add the room to Firestore
        db.collection("Rooms").add(roomData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddRoomActivity.this, "Room added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after adding
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddRoomActivity.this, "Failed to add room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
