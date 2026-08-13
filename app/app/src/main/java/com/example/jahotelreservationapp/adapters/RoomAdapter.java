package com.example.jahotelreservationapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jahotelreservationapp.R;
import com.bumptech.glide.Glide;
import com.example.jahotelreservationapp.models.Room;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Room room);
    }

    public RoomAdapter(List<Room> roomList, OnItemClickListener listener) {
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.bind(room, listener);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivRoomImage;
        private TextView tvRoomNumber, tvType, tvPrice, tvStatus;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvType = itemView.findViewById(R.id.tvType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(final Room room, final OnItemClickListener listener) {
            tvRoomNumber.setText("Room: " + room.getRoomNumber());
            tvType.setText("Type: " + room.getType());
            tvPrice.setText("Price: $" + room.getPrice());
            tvStatus.setText("Status: " + room.getStatus());

            // Use Glide to load the image
            int resId = itemView.getContext().getResources().getIdentifier(
                    room.getImageName(), "drawable", itemView.getContext().getPackageName());
            if (resId != 0) {
                Glide.with(itemView.getContext())
                        .load(resId)
                        .centerCrop()
                        .into(ivRoomImage);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.sample_room)
                        .centerCrop()
                        .into(ivRoomImage);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(room));
            Log.d("RoomAdapter", "ImageName: " + room.getImageName() + ", resId: " + resId);
        }

    }
}
