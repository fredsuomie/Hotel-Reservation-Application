package com.example.jahotelreservationapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.models.Payment;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<Payment> paymentList;
    private OnPaymentClickListener listener;
    private SimpleDateFormat dateFormat;

    // Default public no-argument constructor
    public PaymentAdapter() {
        // Initialize with an empty list and no listener
        this.paymentList = new ArrayList<>();
        this.listener = null;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    // Existing constructor that takes data and a listener
    public PaymentAdapter(List<Payment> paymentList, OnPaymentClickListener listener) {
        this.paymentList = paymentList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    // Optional callback interface if you want to handle item clicks
    public interface OnPaymentClickListener {
        void onPaymentClick(Payment payment);
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.bind(payment, listener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPaymentDetails;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaymentDetails = itemView.findViewById(R.id.tvPaymentDetails);
        }

        public void bind(final Payment payment, final OnPaymentClickListener listener, SimpleDateFormat dateFormat) {
            String dateStr = "";
            Timestamp ts = payment.getTimestamp();
            if (ts != null) {
                dateStr = dateFormat.format(ts.toDate());
            }

            String details = "Payment ID: " + payment.getId() + "\n" +
                    "User: " + payment.getUserId() + "\n" +
                    "Booking: " + payment.getBookingId() + "\n" +
                    "Amount: $" + payment.getAmount() + "\n" +
                    "Status: " + payment.getStatus() + "\n" +
                    "Method: " + payment.getPaymentMethod() + "\n" +
                    "Date: " + dateStr;
            tvPaymentDetails.setText(details);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPaymentClick(payment);
                }
            });
        }
    }
}
