package com.example.jahotelreservationapp.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jahotelreservationapp.R;
import com.example.jahotelreservationapp.websocket.WebSocketManager;

public class SendNotificationActivity extends AppCompatActivity {
    private static final String TAG = "SendNotificationActivity";

    private EditText etTitle, etBody;
    private Button btnSend;

    // WebSocket manager for sending messages
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        etTitle = findViewById(R.id.etTitle);
        etBody = findViewById(R.id.etBody);
        btnSend = findViewById(R.id.btnSend);

        // Create and connect the WebSocket
        webSocketManager = new WebSocketManager();
        webSocketManager.connect(this);

        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String body = etBody.getText().toString().trim();

            if (title.isEmpty() || body.isEmpty()) {
                // You can show a Toast if fields are empty
                Log.e(TAG, "Title or body is empty, cannot send");
                return;
            }

            // Build a JSON message (adjust to match your server's expected format)
            // For example, "action": "notifyUsers" or "notifyAll"
            String messageJson = String.format(
                    "{\"action\":\"notifyUsers\",\"title\":\"%s\",\"body\":\"%s\"}",
                    title, body
            );

            // Send the message to the WebSocket server
            webSocketManager.sendMessage(messageJson);
            Log.d(TAG, "Notification message sent: " + messageJson);

            // Optionally, clear the fields
            etTitle.setText("");
            etBody.setText("");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect the WebSocket to avoid leaks
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }
}
