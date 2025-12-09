# Lịch Vạn Niên - Flutter Frontend

Vietnamese Lunar Calendar application with horoscope features.

## 🚀 Getting Started

### Prerequisites

- Flutter SDK ^3.10.1
- Dart SDK ^3.10.1
- Android Studio / VS Code with Flutter extension

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd fe_lich_van_nien
   ```

2. **Create environment file**
   ```bash
   # Copy the example file
   cp .env.example .env.dev
   
   # Edit with your configuration
   # BASE_URL=http://localhost:8080/api/v1
   ```

3. **Install dependencies**
   ```bash
   flutter pub get
   ```

4. **Run the app**
   ```bash
   # Development
   flutter run
   
   # With specific device
   flutter run -d <device_id>
   ```

## 📁 Project Structure

```
lib/
├── main.dart                    # App entry point
├── routes/                      # Navigation configuration
│   └── app_router.dart
├── core/                        # Core utilities & config
│   ├── config/                  # Environment configuration
│   ├── constants/               # API endpoints, breakpoints
│   ├── di/                      # Dependency injection (providers)
│   ├── network/                 # HTTP client, error handling
│   ├── theme/                   # Colors, typography, spacing
│   └── utils/                   # Date/time, responsive utilities
├── common_widgets/              # Shared widgets
│   ├── async/                   # Loading, error states
│   ├── calendar/                # Calendar components
│   └── layout/                  # Navigation, shells
└── features/                    # Feature modules
    ├── calendar/                # Calendar feature
    │   ├── application/         # State management (providers)
    │   ├── data/                # Repository, API calls
    │   ├── domain/              # Models, entities
    │   └── presentation/        # Screens, widgets
    ├── horoscope/               # Horoscope feature
    │   ├── data/                # Repository
    │   └── domain/              # Models
    ├── culture/                 # Vietnamese culture
    ├── explore/                 # Explore features
    └── notes/                   # Notes feature
```

## 🔧 Configuration

### Environment Files

| File | Description |
|------|-------------|
| `.env.dev` | Development environment |
| `.env.stg` | Staging environment |
| `.env.prod` | Production environment |

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `BASE_URL` | API base URL | `http://localhost:8080/api/v1` |
| `ENV_NAME` | Environment name | `development` |

## 🌐 API Endpoints

The app communicates with the backend API:

| Endpoint | Description |
|----------|-------------|
| `/calendar/day` | Get daily calendar info |
| `/calendar/month` | Get monthly calendar |
| `/horoscope/daily` | Daily horoscope |
| `/horoscope/monthly` | Monthly horoscope |
| `/horoscope/yearly` | Yearly horoscope |
| `/horoscope/lifetime` | Lifetime horoscope by Can-Chi |
| `/horoscope/lifetime/by-birth` | Lifetime horoscope by birth data |
| `/horoscope/can-chi` | Calculate Can-Chi from date |

## 📱 Features

- **Daily View**: Solar & lunar date, Can-Chi, golden hours
- **Month View**: Calendar grid with lunar dates, special days
- **Horoscope**: Daily, monthly, yearly, lifetime predictions
- **Vietnamese Culture**: Traditional knowledge & customs
- **Notes**: Personal reminders and notes

## 🧪 Testing

```bash
# Run all tests
flutter test

# Run with coverage
flutter test --coverage
```

## 📦 Building

```bash
# Android APK
flutter build apk --release

# Android App Bundle
flutter build appbundle --release

# iOS
flutter build ios --release
```

## 🛠 Development

### Code Style

- Follow [Effective Dart](https://dart.dev/guides/language/effective-dart)
- Use `flutter analyze` before committing

### State Management

Using [Riverpod](https://riverpod.dev/) for state management:
- `StateProvider` for simple state
- `FutureProvider` for async data
- `StateNotifier` for complex state logic

### Timezone

All dates are normalized to **Asia/Bangkok (UTC+7)** timezone for consistency with the backend.

## 📄 License

Private - All rights reserved
