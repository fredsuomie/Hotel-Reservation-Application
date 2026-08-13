package com.example.jahotelreservationapp.activities.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jahotelreservationapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URISyntaxException;

import io.socket.client.IO;       // <-- Make sure these are present
import io.socket.client.Socket;  // <-- Make sure these are present
import io.socket.emitter.Emitter; // <-- Make sure these are present

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// ... then your PaymentActivity class ...


public class PaymentActivity extends AppCompatActivity {
    // Tag for logging
    private static final String TAG = "PaymentActivity";
    // Request code for PayPal payment
    private static final int PAYPAL_REQUEST_CODE = 123;

    // PayPal configuration constants for sandbox mode
    // PayPal configuration constants for sandbox mode
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AUQt-FAw1JPyZp54gOG2F7Gk7XjwJmOZZK09OD2Xl5UlYpirfTg56IE0PxEdFhPc-A1c0y6Z_sflv8yB";
    private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("Payment Activity App")
            .merchantPrivacyPolicyUri(Uri.parse("https://example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://example.com/legal"));

    // UI Components
    private TextView tvPaymentDetails;
    private Spinner spinnerPaymentMethod;
    private Button btnPayNow;
    // Firestore instance (if needed for updates)
    private FirebaseFirestore db;

    // Data passed from previous activity
    private String bookingId;
    private double amount;

    // Socket.IO client for WebSocket connection
    private Socket mSocket;
    // URL of your WebSocket (Socket.IO) server on Cloud Run (use HTTPS for Socket.IO)
    private static final String SOCKET_URL = "wss://websocket-server-988359199000.us-central1.run.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Bind UI components
        tvPaymentDetails = findViewById(R.id.tvPaymentDetails);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        btnPayNow = findViewById(R.id.btnPayNow);
        db = FirebaseFirestore.getInstance();

        // Retrieve booking ID and amount
        bookingId = getIntent().getStringExtra("BOOKING_ID");
        amount = getIntent().getDoubleExtra("AMOUNT", 0.0);

        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "No booking ID provided. Cannot proceed.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (amount <= 0) {
            Toast.makeText(this, "Invalid amount. Cannot proceed.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String details = "Pay $" + amount + " for Booking ID: " + bookingId;
        tvPaymentDetails.setText(details);

        // Start PayPalService
        Intent serviceIntent = new Intent(this, PayPalService.class);
        serviceIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(serviceIntent);

        // Initialize and connect to the Socket.IO server
        try {
            IO.Options options = new IO.Options();
            //options.transports = new String[]{"websocket"};
            mSocket = IO.socket(SOCKET_URL, options);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI syntax error: " + e.getMessage());
            Toast.makeText(this, "Socket connection error", Toast.LENGTH_SHORT).show();
        }

        if (mSocket != null) {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "WebSocket connected");
                    // Optionally, join a room using the booking ID
                    mSocket.emit("joinRoom", bookingId);
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "WebSocket disconnected");
                }
            }).on("notification", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    // Handle notifications from the server
                    Log.d(TAG, "Received notification: " + args[0]);
                }
            });

            mSocket.connect();
        }

        // Set click listener on the "Pay Now" button
        btnPayNow.setOnClickListener(v -> {
            String selectedMethod = spinnerPaymentMethod.getSelectedItem().toString();
            Log.d(TAG, "Selected Payment Method: " + selectedMethod);

            switch (selectedMethod) {
                case "Credit Card":
                    // Placeholder for Credit Card payment method
                    Toast.makeText(this, "Credit Card selected", Toast.LENGTH_SHORT).show();
                    break;
                case "UPI":
                    // Placeholder for UPI payment method
                    Toast.makeText(this, "UPI selected", Toast.LENGTH_SHORT).show();
                    break;
                case "Net Banking":
                    // Placeholder for Net Banking payment method
                    Toast.makeText(this, "Net Banking selected", Toast.LENGTH_SHORT).show();
                    break;
                case "PayPal":
                    startPayPalPayment();
                    break;
                default:
                    Toast.makeText(this, "Please select a valid payment method", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * Starts the PayPal checkout process by creating a PayPalPayment object
     * and launching PayPal's PaymentActivity.
     */
    private void startPayPalPayment() {
        BigDecimal paymentAmount = BigDecimal.valueOf(amount);
        PayPalPayment payment = new PayPalPayment(paymentAmount, "USD", "Room Booking Payment", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent paymentIntent = new Intent(PaymentActivity.this, com.paypal.android.sdk.payments.PaymentActivity.class);
        paymentIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        paymentIntent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(paymentIntent, PAYPAL_REQUEST_CODE);
    }

    /**
     * Handles the result from PayPal's PaymentActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        // Log the payment confirmation details (formatted JSON)
                        String confirmationString = confirm.toJSONObject().toString(4);
                        Log.d(TAG, "Payment Confirmation: " + confirmationString);

                        // Extract transaction ID from the confirmation response
                        String transactionId = "";
                        try {
                            JSONObject responseObj = confirm.toJSONObject().getJSONObject("response");
                            transactionId = responseObj.getString("id");
                        } catch (JSONException e) {
                            Log.e(TAG, "Transaction ID not found in response", e);
                        }

                        // Notify the user of successful payment
                        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

                        // Verify the payment via Cloud Function
                        verifyPaypalPayment(transactionId, bookingId);

                        // Emit event to WebSocket server after successful payment
                        if (mSocket != null && mSocket.connected()) {
                            mSocket.emit("paymentVerified", bookingId);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error reading payment confirmation", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalid Payment or Configuration", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Verifies the PayPal payment by calling a Firebase HTTP function.
     *
     * @param transactionId The transaction ID returned by PayPal.
     * @param bookingId     The booking ID associated with this payment.
     */
    private void verifyPaypalPayment(String transactionId, String bookingId) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            String url = "https://us-central1-jahotelreservationapp.cloudfunctions.net/verifyPaypalPayment";

            String json = "{\"transactionId\":\"" + transactionId + "\", \"bookingId\":\"" + bookingId + "\"}";
            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "PayPal Payment Verification Response: " + responseBody);
                    runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "Payment Verified", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e(TAG, "Error verifying PayPal Payment: " + response.message());
                    runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "Payment verification failed: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "PayPal Payment Verification Exception: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "Payment verification error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
        super.onDestroy();
        Log.d(TAG, "PayPal Service stopped and WebSocket disconnected");
    }
}