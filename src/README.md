# Source Organization

The application source is preserved under `app/`.

The Android Java package contains:
- activities for authentication, user workflows, and administrator workflows
- adapters for bookings, notifications, payments, and rooms
- models for Booking, Payment, Room, and NotificationItem
- repositories and services
- ViewModels and utility classes
- WebSocket components for notification communication

This `src/` folder is documentation-only; the actual Android source remains under `app/app/src/main/`.
