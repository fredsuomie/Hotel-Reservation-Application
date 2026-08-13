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
import com.example.jahotelreservationapp.adapters.PaymentAdapter;
import com.example.jahotelreservationapp.models.Payment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminViewPaymentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPayments;
    private TextView emptyView;
    private PaymentAdapter adapter;
    private List<Payment> paymentList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "AdminViewPaymentsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_payments);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerViewPayments = findViewById(R.id.recyclerViewPayments);
        emptyView = findViewById(R.id.emptyView);

        recyclerViewPayments.setLayoutManager(new LinearLayoutManager(this));
        paymentList = new ArrayList<>();
        adapter = new PaymentAdapter(paymentList, payment -> {
            // Optional: handle payment item click
            Toast.makeText(AdminViewPaymentsActivity.this, "Selected Payment: " + payment.getId(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewPayments.setAdapter(adapter);

        // Check if user is admin from Firestore
        checkAdminRoleFromFirestore();
    }

    private void checkAdminRoleFromFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User not authenticated");
            finish();
            return;
        }

        // Fetch user role from Firestore
        db.collection("Users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Log.d(TAG, "User role from Firestore: " + role);

                        if ("Admin".equals(role)) {
                            // User is admin, load payments
                            Log.d(TAG, "Admin role confirmed, loading payments");
                            loadPayments();
                        } else {
                            // Not an admin
                            Toast.makeText(AdminViewPaymentsActivity.this,
                                    "Admin privileges required", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "User does not have admin role in Firestore");
                            finish();
                        }
                    } else {
                        Log.e(TAG, "User document not found in Firestore");
                        Toast.makeText(AdminViewPaymentsActivity.this,
                                "User information not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking user role: " + e.getMessage());
                    Toast.makeText(AdminViewPaymentsActivity.this,
                            "Error checking permissions", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void loadPayments() {
        CollectionReference paymentsRef = db.collection("Payments");
        Query query = paymentsRef.orderBy("timestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                paymentList.clear();
                Log.d(TAG, "Successfully retrieved payments: " + task.getResult().size());

                for (DocumentSnapshot document : task.getResult()) {
                    Payment payment = document.toObject(Payment.class);
                    if (payment != null) {
                        payment.setId(document.getId());
                        paymentList.add(payment);
                    }
                }
                adapter.notifyDataSetChanged();

                if (paymentList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerViewPayments.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerViewPayments.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(AdminViewPaymentsActivity.this,
                        "Failed to load payments", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading payments: ", task.getException());
            }
        });
    }
}