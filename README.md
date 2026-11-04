# 💊 MediCareReminder

MediCareReminder is an Android medication reminder application designed to help users manage their medications, dosages, frequencies and reminder times.

The application uses a RESTful API and database to securely store user accounts and medication information. It also provides medication reminders and notification functionality to support users in taking their medication on time.

---

## 📱 Project Overview

MediCareReminder was developed as an Android application integrated with an ASP.NET Core REST API.

The system consists of three main components:

* 📱 Android mobile application
* 🌐 ASP.NET Core REST API
* 🗄️ Database

The Android application communicates with the REST API using HTTP requests. The API processes authentication and medication-related requests and communicates with the database to store and retrieve information.

---

## 🎯 Objectives

The main objectives of MediCareReminder are to:

* Allow users to create an account.
* Allow registered users to log in securely.
* Protect user passwords using password hashing.
* Allow users to add medication information.
* Store medication information using a database.
* Display saved medications.
* Allow users to specify dosage, frequency and reminder time.
* Provide medication reminders and notifications.
* Provide application settings.
* Demonstrate the use of a RESTful API in an Android application.
* Demonstrate database integration.
* Demonstrate automated software testing.
* Demonstrate GitHub Actions for continuous integration.

---

## ✨ Features

### 👤 User Registration

Users can create an account by providing:

* Full Name
* Email Address
* Password
* Confirm Password

Passwords are not stored as plain text. They are securely hashed before being stored in the database.

---

### 🔐 User Login

Registered users can log into the application using their email address and password.

The application communicates with the REST API to authenticate the user.

---

### 💊 Medication Management

Users can add medication information including:

* Medication Name
* Dosage
* Frequency
* Reminder Time

Example:

```text
Medication Name: Panado
Dosage: 500 mg
Frequency: Every day
Reminder Time: 08:00 AM
```

Saved medications can be displayed in the medication section of the application.

---

### ⏰ Medication Reminders

The application supports medication reminders based on the reminder time configured by the user.

Notifications can be used to remind users when it is time to take their medication.

---

### ⚙️ Settings

The application includes a Settings section where users can manage available application preferences.

Depending on the implemented version, settings may include:

* Notification preferences
* Reminder sound
* Logout
* Navigation back to the previous screen

---

## 🏗️ Architecture

MediCareReminder follows a client-server architecture.

```text
┌─────────────────────────────┐
│      Android Application    │
│                             │
│  Login                      │
│  Registration               │
│  Dashboard                  │
│  Medications                │
│  Add Medication             │
│  Settings                   │
│  Notifications              │
└──────────────┬──────────────┘
               │
               │ REST API
               │ HTTP Requests
               ▼
┌─────────────────────────────┐
│     ASP.NET Core Web API    │
│                             │
│  Authentication             │
│  User Management            │
│  Medication Management      │
│  API Endpoints              │
└──────────────┬──────────────┘
               │
               │ Entity Framework Core
               ▼
┌─────────────────────────────┐
│          Database           │
│                             │
│  Users                      │
│  Medications                │
└─────────────────────────────┘
```

---

# 📱 Android Application

The Android application was developed using Android Studio and Kotlin.

### Main Android components

The application contains screens such as:

* Login
* Registration
* Dashboard
* Medications
* Add Medication
* Settings

The Android application communicates with the REST API to perform operations such as authentication and medication management.

### Android Technologies

* Kotlin
* Android Studio
* Android SDK
* XML layouts
* Material Design
* Retrofit
* Gson
* Android notifications
* REST API integration

---

# 🌐 REST API

The backend was developed using ASP.NET Core Web API.

The API provides endpoints that allow the Android application to communicate with the backend server.

### Main API functionality

The API handles:

* User registration
* User login
* Medication creation
* Medication retrieval
* User-related data
* Database communication

### Technologies

* ASP.NET Core
* C#
* Entity Framework Core
* RESTful API
* Swagger
* BCrypt.Net-Next

Swagger can be used during development to test and inspect API endpoints.

---

# 🗄️ Database

The application uses a relational database to store user and medication information.

The planned database is:

```text
MediCareReminderDB
```

### Users Table

The Users table contains information such as:

```text
UserId
FullName
Email
PasswordHash
CreatedAt
```

### Medications Table

The Medications table contains:

```text
MedicationId
UserId
Name
Dosage
Frequency
ReminderTime
```

The relationship between the tables allows medications to be associated with the user who created them.

---

# 🔐 Password Hashing

Password security is an important part of the application.

User passwords are not stored directly in the database.

Instead, passwords are processed using a secure hashing mechanism before being stored.

The project uses:

```text
BCrypt.Net-Next
```

During login, the supplied password is checked against the stored password hash.

This improves the security of user authentication and prevents plain-text passwords from being stored.

---

# 🔄 REST API Communication

The Android application uses Retrofit to communicate with the REST API.

The general communication process is:

```text
Android App
     │
     │ HTTP Request
     ▼
REST API
     │
     │ Database Request
     ▼
Database
     │
     │ Response
     ▼
REST API
     │
     │ JSON Response
     ▼
Android App
```

JSON is used to exchange data between the Android application and the REST API.

---

# 🧪 Automated Testing

Automated testing was included as part of the project development process.

Testing helps identify errors and verify that important parts of the application continue to work correctly after changes are made.

The project includes automated tests that can be executed as part of the development workflow.

Testing can be performed before changes are merged or considered complete.

---

# ⚙️ GitHub Actions

GitHub Actions is used to automate parts of the project's development workflow.

The repository contains GitHub Actions workflow configuration under:

```text
.github/workflows/
```

The workflow can be used to automatically build and/or test the project when changes are pushed to GitHub.

### Continuous Integration Workflow

The general workflow is:

```text
Developer
    │
    │ Push changes
    ▼
GitHub Repository
    │
    ▼
GitHub Actions
    │
    ├── Build
    │
    ├── Run Tests
    │
    └── Report Result
```

This helps detect problems early and provides evidence that the project can be built and tested automatically.

---

# 📸 Screenshots

Screenshots will be added to this section to demonstrate the completed application.

### Login Screen

<img width="3024" height="4032" alt="WhatsApp Image 2026-09-22 at 15 35 53" src="https://github.com/user-attachments/assets/69e4ce4e-aa4e-4e1e-879d-52e827521176" />


### Registration Screen

<img width="3024" height="4032" alt="WhatsApp Image 2026-09-22 at 15 35 53 (1)" src="https://github.com/user-attachments/assets/f05f2d31-4d8a-4e20-aae6-f7570c283f74" />


### Dashboard

<img width="3024" height="4032" alt="WhatsApp Image 2026-09-22 at 15 35 54 (2)" src="https://github.com/user-attachments/assets/20b0f5ad-a3d8-4736-846c-bcc2e11e0dec" />


### Add Medication

<img width="3024" height="4032" alt="WhatsApp Image 2026-09-22 at 15 35 55" src="https://github.com/user-attachments/assets/efe18224-5f2a-4b4d-8b17-7dba1922687b" />


### Medications

<img width="3024" height="4032" alt="WhatsApp Image 2026-09-22 at 15 35 54 (1)" src="https://github.com/user-attachments/assets/08b52bd9-d80d-450f-a29f-207db4eee5c7" />


### Settings and Medication Reminder


<img width="3024" height="4032" alt="WhatsApp Image 2026-09-22 at 15 35 54" src="https://github.com/user-attachments/assets/bca77270-4883-4f57-a72c-b8cfdae4ecef" />


### APK Release App
Note that my App release APK is inside Android/MedicareReminder

NB: IF YOU WANT TO DOWNLOAD THE APP PLESE USE MY SCHOOL EMAIL AND PASSWORD:
EMAIL:st10245513@rcconnect.edu.za
Password:Nhlelo@123

# 🎥 Video Demonstration

A video demonstration of the MediCareReminder application will demonstrate the main functionality of the system.

The demonstration includes:

1. Launching the application.
2. Registering a new user.
3. Logging into the application.
4. Navigating to the dashboard.
5. Adding a medication.
6. Viewing the saved medication.
7. Demonstrating the reminder/notification functionality.
8. Opening Settings.
9. Demonstrating the main application workflow.

### Video Link

**Demo Video:**
https://youtu.be/l_RdtDnnjE4


# 🤖 AI-Use Statement

Artificial Intelligence tools were used during the development process as a supporting learning and development resource.

AI assistance was used for activities such as:

* Understanding programming concepts.
* Troubleshooting development errors.
* Understanding Android and ASP.NET Core concepts.
* Improving code structure.
* Generating suggestions for debugging.
* Understanding REST API integration.
* Improving documentation.

The developer remained responsible for implementing, testing, reviewing and understanding the project.

AI-generated suggestions were reviewed and adapted where necessary to suit the requirements of the MediCareReminder application.

---

# 🚀 Installation and Setup

## Prerequisites

Before running the project, install the following:

### Android Development

* Android Studio
* Android SDK
* Kotlin
* Android Emulator or Android device

### Backend Development

* Visual Studio
* .NET SDK
* ASP.NET Core
* SQL Server LocalDB or compatible SQL Server installation

### Version Control

* GitHub account
* Git

---

# 📱 Android Setup

### Step 1 — Clone the Repository

Clone the MediCareReminder repository from GitHub.

```bash
git clone <YOUR-GITHUB-REPOSITORY-LINK>
```

### Step 2 — Open Android Project

Open the Android project in Android Studio.

### Step 3 — Sync Gradle

Allow Android Studio to download and configure the required dependencies.

### Step 4 — Configure API URL

Make sure the Android application points to the correct REST API address.

For local development, the API may use an address similar to:

```text
http://10.0.2.2:5071
```

For an Android emulator, `10.0.2.2` can be used to access the host computer's localhost.

### Step 5 — Run Application

Start an Android emulator and click:

```text
Run ▶
```

The MediCareReminder application should launch on the emulator.

---

# 🌐 REST API Setup

### Step 1 — Open the API Project

Open:

```text
MediCareReminderAPI
```

in Visual Studio.

### Step 2 — Restore Packages

Restore the required NuGet packages.

The project uses technologies including:

* Entity Framework Core
* BCrypt.Net-Next
* ASP.NET Core Web API

### Step 3 — Configure Database

Configure the database connection string for:

```text
MediCareReminderDB
```

### Step 4 — Create/Update Database

Run the required Entity Framework Core migrations if migrations are being used.

### Step 5 — Run the API

Start the ASP.NET Core Web API from Visual Studio.

Swagger can be used to verify the available API endpoints.

---

# 🔗 Connecting Android to the API

When running the API locally with the Android Emulator, the Android application should use the appropriate emulator address rather than:

```text
localhost
```

For example:

```text
http://10.0.2.2:5071
```

The exact URL should match the port configured by the ASP.NET Core API.

---

# 📂 Project Structure

A simplified project structure is:

```text
MediCareReminder/
│
├── Android/
│   └── MediCareReminder/
│       ├── app/
│       ├── gradle/
│       └── settings.gradle.kts
│
├── MediCareReminderAPI/
│   ├── Controllers/
│   ├── Models/
│   ├── Data/
│   ├── Program.cs
│   └── appsettings.json
│
├── Tests/
│
├── .github/
│   └── workflows/
│
├── screenshots/
│
└── README.md
```

The exact structure may differ depending on the final repository organisation.

---

# 🧪 Testing Checklist

Before final submission, the following functionality should be tested:

* [ ] Application launches successfully.
* [ ] User can register.
* [ ] User can log in.
* [ ] Incorrect login credentials are handled.
* [ ] User can access the dashboard.
* [ ] User can add medication.
* [ ] Medication is saved successfully.
* [ ] Saved medication can be displayed.
* [ ] Medication reminder works.
* [ ] Notification functionality works.
* [ ] Settings screen works.
* [ ] Logout works.
* [ ] REST API starts successfully.
* [ ] Database connection works.
* [ ] Automated tests pass.
* [ ] GitHub Actions workflow completes successfully.

---

# 🔒 Security Considerations

The project applies basic security practices including:

* Password hashing using BCrypt.
* Validation of user input.
* Separation between the Android client and backend API.
* Database storage for application data.
* Authentication through the REST API.

For a production application, additional security measures such as HTTPS, secure token-based authentication, stronger API protection and secure secret management should also be implemented.

---

# 📚 Technologies Used

| Technology            | Purpose                         |
| --------------------- | ------------------------------- |
| Kotlin                | Android application development |
| Android Studio        | Android development environment |
| XML                   | Android UI layouts              |
| ASP.NET Core          | REST API                        |
| C#                    | Backend programming             |
| Entity Framework Core | Database access                 |
| SQL Server / LocalDB  | Database                        |
| BCrypt.Net-Next       | Password hashing                |
| Retrofit              | Android API communication       |
| Gson                  | JSON conversion                 |
| Swagger               | API testing/documentation       |
| GitHub                | Source control                  |
| GitHub Actions        | Automated CI                    |
| Android Notifications | Medication reminders            |

---

# 👩‍💻 Developer

**Cynthia Mokasi**

Diploma in Software Development

Rosebank College

MediCareReminder — Android Medication Reminder Application

---

# 📄 Project Status

**Status:** Completed / Final Testing

The project demonstrates an Android medication reminder application integrated with a RESTful backend API and database.

Future improvements could include:

* Push notifications.
* Cloud database hosting.
* User profile management.
* Medication editing and deletion.
* Multiple reminder times per medication.
* Improved authentication using JWT.
* Cloud deployment.
* Additional automated tests.

---

## ⭐ Conclusion

MediCareReminder demonstrates the development of a mobile application that integrates Android, Kotlin, RESTful web services, database technology, authentication, password hashing, automated testing and GitHub Actions.

The project provides a foundation for a medication management system while demonstrating practical software development and integration skills.


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
