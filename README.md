# Campus Safety

A mobile incident reporting application for Nigerian universities that enables students to report safety incidents in real time with GPS location detection, and automatically alerts the nearest available security personnel.

Built as a final year project at Bingham University, Karu, Nasarawa State — by Caleb Joshua Fuojima.

---

## Screenshots

> Add screenshots here after first build

---

## Features

- **Real-time incident reporting** — students submit reports instantly from their mobile device
- **GPS location detection** — automatically captures the student's coordinates when reporting
- **Proximity-based alerts** — routes reports to the nearest security officer via Firestore real-time updates
- **Role-based access** — students and security officers see different interfaces after login
- **Incident management** — security officers can acknowledge and resolve incidents from their dashboard
- **Incident history** — students can view all past reports with search and filter support
- **Session persistence** — app remembers logged-in users across restarts via Firebase Auth

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | Clean Architecture + Multi-Module |
| Dependency Injection | Hilt |
| Navigation | Jetpack Navigation Compose |
| Backend | Firebase Auth + Firestore |
| Location | Google Play Services Location |
| Async | Kotlin Coroutines + Flow |
| Build System | Gradle (Kotlin DSL) + Version Catalog |

---

## Architecture

The project follows **Clean Architecture** with a **multi-module** structure. Each feature is broken into three independent modules:

```
CampusSafety/
├── app/                          → entry point, navigation, splash
├── build-logic/
│   └── convention/               → shared Gradle convention plugins
├── core/
│   ├── common/                   → shared utilities and constants
│   ├── ui/                       → shared Compose components and theme
│   └── network/                  → Firebase setup and network config
├── auth/
│   ├── domain/                   → models, repository interface, use cases
│   ├── data/                     → Firebase Auth + Firestore implementation
│   └── presentation/             → Login and Register screens + ViewModels
├── report/
│   ├── domain/                   → models, repository interface, use cases
│   ├── data/                     → Firestore + GPS implementation
│   └── presentation/             → Report, Home, History, Detail screens
└── dashboard/
    └── presentation/             → Security dashboard screen + ViewModel
```

### Layer responsibilities

- **Domain** — pure Kotlin, no Android, no Firebase. Contains models, repository interfaces and use cases
- **Data** — implements repository interfaces using Firebase and GPS. Contains Hilt DI modules
- **Presentation** — Compose screens and ViewModels. Talks only to use cases, never to Firebase directly

---

## Module Dependency Graph

```
app
 ├── auth:presentation
 ├── auth:data
 ├── auth:domain
 ├── report:presentation
 ├── report:data
 ├── report:domain
 ├── dashboard:presentation
 ├── core:ui
 ├── core:common
 └── core:network

auth:presentation → auth:domain, core:ui, core:common
auth:data        → auth:domain, core:common, core:network
report:presentation → report:domain, auth:domain, core:ui, core:common
report:data      → report:domain, core:common, core:network
dashboard:presentation → report:domain, report:presentation, core:ui, core:common
```

---

## Getting Started

### Prerequisites

- Android Studio Narwhal (2025.1.x) or later
- JDK 17
- Android device or emulator running API 26+
- A Firebase project with Auth and Firestore enabled

### Setup

1. Clone the repository

```bash
git clone https://github.com/yourusername/CampusSafety.git
cd CampusSafety
```

2. Add your `google-services.json` file to the `app/` directory. Download it from your Firebase console under Project Settings → Your Apps

3. Enable the following in your Firebase console:
   - Authentication → Email/Password sign-in
   - Firestore Database → Start in test mode

4. Open the project in Android Studio and let Gradle sync

5. Run the app on a device or emulator

---

## Firebase Firestore Structure

```
users/
  {userId}/
    email: String
    fullName: String
    role: "STUDENT" | "SECURITY"
    matricNumber: String?   // students only
    badgeNumber: String?    // security only

incidents/
  {incidentId}/
    reporterId: String
    reporterName: String
    category: String        // THEFT | HARASSMENT | ASSAULT | MEDICAL_EMERGENCY | FIRE | SUSPICIOUS_ACTIVITY | VANDALISM | OTHER
    description: String
    location/
      latitude: Double
      longitude: Double
      address: String
    status: String          // PENDING | ACKNOWLEDGED | RESOLVED
    timestamp: Long
    assignedSecurityId: String?
```

---

## User Flows

### Student
```
Register → Student Home → Report Incident → Submit
                       → View History → Filter/Search → Incident Detail
```

### Security Officer
```
Register → Security Dashboard → Filter by Status
                              → Acknowledge Incident
                              → Resolve Incident
                              → Incident Detail
```

---

## Screens

| Screen | Role | Description |
|---|---|---|
| Splash | All | Checks session and routes to correct home |
| Login | All | Email and password authentication |
| Register | All | Account creation with role selection |
| Student Home | Student | Dashboard with report button and recent incidents |
| Report Incident | Student | Form with GPS, category selector and description |
| Incident History | Student | Full list with search and status filters |
| Incident Detail | All | Full incident info with actions for security |
| Security Dashboard | Security | Real-time feed of all incidents with stats |

---

## Security Rules (Firestore)

For production, replace test mode with proper Firestore rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /incidents/{incidentId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null;
    }
  }
}
```

---

## Roadmap

- [ ] Push notifications via Firebase Cloud Messaging
- [ ] Map view showing incident locations
- [ ] Photo attachments on incident reports
- [ ] Offline support with Room local caching
- [ ] Admin panel for university management
- [ ] Dark mode support

---


## License

This project is submitted for academic purposes. All rights reserved by the author.
