# BinBuddy

## Description
BinBuddy is a user-friendly Android application designed to promote environmental sustainability through educational content and help users manage their waste disposal schedule. The app serves two main purposes: providing informative content about environmental sustainability and offering a practical trash pickup reminder system. With BinBuddy, users can stay informed about environmental issues while never missing their scheduled trash collection days.

## Features
- Trash Pickup Reminders
  - Set customizable reminders for trash collection days
  - Configure specific times and dates (e.g., every Tuesday and Friday at 09:00)
  - Receive notifications before scheduled pickup times
  - Multiple reminder settings for different types of waste collection

- Environmental Education Hub
  - Access comprehensive articles about environmental sustainability
  - Learn about proper waste management practices
  - Read tips for reducing environmental impact
  - Stay updated with current environmental issues and solutions

## Prerequisites
- Android Studio Arctic Fox (2020.3.1) or newer
- Kotlin 1.5.0 or newer
- Minimum SDK: API 21 (Android 5.0)
- Target SDK: API 33 (Android 13)

## Getting Started

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/faizjauzah/binbuddy-expo.git
   ```

2. Open Android Studio
3. Select "Open an Existing Project"
4. Navigate to the cloned repository and click "OK"
5. Wait for the project to build and sync

### Configuration
1. Add any required API keys in `local.properties`:
   ```properties
   api.key=your_api_key_here
   ```

2. Sync project with Gradle files

## Project Structure
```
app/
├── manifests/
│   └── AndroidManifest.xml
├── kotlin+java/
│   └── com.example.expopab/
│       ├── model/
│       │   └── EducationalContent
│       ├── notification/
│       │   ├── NotificationHelper
│       │   └── TrashReminderWorker
│       ├── repository/
│       │   ├── AuthRepository
│       │   └── EducationalContentRepository
│       ├── ui/
│       │   ├── article/
│       │   │   └── ArticleDetailActivity
│       │   ├── auth/
│       │   │   ├── SettingsActivity
│       │   │   ├── SignInActivity
│       │   │   ├── SignOutActivity
│       │   │   ├── SignUpActivity
│       │   │   └── WelcomeActivity
│       │   └── home/
│       │       ├── adapter/
│       │       │   ├── EducationalContentAdapter
│       │       │   └── ReminderAdapter
│       │       └── data/
│       │           ├── ReminderTime
│       │           ├── AccountFragment
│       │           ├── EducationFragment
│       │           ├── EducationUIState
│       │           ├── HomeFragment
│       │           ├── ReminderFragment
│       │           └── TrackingFragment
│       ├── utils/
│       │   └── ValidationsUtils.kt
│       └── viewmodel/
│       │   ├── AuthViewModel
│       │   └── EducationViewModel
│       └── MainActivity
└── res/
    ├── drawable/
    │   ├── bg_button.xml
    │   ├── bg_day_selector.xml
    │   ├── bg_educontent.png
    │   ├── bg_home.xml
    │   ├── bg_item_reminder.xml
    │   ├── bg_profile.xml
    │   ├── ic_account.png
    │   ├── ic_back.xml
    │   ├── ic_education.png
    │   ├── ic_home.png
    │   ├── ic_tracking.png
    │   ├── img_sign_out.png
    │   ├── logo_binbuddy.png
    │   └── red_circle.png
    ├── layout/
    │   ├── activity_article_detail.xml
    │   ├── activity_main.xml
    │   ├── activity_settings.xml
    │   ├── activity_sign_in.xml
    │   ├── activity_sign_out.xml
    │   ├── activity_sign_up.xml
    │   ├── activity_welcome.xml
    │   ├── fragment_account.xml
    │   ├── fragment_education.xml
    │   ├── fragment_home.xml
    │   ├── fragment_reminder.xml
    │   ├── fragment_tracking.xml
    │   ├── item_educational_content.xml
    │   └── item_reminder.xml
    ├── menu/
    │   └── bottom_nav_menu.xml
    ├── mipmap/
    │   ├── ic_launcher/
    │   └── ic_launcher_round/
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── xml/
        ├── backup_rules.xml
        └── data_extraction_rules.xml
```

## Architecture
This project follows the MVVM (Model-View-ViewModel) architecture pattern and uses the following components:
- ViewModel: Manages UI-related data
- LiveData: Handles data observation
- Room: Local database storage
- Retrofit: Network operations
- Dependency Injection: Hilt/Dagger

## Libraries Used
- [AndroidX](https://developer.android.com/jetpack/androidx) - Core Android components
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Asynchronous programming
- [Retrofit](https://square.github.io/retrofit/) - HTTP client
- [Room](https://developer.android.com/training/data-storage/room) - Database
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Dependency injection
- [Navigation Component](https://developer.android.com/guide/navigation) - Navigation
- [Glide](https://github.com/bumptech/glide) - Image loading

## Common Issues and Solutions

### Build Issues
If you encounter build issues:
1. Clean project (Build > Clean Project)
2. Invalidate caches (File > Invalidate Caches > Invalidate and Restart)
3. Sync project with Gradle files

### Error: SDK Location Not Found
1. Create a `local.properties` file in the project root
2. Add your SDK location:
   ```properties
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   ```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## Testing
The project includes both unit tests and instrumented (UI) tests:

### Running Unit Tests
```bash
./gradlew test
```

### Running Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Build and Deploy
To build a release version:
1. Update `versionCode` and `versionName` in `app/build.gradle`
2. Create signing configuration in Android Studio
3. Build > Generate Signed Bundle / APK

## Contact
Muhammad Faiz Jauzah - jauzahfaiz@gmail.com
Project Link: https://github.com/faizjauzah/binbuddy-expo
