# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**eMaliEstates** is a Kotlin Multiplatform Mobile (KMM) application built with Jetpack Compose Multiplatform that targets both Android and iOS. The app is a real estate/property listing application with authentication, property browsing, user management, and location-based features.

**Tech Stack:**
- Kotlin 2.2.0
- Compose Multiplatform 1.9.1
- Koin 4.1.0 for dependency injection
- Ktor 3.3.3 for HTTP networking
- SQLDelight 2.1.0 for local database
- Coil 3.3.0 for image loading
- KMAuth 0.3.1 for Google/Supabase authentication
- Navigation Compose 2.9.1 for routing
- BuildKonfig 0.15.1 for build-time configuration
- multiplatform-settings 1.3.0 for key-value storage
- kotlinx-datetime 0.7.1 for date/time operations

## Architecture

The project follows a **layered, feature-based architecture** with clear separation of concerns:

```
composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/
├── App.kt                          # Main entry point: KMAuth, Coil, theme, navigation
├── navigation/                     # Navigation graph and routes
│   ├── AppNavGraph.kt             # Main navigation host with two nested graphs
│   ├── Graph.kt                   # Route constants (AUTHENTICATION, BASE)
│   ├── AuthScreen.kt              # Auth flow routes
│   └── BaseScreen.kt              # Main app routes (home, map, tours, search, profile)
├── domain/                        # Domain models and business logic
│   ├── auth/                      # Auth-specific models (LoginRequest, SignupRequest, User, etc.)
│   ├── Property.kt, Country.kt   # Core domain models
│   ├── RequestState.kt           # Sealed class for UI state (Idle, Loading, Success, Error)
│   └── [Other response types]
├── data/                          # Data layer
│   ├── local/                     # SQLDelight database
│   │   ├── DatabaseFactory.kt    # Platform-specific driver factory
│   │   └── LocalDatabase.kt      # Database wrapper
│   ├── remote/                    # API clients
│   │   ├── AuthApi.kt             # Auth endpoints
│   │   ├── PropertyApi.kt         # Property endpoints
│   │   ├── CategoryApi.kt         # Category endpoints
│   │   └── HttpClientFactory.kt  # Ktor client creation with auth interceptor
│   ├── repository/                # Repositories (orchestrate data sources)
│   │   ├── AuthRepository.kt      # Auth repo (local + remote)
│   │   ├── PropertyRepository.kt  # Property repo
│   │   └── CategoryRepository.kt  # Category repo
│   ├── AuthSDK.kt                # Auth service wrapper (used by ViewModels)
│   └── PropertySDK.kt            # Property service wrapper
├── di/                           # Dependency injection
│   └── KoinModule.kt            # Koin module: all dependencies + viewModels
├── auth/                        # Authentication feature
│   ├── presentation/            # UI composables
│   │   ├── SigninScreen.kt      # Email/Password + Google Sign-In
│   │   ├── SignupScreen.kt      # Step 1 of signup (basic info)
│   │   ├── Signup2Screen.kt     # Step 2 of signup (additional details)
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── VerifyOtpScreen.kt
│   │   └── ChangePasswordScreen.kt
│   └── viewModel/
│       └── AuthViewModel.kt     # Auth state: login, signup, forgot/verify/reset password
├── home/                       # Home feature
│   ├── presentation/
│   │   ├── MainScreen1.kt       # Main app screen with bottom navigation
│   │   ├── bottomNavigation/    # Bottom nav bar composable
│   │   └── ProfileScreen.kt
│   └── viewModel/
│       └── MainViewModel.kt     # Manages home screen state
├── property/                   # Property feature
│   ├── presentation/
│   │   └── PropertyDetailScreen.kt
│   └── viewModel/
│       └── PropertyDetailViewModel.kt
├── theme/                      # Compose theme
│   ├── Theme.kt
│   ├── Color.kt
│   └── Typography.kt
└── util/                       # Shared utilities
    ├── components/
    │   ├── auth/              # Reusable auth UI components (text fields, buttons)
    │   └── common/            # Common UI components (AppButton, LoadingDialog)
    └── helpers/
        ├── DateHelper.kt
        ├── AppConstants.kt    # BuildKonfig constants (API keys, endpoints)
        └── MessageSerializer.kt
```

**Platform-specific code:**
- `composeApp/src/androidMain/` - Android-specific (drivers, AndroidManifest.xml)
- `composeApp/src/iosMain/` - iOS-specific (Darwin driver)
- `iosApp/` - iOS native entry point (Swift/Xcode project)

**Key Architectural Patterns:**

1. **SDK Pattern**: `*SDK` classes (AuthSDK, PropertySDK) handle all external API calls. They receive `*Api` interfaces and `*Repository` classes, encapsulating business logic and error handling.
2. **Repository Pattern**: Repositories abstract data sources (local DB + remote API). They're injected into SDKs.
3. **State Management**: ViewModels expose `StateFlow<RequestState<T>>` for UI state. `RequestState` is a custom sealed class (Idle, Loading, Success, Error) with helper methods.
4. **Dependency Injection**: Koin is used. `sharedModule` in `di/KoinModule.kt` defines all common dependencies. Platform-specific modules are added via `expect/actual` pattern with `targetModule`.
5. **Navigation**: Two main graphs:
   - `GRAPH_AUTHENTICATION`: Auth screens (signin, signup, signup2, forgot password, verify OTP, change password)
   - `GRAPH_BASE`: Main app with bottom navigation (home, map, tours, search, profile) + property detail

## Common Development Tasks

### Build and Run

**Android:**
```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on connected device/emulator
./gradlew :composeApp:installDebug

# Run from IDE: Use the run configuration in Android Studio/IntelliJ toolbar
```

**iOS:**
```bash
# Open in Xcode and run from there
open iosApp/iosApp.xcodeproj

# Or build from command line (requires Xcode command line tools)
cd iosApp && xcodebuild
```

### Testing

```bash
# Run all common tests
./gradlew :composeApp:commonTest

# Run a single test class (common test)
./gradlew :composeApp:commonTest --tests "com.shortspark.emaliestates.ComposeAppCommonTest"

# Run tests with wildcard pattern
./gradlew :composeApp:commonTest --tests "*AuthViewModel*"

# Run tests with verbose output
./gradlew :composeApp:commonTest --info

# Run tests with stacktrace
./gradlew :composeApp:commonTest --stacktrace
```

**Note:** Currently only common tests are set up. Android instrumented tests (`androidTest`) directory exists but is empty.

### Linting and Formatting

```bash
# Run ktlint (if configured via gradle plugin)
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat
```

### Compilation

```bash
# Compile only the common main Kotlin code (fast check)
./gradlew :composeApp:compileCommonMainKotlinMetadata

# Clean build (all targets)
./gradlew clean
./gradlew :composeApp:assembleDebug

# Skip configuration cache if you suspect caching issues
./gradlew --no-configuration-cache :composeApp:compileCommonMainKotlinMetadata
```

### Gradle Tasks

```bash
# List all tasks
./gradlew tasks

# View dependency tree for a module
./gradlew :composeApp:dependencies

# Generate SQLDelight database classes (after schema changes)
./gradlew :composeApp:sqldelightGeneratePlatformDatabase
```

### Database Operations

```bash
# Check SQLDelight migrations
./gradlew :composeApp:sqldelightVerifyMigration

# Generate database schema from .sq files
./gradlew :composeApp:sqldelightGenerate

# The database schema is in:
# composeApp/src/commonMain/sqldelight/com/shortspark/PropertyDatabase.sq
```

## Configuration

### Build Configuration (BuildKonfig)

Build-time configuration is managed via `local.properties` in the project root. Add your keys:

```properties
webClientId=your_google_oauth_client_id
webClientSecret=your_google_oauth_client_secret
supabaseUrl=your_supabase_url
supabaseKey=your_supabase_anon_key
apiEndpoint=your_backend_api_endpoint
```

These values are accessed in code via `AppConstants.WEB_CLIENT_ID`, etc. (generated by BuildKonfig into `BuildConfig`).

**Note:** Never commit `local.properties` (already in `.gitignore`). Use environment variables or secure credential storage for CI/CD.

### Database

SQLDelight is used for local persistence. The database schema is defined in `.sq` files:
- `composeApp/src/commonMain/sqldelight/com/shortspark/PropertyDatabase.sq`
- `composeApp/src/commonMain/sqldelight/com/shortspark/*.sq` (tables: User, AuthToken, Property, Category, Place, Location, Province, District, Sector, Cell, Village, Reservation, Favorite, Identification, Testimonial, RentalPeriod, Notification, FAQ)

The main database interface is `PropertyDatabase` (generated by SQLDelight).

### Dependency Injection

Koin is used for DI. The `sharedModule` in `di/KoinModule.kt` defines all common dependencies. Platform-specific modules are added via `expect/actual` `targetModule`. `initializeKoin()` starts Koin with both modules.

### Navigation

Navigation is centralized in `navigation/AppNavGraph.kt`. The app uses two main graphs:

1. **Authentication Graph** (`GRAPH_AUTHENTICATION`):
   - SignIn → standard email/password + Google OAuth
   - SignUp → multi-step: SignupScreen (basic info) → Signup2Screen (additional details) → VerifyOtpScreen
   - ForgotPassword → ForgotPasswordScreen → VerifyOtpScreen → ChangePasswordScreen

2. **Base Graph** (`GRAPH_BASE`): Main app with bottom navigation
   - Home (`MainScreen1`) - primary screen
   - Map - placeholder
   - Tours - placeholder
   - Search - placeholder
   - Profile (`ProfileScreen`)
   - PropertyDetail (receives `propertyId` argument)

## Code Style Guidelines

- Use Kotlin coding conventions (official Kotlin style guide)
- Prefer immutable declarations (`val` over `var`)
- Use expression-bodied functions where appropriate
- Follow Compose best practices: state hoisting, remember/derivedStateOf, pass events up, state down
- ViewModels should expose `StateFlow<RequestState<T>>` for async UI state
- Repository pattern: SDKs handle API calls, repositories orchestrate data sources
- Use `RequestState<T>` sealed class for async operations (Success, Error, Loading)
- Network errors should be propagated as `RequestState.Error` with user-friendly messages

## Important Notes

1. **KMP Considerations**: Platform-specific code should be minimal and isolated to `androidMain`/`iosMain`. Use `expect/actual` declarations when platform APIs differ (e.g., `DatabaseDriverFactory`).

2. **Coil Image Loading**: The app configures Coil with Ktor network fetcher in `App.kt` for cross-platform image loading (works on Android and iOS).

3. **Authentication**: 
   - Primary: Google OAuth via KMAuth (enabled)
   - Alternative: Supabase auth (code present but commented out in `App.kt`)
   - Auth flow includes email/password, forgot password, OTP verification
   - `AuthViewModel` handles all auth operations with unified state management

4. **SKIE**: Used for better Kotlin/iOS interop. The iOS framework is built as a static framework (see `build.gradle.kts`).

5. **Testing**: Currently only has a basic common test (`ComposeAppCommonTest`). Unit tests should go in `commonTest`, Android tests in `androidTest` (directory not yet created). Consider adding ViewModel tests and repository tests.

6. **API Layer**: All network calls go through `*SDK` classes (AuthSDK, PropertySDK, CategorySDK implied). They use Ktor with authentication interceptors (Bearer tokens) and error handling.

7. **Local Storage**: 
   - Settings use `multiplatform-settings` (key-value)
   - Database uses SQLDelight with platform-specific drivers (Android: `AndroidDriver`, iOS: `NativeDriver`)
   - Auth tokens are stored locally in `AuthToken` table

8. **Current Branch**: `ft/auth-screen-designs` - working on auth screen redesigns. Recent changes include Signup2Screen, updated SigninScreen, etc.

9. **Date/Time API**: Uses `kotlin.time.Clock` (stdlib) instead of `kotlinx.datetime.Clock` (removed in 0.7.x). Requires `@OptIn(ExperimentalTime::class)` annotation. See `DateHelper.kt` for utility functions.

10. **State Management**: ViewModels use mutableStateOf for Compose state and StateFlow for one-time events. `RequestState<T>` wrapper provides unified loading/error/success handling.

## Troubleshooting

**Gradle wrapper issues**: Use the provided `gradlew` script, not system gradle.

**Build fails after dependency updates**: Check version compatibility in `gradle/libs.versions.toml`. Compose Multiplatform and Kotlin versions should be aligned.

**iOS build fails**: Ensure you have the correct Xcode version and CocoaPods installed. Run `pod install` in `iosApp` if needed.

**Signing config missing for Android**: Configure signing in `composeApp/build.gradle.kts` or ensure debug signing is properly set up.

**Koin errors**: Check that all dependencies are correctly registered in `KoinModule.kt` and `initializeKoin()` is called before composition (it's called in `App()`).

**SQLDelight errors**: Ensure the `sqldelight` Gradle plugin is applied and database schema is valid. Regenerate code with `./gradlew :composeApp:sqldelightGeneratePlatformDatabase`.

**Navigation issues**: Verify routes in `Graph.kt` and `AuthScreen.kt`/`BaseScreen.kt` match the composable destinations in `AppNavGraph.kt`.

**Clock.System unresolved reference**: Ensure you have `@OptIn(ExperimentalTime::class)` annotation on the class/function using it, and import `kotlin.time.Clock` (not `kotlinx.datetime.Clock`).

**DatePeriod constructor errors**: Use `DatePeriod(years = X)` for year-based periods, or `today.minus(X.toLong(), DateTimeUnit.YEAR)` for date arithmetic. The `DatePeriod` constructor is internal.

## Resources

- [Kotlin Multiplatform Documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)
- [Ktor Client](https://ktor.io/docs/client-index.html)
- [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)