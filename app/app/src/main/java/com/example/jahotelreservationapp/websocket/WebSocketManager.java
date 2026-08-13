package com.example.jahotelreservationapp.websocket;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    // Use wss:// for secure WebSocket connections
    private static final String SOCKET_URL = "wss://websocket-server-988359199000.us-central1.run.app";
    private WebSocket webSocket;
    private OkHttpClient client;

    /**
     * Establishes a WebSocket connection.
     *
     * @param context The Android Context needed for MyWebSocketListener (e.g., for notifications).
     */
    public void connect(Context context) {
        client = new OkHttpClient();
        // Create a request using the defined SOCKET_URL constant
        Request request = new Request.Builder()
                .url(SOCKET_URL)
                .build();
        // Create a new instance of MyWebSocketListener with the provided context
        MyWebSocketListener listener = new MyWebSocketListener(context);
        // Initiate the WebSocket connection
        webSocket = client.newWebSocket(request, listener);
        Log.d(TAG, "WebSocket connection initiated using URL: " + SOCKET_URL);
    }

    /**
     * Sends a text message through the established WebSocket connection.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
            Log.d(TAG, "Message sent: " + message);
        } else {
            Log.e(TAG, "WebSocket not connected; cannot send message");
        }
    }

    /**
     * Disconnects the WebSocket connection.
     */
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "User disconnected");
            webSocket = null;
            Log.d(TAG, "WebSocket disconnected.");
        }
    }
}
