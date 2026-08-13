package com.example.jahotelreservationapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jahotelreservationapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Spinner spinnerRole;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering, please wait...");
        progressDialog.setCancelable(false);

        btnRegister.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            // Enhanced Input Validation
            if (name.isEmpty()) {
                etName.setError("Name is required");
                etName.requestFocus();
                return;
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Valid email is required");
                etEmail.requestFocus();
                return;
            }
            if (password.isEmpty() || password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                etPassword.requestFocus();
                return;
            }
            // Optionally prevent public registration as Admin
            if (role.equalsIgnoreCase("Admin")) {
                Toast.makeText(this, "Admin registration is restricted. Please contact support.", Toast.LENGTH_SHORT).show();
                return;
            }
            // All validations passed, register user
            registerUser(name, email, password, role);
        });

        tvBackToLogin.setOnClickListener(this::goToLogin);
    }

    private void registerUser(String name, String email, String password, String role) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Create a user document in Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("role", role);

                            db.collection("Users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        // Call Cloud Function to assign the role
                                        assignUserRole(email, role);
                                        Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegistrationActivity.this, "Failed to store user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Firestore error: " + e.getMessage());
                                    });
                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Registration error: " + task.getException().getMessage());
                    }
                });
    }

    private void assignUserRole(String email, String role) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("role", role);

        functions.getHttpsCallable("setUserRole")
                .call(data)
                .addOnSuccessListener(result -> {
                    Log.d("AssignRole", "Role assigned successfully");
                    // Force refresh the token to apply the new role (VERY IMPORTANT)
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        currentUser.getIdToken(true)
                                .addOnSuccessListener(tokenResult -> {
                                    Log.d("Auth", "Token refreshed: " + tokenResult.getClaims());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Auth", "Error refreshing token: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("AssignRole", "Error assigning role: " + e.getMessage()));
    }

    public void goToLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
