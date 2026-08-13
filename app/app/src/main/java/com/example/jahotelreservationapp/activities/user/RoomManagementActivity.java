package com.example.jahotelreservationapp.activities.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.adapters.RoomAdapter;
import com.example.jahotelreservationapp.models.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RoomManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private FloatingActionButton btnAddRoom;
    private RoomAdapter adapter;
    private List<Room> roomList;
    private FirebaseFirestore db;
    private static final String TAG = "RoomManagementActivity";

    // Flag to determine if the current user is an admin
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);

        recyclerView = findViewById(R.id.recyclerViewRooms);
        emptyView = findViewById(R.id.emptyView);
        btnAddRoom = findViewById(R.id.btnAddRoom);

        // Get the admin flag from Intent extras; default is false (user)
        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        // Hide add button for non-admin users
        if (!isAdmin) {
            btnAddRoom.setVisibility(View.GONE);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();
        adapter = new RoomAdapter(roomList, room -> {
            // Handle room item click if needed.
            Toast.makeText(RoomManagementActivity.this, "Clicked on room: " + room.getRoomNumber(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAvailableRooms();

        btnAddRoom.setOnClickListener(v -> {
            // Only admin should reach here. Add room functionality for admin.
            Toast.makeText(RoomManagementActivity.this, "Add Room clicked", Toast.LENGTH_SHORT).show();
            // TODO: Implement add room functionality (e.g., launch an AddRoomActivity)
        });
    }

    private void loadAvailableRooms() {
        CollectionReference roomsRef = db.collection("Rooms");
        Query query = roomsRef.whereEqualTo("status", "available");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                roomList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Room room = document.toObject(Room.class);
                    if (room != null) {
                        room.setId(document.getId()); // Optionally store the document ID
                        roomList.add(room);
                    }
                }
                adapter.notifyDataSetChanged();

                // Toggle empty view based on whether roomList is empty
                if (roomList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
                Toast.makeText(RoomManagementActivity.this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
