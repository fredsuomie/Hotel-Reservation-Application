package com.example.jahotelreservationapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.models.Booking;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private OnBookingActionListener actionListener;
    private SimpleDateFormat dateFormat;

    public interface OnBookingActionListener {
        void onApprove(Booking booking);
        void onReject(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, OnBookingActionListener actionListener) {
        this.bookingList = bookingList;
        this.actionListener = actionListener;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking, actionListener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBookingDetails;
        private Button btnApprove, btnReject;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingDetails = itemView.findViewById(R.id.tvBookingDetails);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        public void bind(final Booking booking, final OnBookingActionListener listener, SimpleDateFormat dateFormat) {
            String startDateStr = booking.getStartDate() != null ? dateFormat.format(booking.getStartDate().toDate()) : "N/A";
            String endDateStr = booking.getEndDate() != null ? dateFormat.format(booking.getEndDate().toDate()) : "N/A";

            String details = "Booking ID: " + booking.getId() + "\n" +
                    "User: " + booking.getUserId() + "\n" +
                    "Room: " + booking.getRoomId() + "\n" +
                    "From: " + startDateStr + "\n" +
                    "To: " + endDateStr + "\n" +
                    "Status: " + booking.getStatus();
            tvBookingDetails.setText(details);

            btnApprove.setOnClickListener(v -> listener.onApprove(booking));
            btnReject.setOnClickListener(v -> listener.onReject(booking));
        }
    }
}
