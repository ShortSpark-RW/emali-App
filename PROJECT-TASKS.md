# eMaliEstates - Development Tasks & Progress

**Last Updated:** 2026-04-03  
**Current Branch:** `ft/auth-screen-designs`  
**Project Status:** Active Development

---

## 📋 Quick Reference

### Tech Stack
- **Kotlin:** 2.2.0
- **Compose Multiplatform:** 1.9.1
- **Koin:** 4.1.0 (Dependency Injection)
- **Ktor:** 3.3.3 (HTTP Networking)
- **SQLDelight:** 2.1.0 (Local Database)
- **Coil:** 3.3.0 (Image Loading)
- **KMAuth:** 0.3.1 (Google/Supabase Authentication)
- **Navigation Compose:** 2.9.1 (Routing)
- **BuildKonfig:** 0.15.1 (Build-time Configuration)
- **multiplatform-settings:** 1.3.0 (Key-Value Storage)

### Branch Information
- **Current Branch:** `ft/auth-screen-designs`
- Recent Commits:
  - 2d83d67 - feat: add category filtering to home screen
  - bada0f4 - fix: add HomeScreen1 and suppress IDE false positives
  - d8af60f - fix: resolve unresolved references and add missing HomeScreen1
  - cf329db - Merge pull request #1 from ShortSpark-RW/ft/auth-screen-designs
  - 44479c7 - docs: add CLAUDE.md with development guidelines and architecture overview

---

## 🎯 Active Tasks

### Task 1: Refactor HomeScreen for Performance and Design
**Status:** In Progress  
**Priority:** High  
**Assignee:** TBD

#### Current Issues Being Fixed:
1. ✅ **State value access error** - Fixed improper `.value` access on delegate properties
2. ✅ **Type projection issue** - Fixed `RequestState.Success<*>`.isEmpty() compilation error
3. ✅ **Unresolved reference 'currentUser'** - Added `currentUser` property to `AuthViewModel`
4. ⚠️ **SplashScreen integration** - New file not yet tracked by git

#### Detailed Requirements:
- [ ] **Performance Optimization**
  - [ ] Implement lazy loading for property images
  - [ ] Add pagination/infinite scroll for properties list
  - [ ] Optimize category filter to use derived state
  - [ ] Cache category results to avoid repeated API calls
  - [ ] Use `remember` and `derivedStateOf` appropriately

- [ ] **UI/UX Improvements**
  - [ ] Add shimmer placeholder while images load
  - [ ] Improve empty state with better messaging/illustration
  - [ ] Add pull-to-refresh functionality
  - [ ] Implement proper skeleton loading states
  - [ ] Add error boundaries for graceful failure handling
  - [ ] Improve property card layout with better spacing

- [ ] **Code Quality**
  - [ ] Extract reusable components (PropertyCard, CategoryChip)
  - [ ] Add proper content descriptions for accessibility
  - [ ] Follow Material 3 design guidelines
  - [ ] Add unit tests for HomeScreen and ViewModel
  - [ ] Reduce cyclomatic complexity

- [ ] **State Management**
  - [ ] Consider using `derivedStateOf` for computed values
  - [ ] Properly handle state recreation on configuration changes
  - [ ] Add state restoration for scroll position
  - [ ] Ensure state flows are collected with proper lifecycle awareness

#### Files to Modify:
- `composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/home/presentation/HomeScreen1.kt`
- `composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/home/viewModel/MainViewModel.kt`
- `composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/home/presentation/bottomNavigation/` (if exists)

#### Testing Strategy:
- [ ] Unit tests for MainViewModel filtering logic
- [ ] UI tests for category selection and property display
- [ ] Performance tests for large lists (100+ properties)
- [ ] Memory leak detection with LeakCanary (Android)

---

## 📁 Project Structure Documentation

### Root Level
```
emali-App/
├── .claude/                    # Claude Code configuration
├── .gitignore                 # Git ignore rules
├── CLAUDE.md                  # Claude-specific guidance (primary reference)
├── README.md                  # Project overview
├── build.gradle.kts           # Root build configuration
├── settings.gradle.kts        # Settings configuration
├── gradle.properties          # Gradle properties
├── gradlew                    # Gradle wrapper (Unix)
├── gradlew.bat                # Gradle wrapper (Windows)
└── gradle/                    # Gradle wrapper files
```

### Shared Code (`composeApp/src/commonMain/`)
```
composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/
├── App.kt                     # Main entry point (KMAuth, Coil, Navigation)
├── navigation/
│   ├── AppNavGraph.kt         # Main navigation host
│   ├── Graph.kt               # Route constants
│   ├── AuthScreen.kt          # Authentication routes
│   └── BaseScreen.kt          # Main app routes
├── domain/
│   ├── auth/
│   │   ├── LoginRequest.kt
│   │   ├── SignupRequest.kt
│   │   ├── User.kt
│   │   └── [other auth models]
│   ├── Property.kt            # Property model
│   ├── Country.kt             # Country model
│   ├── RequestState.kt        # Sealed class: Idle/Loading/Success/Error
│   └── [other response types]
├── data/
│   ├── local/
│   │   ├── DatabaseFactory.kt # Platform-specific driver factory (expect/actual)
│   │   └── LocalDatabase.kt   # Database wrapper
│   ├── remote/
│   │   ├── AuthApi.kt         # Auth endpoints interface
│   │   ├── PropertyApi.kt     # Property endpoints interface
│   │   ├── CategoryApi.kt     # Category endpoints interface
│   │   └── HttpClientFactory.kt # Ktor client setup with auth interceptor
│   ├── repository/
│   │   ├── AuthRepository.kt  # Combines local + remote auth data
│   │   ├── PropertyRepository.kt # Combines local + remote property data
│   │   └── CategoryRepository.kt # Category data source
│   ├── AuthSDK.kt             # Auth service (used by ViewModels)
│   └── PropertySDK.kt         # Property service (used by ViewModels)
├── di/
│   └── KoinModule.kt          # Koin DI module (sharedModule + expect/actual)
├── auth/
│   ├── presentation/
│   │   ├── SigninScreen.kt    # Email/Password + Google Sign-In
│   │   ├── SignupScreen.kt    # Step 1: basic info
│   │   ├── Signup2Screen.kt   # Step 2: additional details
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── VerifyOtpScreen.kt
│   │   ├── ChangePasswordScreen.kt
│   │   └── [other auth screens]
│   └── viewModel/
│       └── AuthViewModel.kt   # Auth state management
├── home/
│   ├── presentation/
│   │   ├── MainScreen1.kt     # Main app with bottom nav (currently being refactored)
│   │   ├── SplashScreen.kt    # NEW: Splash screen (untracked)
│   │   ├── bottomNavigation/  # Bottom nav bar component(s)
│   │   └── ProfileScreen.kt
│   └── viewModel/
│       └── MainViewModel.kt   # Home state management
├── property/
│   ├── presentation/
│   │   └── PropertyDetailScreen.kt
│   └── viewModel/
│       └── PropertyDetailViewModel.kt
├── theme/
│   ├── Theme.kt               # Material 3 theme setup
│   ├── Color.kt               # Color tokens
│   └── Typography.kt          # Typography tokens
└── util/
    ├── components/
    │   ├── auth/              # Reusable auth UI (text fields, buttons)
    │   └── common/            # Common UI (AppButton, LoadingDialog)
    └── helpers/
        ├── DateHelper.kt
        ├── AppConstants.kt    # BuildKonfig constants wrapper
        └── MessageSerializer.kt
```

### Platform-Specific Code
```
composeApp/src/androidMain/    # Android-specific (drivers, AndroidManifest.xml)
composeApp/src/iosMain/        # iOS-specific (Darwin driver)
iosApp/                        # iOS native entry point (Swift/Xcode)
```

---

## 🏗️ Architecture Deep Dive

### 1. Layered Architecture

```
Presentation Layer (Compose UI)
       ↓
ViewModels (State management, business logic)
       ↓
SDK Layer (API orchestration, error handling)
       ↓
Repositories (Data source coordination)
       ↓
Data Sources (Local DB + Remote API)
```

### 2. State Management Pattern

**RequestState<T>** - Sealed class for async UI state:
```kotlin
sealed class RequestState<out T> {
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()
}
```

**ViewModel Exposes:** `StateFlow<RequestState<T>>` (or `MutableState<T>` for Compose state)

### 3. Navigation Structure

**Two-Graph System:**
- `GRAPH_AUTHENTICATION` - Auth flow (unauthenticated users)
- `GRAPH_BASE` - Main app with bottom navigation (authenticated users)

**Routes:** Defined in `Graph.kt`, `AuthScreen.kt`, `BaseScreen.kt`

**Splash Screen:** Entry point that checks `currentUser` and routes accordingly

### 4. Dependency Injection (Koin)

**Module Structure:**
- `sharedModule` (commonMain) - All common dependencies
- Platform-specific modules via `expect/actual targetModule`
- ViewModels are factory-scoped
- SDKs and repositories are singletons

**Initialization:** `initializeKoin()` called in `App.kt` before Compose composition

### 5. Data Flow

**Property Listing Flow:**
1. `MainViewModel` loads categories on init
2. `MainViewModel` loads properties (optionally filtered by category)
3. UI observes `allProperties` and `allCategories` `StateFlow`s
4. User selects category → `selectCategory()` → `loadProperties(categoryId)`
5. `PropertySDK.getAllProperties()` → `PropertyRepository` → chooses local/remote
6. Results flow back through ViewModel to UI

---

## 🔧 Common Development Tasks

### Build & Run

**Android:**
```bash
./gradlew :composeApp:assembleDebug      # Build APK
./gradlew :composeApp:installDebug       # Install on device/emulator
./gradlew :composeApp:assembleRelease   # Build release APK
```

**iOS:**
```bash
open iosApp/iosApp.xcodeproj            # Open in Xcode
cd iosApp && xcodebuild                 # Build from command line
```

### Testing
```bash
./gradlew :composeApp:commonTest        # Run all common tests
./gradlew :composeApp:commonTest --tests "*AuthViewModel*"  # Run specific test
./gradlew :composeApp:test              # Run all tests including Android
```

### Code Quality
```bash
./gradlew ktlintCheck                   # Check code style
./gradlew ktlintFormat                  # Format code
./gradlew :composeApp:detekt           # Run static analysis (if configured)
```

---

## ⚙️ Configuration

### Build Configuration (BuildKonfig)

Create `local.properties` in project root:
```properties
webClientId=your_google_oauth_client_id
webClientSecret=your_google_oauth_client_secret
supabaseUrl=your_supabase_url
supabaseKey=your_supabase_anon_key
apiEndpoint=your_backend_api_endpoint
```

Access in code: `AppConstants.WEB_CLIENT_ID` (generated from BuildKonfig)

**Security:** Never commit `local.properties`! Use environment variables for CI/CD.

### Database (SQLDelight)

Schema files: `composeApp/src/commonMain/sqldelight/com/shortspark/*.sq`

**Key Tables:**
- `User`, `AuthToken`, `Property`, `Category`, `Place`, `Location`
- `Province`, `District`, `Sector`, `Cell`, `Village`
- `Reservation`, `Favorite`, `Identification`, `Testimonial`
- `RentalPeriod`, `Notification`, `FAQ`

**Regenerate after schema changes:**
```bash
./gradlew :composeApp:sqldelightGeneratePlatformDatabase
```

---

## 🐛 Troubleshooting

### Gradle Issues
- Always use `./gradlew` (wrapper), not system Gradle
- Clean build: `./gradlew clean`
- Update dependencies in `gradle/libs.versions.toml`

### Compilation Errors

**"Unresolved reference 'currentUser'"**
- ✅ Fixed: Added `currentUser` derived property to `AuthViewModel`

**"None of the following candidates is applicable" for `.isEmpty()`**
- ✅ Fixed: Cast star-projected `RequestState.Success<*>` to proper type

**Koin errors**
- Check `KoinModule.kt` registrations
- Verify `initializeKoin()` is called in `App.kt` before composition

**SQLDelight errors**
- Ensure plugin applied
- Validate `.sq` file syntax
- Regenerate: `./gradlew :composeApp:sqldelightGeneratePlatformDatabase`

### Platform-Specific

**iOS build fails:**
- Ensure Xcode version matches deployment target
- Run `pod install` in `iosApp/`
- Check Kotlin/Native compatibility

**Android signing:**
- Debug signing usually auto-configured
- Release signing: configure in `composeApp/build.gradle.kts`

---

## 📋 Today's Task Breakdown (2026-04-03)

### 🎯 Goal: Complete HomeScreen Refactoring

#### Current State:
- `HomeScreen1.kt` has compilation errors (FIXED)
- `AuthViewModel` missing `currentUser` (FIXED)
- `SplashScreen.kt` is new and untracked

#### Immediate Actions:

1. **Fix Compilation Errors** (COMPLETED)
   - [x] Fix `categoriesState.value` → `categoriesState` (delegate pattern)
   - [x] Fix `propertiesState.value` → `propertiesState`
   - [x] Fix `state.data.isEmpty()` by casting from star projection
   - [x] Add `currentUser` to `AuthViewModel`

2. **Integrate SplashScreen**
   - [ ] Verify `SplashScreen.kt` is properly structured
   - [ ] Ensure navigation to `SplashScreen` works
   - [ ] Test splash → auth vs splash → home routing

3. **Continue HomeScreen Refactoring**
   - [ ] Profile performance with large datasets
   - [ ] Implement proper loading/error/empty states
   - [ ] Add category filtering with state restoration
   - [ ] Optimize `LazyColumn` item composables
   - [ ] Extract `PropertyCard` into reusable component
   - [ ] Add unit tests for `MainViewModel.filtering`

4. **Code Quality**
   - [ ] Run ktlint and fix formatting
   - [ ] Review for Compose best practices
   - [ ] Add proper accessibility labels
   - [ ] Ensure Material 3 compliance

5. **Testing**
   - [ ] Run existing tests
   - [ ] Add ViewModel tests for category filtering
   - [ ] Manual testing on Android emulator/iOS simulator

---

## 📊 Current Git Status

```
Branch: main (should eventually commit to ft/auth-screen-designs)

Modified files:
  M composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/App.kt
  M composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/home/presentation/HomeScreen1.kt
  M composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/navigation/AppNavGraph.kt

Untracked files:
  ?? composeApp/src/commonMain/kotlin/com/shortspark/emaliestates/home/presentation/SplashScreen.kt
```

**Note:** Verify you're on the correct branch (`ft/auth-screen-designs`)

---

## 🔗 Useful Resources

### Official Documentation
- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)
- [Ktor Client](https://ktor.io/docs/client-index.html)
- [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)

### Project-Specific
- `CLAUDE.md` - Primary reference for Claude Code assistance
- This document - Task tracking and project overview

---

## 📝 Notes

- Current branch: `ft/auth-screen-designs` - focused on auth screen redesign
- Recent work: Category filtering added to home screen, HomeScreen1 introduced
- AuthViewModel now uses unified state for all auth operations
- Navigation uses two-graph system with splash screen routing
- All Compose UI follows Material 3 guidelines

---

**Document Maintainer:** Claude Code Assistant  
**Next Review:** 2026-04-04 (or as needed)
