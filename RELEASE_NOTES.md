# MediCareReminder – Release Notes

**Version:** 1.0.0  
**Release:** Part 3 – Final Application  
**Platform:** Android  
**Package name:** `com.example.medicareReminder`

## 1. Overview

MediCareReminder has been enhanced from its initial prototype into a more complete medication management application. This release introduces improved medication management, automated reminders, offline access, multilingual support and cloud integration. The updates aim to help users manage their medication schedules more reliably and access important medication information.

## 2. New Features and Improvements

### 2.1 Medication Management
- Added functionality to add and manage medication information.
- Improved medication editing and deletion.
- Connected medication management to the existing ASP.NET Core REST API.
- Added local Room database storage to maintain medication information on the device.

### 2.2 Automated Medication Reminders
- Added scheduled medication reminders using Android AlarmManager.
- Implemented daily recurring reminders.
- Added notification preferences and reminder sound settings.
- Improved reminder scheduling after editing or deleting medication.
- Added automatic reminder restoration after restarting the device.

### 2.3 Offline Access
- Implemented local medication caching using Room.
- Enabled users to access previously saved medication information without an internet connection.
- Improved the application's ability to retain medication data when connectivity is unavailable.

### 2.4 Multilingual Support
- Added support for three languages:
  - English
  - Xitsonga
  - isiZulu
- Improved accessibility for users who prefer using the application in different supported languages.

### 2.5 Cloud Integration and Data Security
- Integrated Azure Blob Storage for file uploads.
- Integrated Azure Cosmos DB for NoSQL user settings storage.
- Connected the Android application to the existing ASP.NET Core REST API.
- Retained user registration and login functionality.
- Used BCrypt password hashing in the API to protect stored passwords.

### 2.6 Application Reliability
- Improved application functionality through testing and debugging.
- Added support for Android notification permissions.
- Improved reminder behaviour when notification permissions are unavailable.
- Updated the application for compatibility with the configured Android SDK.

## 3. Testing and Verification

The following functionality was tested during development:

- Medication registration and management.
- Medication editing and deletion.
- Medication reminders and notifications.
- Daily reminder scheduling.
- Reminder restoration after device restart.
- Offline access to cached medication information.
- Language selection.
- Azure Blob Storage file upload.
- Android application build and release preparation.

## 4. Release Preparation

- Successfully generated a signed Android App Bundle (`.aab`) using Android Studio.
- Prepared the application for release distribution.
- Prepared the project for GitHub submission.
- Continued preparation of the signed APK for direct installation and testing.

## 5. Known Limitations

- Daily recurring reminders are supported by the current reminder implementation.
- Previously cached medication information is available offline; cloud-dependent features require an internet connection.
- Google Play Store publication has not been completed. The signed App Bundle is available for future publication, subject to the required developer account registration.

## 6. Conclusion

Version 1.0.0 represents a significant improvement to MediCareReminder. The application now provides medication management, automated reminders, offline access, multilingual support and cloud integration. These enhancements improve the application's usability, accessibility and reliability while preparing it for future distribution.

**Developer:** Cynthia Mokasi  
**Project:** MediCareReminder  
**Release Version:** 1.0.0
