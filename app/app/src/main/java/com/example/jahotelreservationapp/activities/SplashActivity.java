package com.example.jahotelreservationapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.activities.admin.AdminDashboardActivity;
import com.example.jahotelreservationapp.activities.user.UserDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgLogo;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgLogo = findViewById(R.id.imgLogo);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Start fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imgLogo.startAnimation(fadeIn);

        // Delay for animation (3 seconds) then check login status
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is already logged in, check the role from Firestore
                String userId = currentUser.getUid();
                db.collection("Users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String role = documentSnapshot.getString("role");
                                if ("admin".equalsIgnoreCase(role)) {
                                    startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                                } else {
                                    // For regular users, also pass the username if needed.
                                    String name = documentSnapshot.getString("name");
                                    Intent intent = new Intent(SplashActivity.this, UserDashboardActivity.class);
                                    intent.putExtra("USERNAME", name);
                                    startActivity(intent);
                                }
                            } else {
                                // If user data not found, go to LoginActivity
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            }
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // On failure, go to LoginActivity as a fallback.
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        });
            } else {
                // No user is logged in; proceed to LoginActivity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000); // Delay time in milliseconds
    }
}
