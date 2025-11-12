# MyUniEvents (Kotlin, Jetpack Compose, Room, Firebase, DataStore, WorkManager)

This project is ready to import in Android Studio (Giraffe+). It demonstrates:
- Room for offline events
- Firebase Auth + Firestore for announcements (live updates)
- Firebase Storage for avatar upload
- WorkManager periodic sync for unpublished events
- DataStore-powered Dark Mode toggle (persistent)
- Jetpack Compose Material3 UI with snackbars, validation, bottom navigation

## Setup
1. In Firebase Console, create a project and add an **Android app** with package `com.example.myunievents`.
2. Download `google-services.json` and place it under `app/` directory.
3. In Firestore, create (or let the app create) a collection named `announcements`.
4. Enable **Email/Password** sign-in in Authentication.
5. (Optional) Enable Firebase Storage (default rules for development).

## Run
- Open this folder in Android Studio.
- Sync Gradle.
- Run on device/emulator.

## Notes
- The profile screen uses a tiny sample PNG as avatar bytes for demo. Replace with gallery picker to upload real images.
- Push notifications (FCM) are not included to keep setup simple.
