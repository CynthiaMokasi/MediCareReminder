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

![Login Screen](screenshots/login.png)

### Registration Screen

![Registration Screen](screenshots/register.png)

### Dashboard

![Dashboard](screenshots/dashboard.png)

### Add Medication

![Add Medication](screenshots/add-medication.png)

### Medications

![Medications](screenshots/medications.png)

### Settings

![Settings](screenshots/settings.png)

### Medication Reminder

![Medication Reminder](screenshots/reminder.png)

> Replace the image paths above with the actual screenshot filenames after adding the screenshots to the repository.

---

# 🎥 Video Demonstration

A video demonstration of the MediCareReminder application will demonstrate the main functionality of the system.

The demonstration should include:

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
*Add the final video link here.*

---

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
