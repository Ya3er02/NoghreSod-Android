# NoghreSod Android App

Complete native Android application for the NoghreSod marketplace platform, built with Kotlin, Jetpack Compose, and modern Android architecture.

## Features

- ✅ User Authentication (Login/Register)
- ✅ Product Browsing & Search
- ✅ User Profile Management
- ✅ Order Management
- ✅ Dark/Light Theme Support
- ✅ Offline Support with Local Caching
- ✅ Real-time Notifications
- ✅ Multi-language Support (Ready)
- ✅ RTL Layout Support (Persian)

## Tech Stack

### Architecture
- **MVVM**: Model-View-ViewModel pattern
- **Clean Architecture**: Separation of concerns
- **Repository Pattern**: Abstracted data layer

### UI Framework
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material Design 3**: Latest Google design system
- **Navigation Compose**: Type-safe navigation

### Dependency Injection
- **Hilt**: Simplified dependency injection for Android

### Networking
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: HTTP interceptor for logging and auth
- **Kotlinx Serialization**: JSON serialization/deserialization

### Local Storage
- **Room**: SQLite abstraction layer
- **DataStore**: Preferences management

### Others
- **Kotlin Coroutines**: Async programming
- **Flow**: Reactive data streams
- **Timber**: Logging
- **Coil**: Image loading and caching

## Project Structure

```
app/src/main/
├── kotlin/com/noghre/sod/
│   ├── di/                      # Dependency Injection modules
│   ├── data/
│   │   ├── local/               # Local database & preferences
│   │   ├── remote/              # API service & DTOs
│   │   └── repository/          # Data repositories
│   ├── domain/                  # Business logic & models
│   ├── presentation/
│   │   ├── navigation/          # Navigation setup
│   │   ├── screen/              # UI screens
│   │   ├── viewmodel/           # ViewModels
│   │   └── component/           # Reusable components
│   ├── ui/theme/                # Theme & styling
│   ├── MainActivity.kt
│   └── NoghreSodApp.kt          # Application class
└── res/                         # Resources
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+
- Kotlin 1.9.10+
- Java 17+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Ya3er02/NoghreSod-Android.git
   cd NoghreSod-Android
   ```

2. **Open in Android Studio**
   ```bash
   android-studio .
   ```

3. **Configure API Base URL**
   - Edit `di/NetworkModule.kt`
   - Update the `baseUrl` to your API endpoint:
   ```kotlin
   .baseUrl("https://your-api.com/")
   ```

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run on emulator or device**
   ```bash
   ./gradlew installDebug
   ```

## API Configuration

The app expects the backend API at `https://api.noghre-sod.com/`. Adjust this in `NetworkModule.kt` to match your backend server.

### Expected Endpoints

- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `GET /api/v1/products` - List products
- `GET /api/v1/products/{id}` - Get product details
- `GET /api/v1/users/profile` - Get user profile

## Build & Deployment

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Generate Signed APK
```bash
./gradlew bundleRelease
```

## Key Components

### Authentication
- `AuthViewModel` - Manages login/register state
- `AuthRepository` - Handles auth API calls
- `PreferencesManager` - Stores auth tokens securely

### Product Management
- `ProductViewModel` - Manages product listing state
- `ProductRepository` - Handles product API calls
- Product screens with pagination and search

### User Profile
- Profile viewing and editing
- User preference management
- Theme switching

## Development Guidelines

### Code Style
- Follow Kotlin conventions
- Use meaningful variable names
- Document complex logic with comments
- Keep functions small and focused

### Testing
- Unit tests for ViewModels
- Integration tests for Repositories
- UI tests with Compose test rules

### Performance
- Use lazy loading for lists
- Cache images and API responses
- Minimize recompositions in Compose
- Profile with Android Profiler

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Version History

### v1.0.0 (Initial Release)
- Authentication system
- Product browsing
- User profile management
- Order management
- Dark/Light themes

## Troubleshooting

### Build Issues
- Clear cache: `./gradlew clean`
- Invalidate caches: Android Studio > File > Invalidate Caches
- Update gradle: `./gradlew wrapper --gradle-version 8.1.1`

### Runtime Issues
- Check API URL configuration
- Verify network connectivity
- Review Logcat for detailed errors
- Check Timber logs in debug builds

## License

This project is part of the NoghreSod ecosystem. See main repository for license information.

## Contact & Support

- GitHub Issues: [Report bugs](https://github.com/Ya3er02/NoghreSod-Android/issues)
- Discussions: [Ask questions](https://github.com/Ya3er02/NoghreSod-Android/discussions)
- Main Project: [NoghreSod](https://github.com/Ya3er02/NoghreSod)

## Roadmap

- [ ] Push notifications (FCM)
- [ ] Advanced search filters
- [ ] Wishlist feature
- [ ] Payment integration
- [ ] Order tracking
- [ ] Reviews and ratings
- [ ] Chat messaging
- [ ] Widget support
- [ ] Offline-first mode
- [ ] Analytics integration

---

**Built with ❤️ using Kotlin & Jetpack Compose**
