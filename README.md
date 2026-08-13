# Hotel Reservation Android App

A native Android hotel reservation application built with Java and Firebase, with separate user and administrator workflows for room management, booking, payments, and notifications.

## Project Overview

The application provides a hotel reservation workflow in which users can browse/manage rooms, make bookings, complete payments, and receive notifications. Administrators can manage rooms and bookings, review payments, and send notifications.

## Core Features

### User
- Registration and login
- Forgot-password workflow
- User dashboard
- Room management/browsing
- Hotel room booking
- Booking/payment workflow
- Payment history
- Notifications

### Administrator
- Administrator dashboard
- Add rooms
- Manage rooms
- Manage bookings
- View payments
- Send notifications

## Technology Stack

- Java
- Android SDK
- AndroidX
- Material Components
- RecyclerView / CardView
- Firebase Authentication
- Firebase Firestore
- Firebase Cloud Messaging
- Firebase Functions
- Firebase Analytics
- Glide
- OkHttp
- Socket.IO / WebSocket communication
- PayPal Android SDK
- Braintree Card SDK
- Android Studio / Gradle

## Architecture Components

The source includes activities, adapters, models, repositories, services, ViewModels, utilities, and WebSocket components. The application also uses Firebase services for authentication and cloud data operations.

## Security / Portfolio Notes

The original development archive contained Firebase configuration information. The public portfolio copy intentionally excludes:

- `google-services.json`
- `.firebaserc`
- generated Gradle/build directories

Before running the project locally, create your own Firebase configuration and place the appropriate `google-services.json` in the Android `app/` module.

Do not publish private API keys, payment credentials, service-account keys, or production Firebase configuration.

## Running Locally

1. Open the project in Android Studio.
2. Allow Gradle to sync.
3. Create/configure your own Firebase project.
4. Add your Firebase `google-services.json` to `app/`.
5. Enable the Firebase services required by the application.
6. Configure any required PayPal/Braintree credentials using secure local configuration.
7. Build and run the application on an Android emulator or device.

## Repository Structure

```text
hotel-reservation-app/
├── app/
│   ├── app/
│   │   ├── src/main/java/
│   │   ├── src/main/res/
│   │   ├── src/main/AndroidManifest.xml
│   │   └── build.gradle
│   ├── build.gradle
│   ├── settings.gradle
│   └── gradle/
├── docs/
│   ├── project_report.pdf
│   ├── viva_overview.pdf
│   └── PORTFOLIO_NOTES.md
├── screenshots/
├── src/
│   └── README.md
├── README.md
└── .gitignore
```

## Portfolio Description

A native Android hotel reservation application developed with Java and Firebase, featuring role-based user and administrator workflows, room management, booking, payment integration, and real-time notification capabilities.
