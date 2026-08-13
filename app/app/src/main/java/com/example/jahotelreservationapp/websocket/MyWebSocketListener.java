package com.example.jahotelreservationapp.websocket;

import android.content.Context;
import android.util.Log;

import com.example.jahotelreservationapp.utils.NotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MyWebSocketListener extends WebSocketListener {
    private static final String TAG = "MyWebSocketListener";
    private Context context;

    // Constructor that accepts a Context for notification purposes
    public MyWebSocketListener(Context context) {
        this.context = context;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "WebSocket opened: " + response);
        // Optionally, join a room or send an initial message:
        webSocket.send("{\"action\": \"joinRoom\", \"room\": \"admins\"}");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Message received: " + text);
        try {
            JSONObject json = new JSONObject(text);
            // Assume your JSON message contains "title" and "body" for notifications
            String title = json.getString("title");
            String body = json.getString("body");

            // Directly post a system notification using NotificationHelper
            // You can choose a unique notificationId (here, we use 1)
            NotificationHelper.showNotification(context, title, body, 1);

            // Optionally, you could update a shared data source if you have a NotificationActivity

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing WebSocket message", e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(TAG, "Binary message received: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "WebSocket closing: " + code + " / " + reason);
        webSocket.close(1000, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket error: ", t);
    }
}

