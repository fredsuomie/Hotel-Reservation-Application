package com.example.jahotelreservationapp.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.adapters.RoomAdapter;
import com.example.jahotelreservationapp.models.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ManageRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRooms;
    private TextView emptyView;
    private FloatingActionButton btnAddRoom;
    private RoomAdapter adapter;
    private List<Room> roomList;
    private FirebaseFirestore db;
    private static final String TAG = "ManageRoomsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);
        emptyView = findViewById(R.id.emptyView);
        btnAddRoom = findViewById(R.id.btnAddRoom);

        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();
        adapter = new RoomAdapter(roomList, room -> {
            // Handle room item click: Launch EditRoomActivity (to be implemented) or show a message
            Toast.makeText(ManageRoomsActivity.this, "Selected Room: " + room.getRoomNumber(), Toast.LENGTH_SHORT).show();
            // Example:
            // Intent intent = new Intent(ManageRoomsActivity.this, EditRoomActivity.class);
            // intent.putExtra("ROOM_ID", room.getId());
            // startActivity(intent);
        });
        recyclerViewRooms.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAllRooms();

        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch AddRoomActivity or display a dialog for adding a new room
                Toast.makeText(ManageRoomsActivity.this, "Add Room clicked", Toast.LENGTH_SHORT).show();
                // Example:
                 startActivity(new Intent(ManageRoomsActivity.this, AddRoomActivity.class));
            }
        });
    }

    private void loadAllRooms() {
        CollectionReference roomsRef = db.collection("Rooms");
        // Optionally order by room number if needed
        Query query = roomsRef.orderBy("roomNumber");
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
                if (roomList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerViewRooms.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerViewRooms.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(ManageRoomsActivity.this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting rooms: ", task.getException());
            }
        });
    }
}
