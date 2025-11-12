# MyUniEvents (Kotlin, Jetpack Compose, Room, Firebase, DataStore, WorkManager)

This project is ready to import in Android Studio (Giraffe+). It demonstrates:
- Room for offline events
- Firebase Auth + Firestore for announcements (live updates)
- Firebase Storage for avatar upload
- WorkManager periodic sync for unpublished events
- DataStore-powered Dark Mode toggle (persistent)
- Jetpack Compose Material3 UI with snackbars, validation, bottom navigation


## Run
- Open this folder in Android Studio.
- Sync Gradle.
- Run on device/emulator.

## Notes
- The profile screen uses a tiny sample PNG as avatar bytes for demo. Replace with gallery picker to upload real images.
- Push notifications (FCM) are not included to keep setup simple.
