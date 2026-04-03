# eMaliEstates - Comprehensive Implementation Roadmap

**Last Updated:** April 3, 2026  
**Project:** eMali Estates Mobile App (Kotlin Multiplatform)  
**Backend:** Nest.js + Prisma + PostgreSQL  
**Design:** Figma UI/UX Designs  
**Jira Project:** SCRUM (shortspark.atlassian.net)  
**Current Branch:** `ft/auth-screen-designs` → transitioning to `ft/property-listing-with-categories`

---

## 📋 Executive Summary

eMaliEstates is a full-featured real estate marketplace mobile app built with **Kotlin Multiplatform (Compose Multiplatform)** targeting Android and iOS. The app integrates with a comprehensive Nest.js backend providing property listings, authentication, payments, video feeds, and geolocation-based search.

### Current Status (as of April 2026)
- ✅ **Core Infrastructure**: KMM setup, DI (Koin), Navigation, Theme, Auth flow
- ✅ **Backend APIs**: Complete REST API with Prisma, PostgreSQL, AWS integrations
- ✅ **Database**: SQLDelight local persistence with comprehensive schema
- 🔄 **UI Implementation**: 60% complete
  - ✅ **Auth** (100%): Signin, Signup, OTP, Forgot/Change Password
  - ✅ **Property Listing** (90%): Category filtering, infinite scroll, pull-to-refresh, PropertyCard with images
  - 🔄 **HomeScreen**: Just completed (Sprint 1.1) - category filter + property grid
  - ❌ **MapScreen**: Not started (blocked on backend lat/lng data)
  - ❌ **Favorites**: Backend ready, UI missing
  - ❌ **Property Management**: Create/Edit screens not started
  - ❌ **Video Feed**: Backend partial, UI missing
  - ❌ **Payments**: Backend partial, UI missing

---

## 🎯 Jira Epics & Feature Breakdown

### Epic 1: SCRUM-2 - Authentication & User Identity Management
**Priority:** High | **Status:** 80% Complete (Mobile)

#### Backend (✅ Complete)
- ✅ JWT-based auth with refresh tokens
- ✅ Email/password login & signup
- ✅ OTP verification via email/SMS
- ✅ Password reset flow (forgot & reset)
- ✅ Google OAuth integration
- ✅ Logout with server-side invalidation
- ✅ Role-based access control (OWNER, AGENT, CLIENT)

#### Mobile App (✅ 80% Complete)
- ✅ Signin Screen (`SigninScreen.kt`)
- ✅ Signup Screen (`SignupScreen.kt`, `Signup2Screen.kt`)
- ✅ OTP Verification (`VerifyOtpScreen.kt`)
- ✅ Forgot/Change Password (`ForgotPasswordScreen.kt`, `ChangePasswordScreen.kt`)
- ✅ Google Auth ViewModel (`GoogleAuthViewModel.kt`)
- ✅ Secure token storage (SQLDelight)
- ✅ Session management with auto-login
- ❌ **Missing:** Biometric login support (fingerprint/face)
- ❌ **Missing:** Social login buttons UI for Facebook/Apple

#### Acceptance Criteria
- ✅ Users can create accounts and verify via OTP
- ✅ Login/logout with secure session handling
- ✅ Google login functional
- ⚠️ Biometric login pending (future enhancement)

---

### Epic 2: SCRUM-3 - User Identification & Verification
**Priority:** High | **Status:** 70% Complete

**📘 See Detailed Plans:**
- **MapScreen Implementation:** [`MAP_SCREEN_IMPLEMENTATION_PLAN.md`](./MAP_SCREEN_IMPLEMENTATION_PLAN.md)

#### Backend (✅ Complete)
- ✅ POST `/api/v1/identifications` - Upload ID document
- ✅ GET `/api/v1/identifications/{id}` - Get status
- ✅ PATCH `/api/v1/identifications/{id}` - Manual correction
- ✅ GET `/api/v1/identifications/user/{userId}` - Get user's ID
- ✅ PATCH `/api/v1/identifications/{id}/verify` - Verify identity
- ✅ OCR integration (Tesseract/AWS Textract)
- ✅ AWS Rekognition for face matching
- ✅ Statuses: PENDING, APPROVED, REJECTED

#### Mobile App (⚠️ 30% Complete)
- ✅ Domain model: `Identification.kt` (type, status, documentUrl, etc.)
- ✅ Identification DTOs
- ❌ **Missing UI Screens:**
  - ID upload screen (camera/gallery capture)
  - OCR extraction preview & edit
  - Verification status tracker
  - Retry flow for rejected verifications
- ❌ **Missing:** Camera integration in KMM
- ❌ **Missing:** File upload to S3

#### Gap Analysis
**Critical:** Implement identification upload UI and API integration. This is needed for user verification compliance (KYC/AML).

---

### Epic 3: SCRUM-4 - Property Management & Listings (Core Feature)
**Priority:** Critical | **Status:** 60% Complete

#### Backend (✅ 100% Complete)
All CRUD endpoints implemented in `properties.controller.ts`:
- ✅ POST `/properties` - Create property
- ✅ GET `/properties` - List with pagination
- ✅ GET `/properties/featured` - Featured properties
- ✅ GET `/properties/search` - Advanced filters (type, price, bedrooms, etc.)
- ✅ GET `/properties/category/{id}` - Filter by category
- ✅ GET `/properties/place/{id}` - Filter by place
- ✅ GET `/properties/owner/{id}` - Owner's properties
- ✅ GET `/properties/{id}` - Property details
- ✅ PUT `/properties/{id}` - Update
- ✅ DELETE `/properties/{id}` - Delete
- ✅ POST `/properties/{id}/images` - Upload images (up to 11)
- ✅ PUT `/properties/{id}/archive` - Archive with reason
- ✅ PUT `/properties/{id}/unarchive` - Unarchive
- ✅ PUT `/properties/{id}/toggle-featured` - Toggle featured
- ✅ PUT `/properties/{id}/toggle-verified` - Toggle verified
- ✅ PUT `/properties/{id}/mark-sold` - Mark as sold
- ✅ PUT `/properties/{id}/mark-rented` - Mark as rented

#### Database (✅ Complete)
SQLDelight schema includes:
- ✅ Property table with all fields (type, price, location, images, amenities, status flags)
- ✅ Foreign keys: ownerId → User, categoryId → Category, placeId → Place, locationId → Location
- ✅ Indexes for performance (owner, category, place, type, featured, createdAt)
- ✅ Support for: images, video, floor plan, amenities, utilities

#### Mobile App (⚠️ 40% Complete)
**✅ Completed:**
- Property domain model (`Property.kt`)
- PropertyDetailScreen with basic display
- PropertyDetailViewModel with caching logic (5-min TTL)
- PropertyApi with getPropertyById()
- Repository pattern with SQLDelight

**❌ Missing / In Progress:**
- 🚨 **Main Property Listing Screen** (Home tab)
  - Grid/List view with infinite scroll
  - Property cards with image, title, price, location, badges
  - Search bar with filters
  - Pull-to-refresh
- 🚨 **Create/Edit Property Flow**
  - Form with all fields (title, description, type, saleType, price, location, bedrooms, bathrooms, area, amenities, utilities)
  - Image picker (camera/gallery) with preview
  - Video URL input / file upload
  - Location picker integration
- 🚨 **Filter & Search UI**
  - Bottom sheet or modal with filter options
  - Price range slider
  - Type & saleType dropdowns
  - Bedrooms/bathrooms/area min-max
  - Furnished toggle
  - Reset/Apply buttons
- 🚨 **Owner Dashboard** ("My Properties")
  - List of user's properties
  - Edit/Archive/Delete actions
  - Status toggles (featured, verified, sold, rented)
- ⚠️ **Image Gallery** in PropertyDetailScreen (currently only basic display)
- ⚠️ **Share Property** functionality
- ⚠️ **Reservation** flow (if needed)

#### Figma Design Reference
🔗 [Figma Link](https://www.figma.com/design/TquEc9YNZ33iIfhWlrKRXV/?node-id=0-1&p=f&t=Nsgu0oaz7E8JbHbz-0)
- Expected: Property card designs, listing layouts, detail screens, create/edit forms

---

### Epic 4: SCRUM-5 - GPS-Based Property Search on Interactive Map
**Priority:** Medium | **Status:** 15% Complete

#### Backend (⚠️ 50% Complete)
- ✅ Location table exists (`Location.sq` with lat, lng, placeId)
- ✅ Property linked to Location via `locationId`
- ✅ Places table (`Place.sq`) for place-based search
- ❌ **Missing:** Geospatial query endpoint
  - Need: `GET /api/v1/properties/nearby?lat={lat}&lng={lng}&radius={km}`
  - Need: PostGIS setup or haversine formula implementation
- ❌ **Missing:** Geospatial indexing in PostgreSQL (GIST index on Location coordinates)

#### Mobile App (❌ 0% Complete)
- ❌ **Missing Map Screen** (currently placeholder: `Text("Map Screen")`)
- ❌ **Missing:**
  - Google Maps or Mapbox integration
  - GPS permission handling
  - User location fetching
  - Property pins/clusters on map
  - Radius-based filtering
  - Map ↔ List toggle
  - Property detail popup on pin tap

#### Implementation Plan
**Phase 1:** Add backend nearby properties endpoint with distance calculation  
**Phase 2:** Integrate Google Maps SDK in KMM (compose-maps)  
**Phase 3:** Implement location services & permissions  
**Phase 4:** Build map UI with markers and clustering  
**Phase 5:** Add filters panel (radius, category, price)

---

### Epic 5: SCRUM-6 - Short Video Feed of Trending Properties
**Priority:** Medium | **Status:** 5% Complete

#### Backend (⚠️ 40% Complete)
**Existing Infrastructure:**
- ✅ Property model has `videoUrl` field
- ✅ S3 module exists (`src/s3/`) for file uploads
- ✅ Testimonials module (could inspire video comments)

**❌ Missing Video-Specific:**
- Video upload API (`POST /properties/{id}/video`)
- Video streaming endpoints (CDN integration)
- Trending algorithm (views, likes, shares, recency)
- Video engagement endpoints:
  - POST `/videos/{id}/like`
  - POST `/videos/{id}/share`
  - POST `/videos/{id}/comment`
  - POST `/videos/{id}/view` (track view)
- Video model/schema (separate from property images)
- Videofeed endpoint: `GET /videos/trending` or `GET /videos/feed`

#### Mobile App (❌ 0% Complete)
- ❌ **Missing Video Feed Screen** (Tours tab placeholder)
- ❌ **Missing:**
  - Video player (ExoPlayer or Compose VideoPlayer)
  - Vertical swipe gesture navigation
  - Autoplay on visible item
  - Like/comment/share/save overlays
  - Property detail quick-view from video
  - Video upload from app (owners/agents)
  - Offline caching of recent videos
  - Push notifications for trending videos

#### Implementation Plan
**Phase 1:** Backend video upload + streaming API (S3 presigned URLs)  
**Phase 2:** Video model + trending algorithm  
**Phase 3:** Integrate video player library in KMM  
**Phase 4:** Build TikTok-style vertical feed UI  
**Phase 5:** Engagement actions (like, comment, share, save)

---

### Epic 6: SCRUM-7 - Paid Access to Property Details (Freemium)
**Priority:** Medium | **Status:** 10% Complete

#### Backend (⚠️ 60% Complete)
**Existing Infrastructure:**
- ✅ Payments module (`src/payments/`)
- ✅ MoMo integration (Africa's Talking)
- ✅ Reservation module (`src/reservations/`)
- ✅ Transaction model
- ❌ **Missing Per-Property Unlock:**
  - Need: Payment endpoint to unlock property details
  - Need: Track `user.hasPaidForProperty[propertyId]` in database
  - Need: Middleware/guard to restrict sensitive fields (owner contact, phone, email)
  - Need: Webhook validation callback from payment provider

**Proposed API:**
- `POST /api/v1/payments/unlock-property/{propertyId}` - Initiate payment
- `POST /api/v1/payments/verify-unlock/{paymentId}` - Verify & grant access
- `GET /api/v1/user/unlocked-properties` - List user's unlocked properties
- Middleware: `UnlockRequiredGuard` to protect sensitive property fields

#### Mobile App (❌ 0% Complete)
- ❌ **Missing Payment UI:**
  - Property locked state overlay (padlock icon)
  - "Unlock to view contact" prompt
  - Payment modal with pricing & payment options
  - Success/failure screens
  - Receipt/invoice view
- ❌ **Missing:** Payment SDK integration (Flutterwave/Stripe mobile SDKs)
- ❌ **Missing:** Payment state management in ViewModel

#### Implementation Plan
**Phase 1:** Design payment schema & unlock tracking  
**Phase 2:** Implement payment endpoints + webhook  
**Phase 3:** Create UI screens (lock modal, checkout, receipt)  
**Phase 4:** Integrate payment SDK in KMM  
**Phase 5:** Test end-to-end payment flow

---

### Task: SCRUM-78 - Property Favorites
**Priority:** Medium | **Status:** 60% Complete

#### Backend (✅ Complete)
- ✅ Favorites module exists (`src/favorites/`)
- ✅ Endpoints:
  - POST `/favorites` - Add favorite
  - DELETE `/favorites/{id}` - Remove favorite
  - GET `/favorites/user/{userId}` - Get user's favorites
  - GET `/favorites/property/{propertyId}` - Get property's favoriters
- ✅ Favorite table with composite unique (userId, propertyId)

#### Mobile App (⚠️ 60% Complete)
**✅ Completed:**
- Favorite domain model (`Favorite.kt`)
- Favorite DTOs
- Favorite repository queries in SQLDelight (`Favorite.sq`)
- Likely API layer in PropertyApi or PropertySDK

**❌ Missing:**
- ❌ **Favorite toggle UI** on property cards & detail screen (heart icon)
- ❌ **Favorites screen** (list of saved properties)
- ❌ ViewModel for managing favorites state
- ❌ Guest user prompt (login required) when favoriting

---

## 📊 Overall Completion Summary (Updated April 2026)

| Module | Backend | Mobile UI | Integration | Overall |
|--------|---------|-----------|-------------|---------|
| **Authentication** | 100% | 100% ✅ | 95% | **98%** |
| **Categories** | 90%* | 100% ✅ | 90% | **93%** |
| **Property Browse** | 100% | 75% ✅ | 60% | **78%** |
| **Property Mgmt** | 100% | 10% | 20% | **43%** |
| **Favorites** | 100% | 20% | 30% | **50%** |
| **Map Search** | 50% | 5% | 10% | **22%** |
| **Video Feed** | 40% | 0% | 5% | **15%** |
| **Payments** | 60% | 0% | 10% | **23%** |
| **Overall** | **88%** | **61%** | **45%** | **64%** |

*Note: Categories endpoint may need implementation on backend if not already present.

**Sprint 1.1 completed:** Category filtering + property listing with infinite scroll ✅
**Next priority:** Sprint 2.2 - MapScreen (planning complete, ready to start after backend lat/lng confirmation)

---

## 🗺️ Implementation Roadmap (Prioritized Phases)

### **PHASE 1: Core Property Experience** (Weeks 1-3)
**Goal:** Users can browse, search, view, and create properties.

**Sprint 1.1: Property Listing & Browse**
- [ ] Build HomeScreen with property grid/list (RecyclerView equivalent in Compose)
- [ ] Implement PropertyCard composable with image, title, price, location, badges
- [ ] Add infinite scroll pagination
- [ ] Implement search bar with filter button
- [ ] Wire up PropertySDK.getAllProperties() with caching
- [ ] Add pull-to-refresh

**Sprint 1.2: Property Detail Enhancement**
- [ ] Complete PropertyDetailScreen with full data display
- [ ] Image carousel with zoom capability (Coil + Compose)
- [ ]Amenities & utilities chips
- [ ] Contact owner button (with paid-unlock placeholder)
- [ ] Share property intent
- [ ] Favorite toggle (heart icon) - requires login check

**Sprint 1.3: Property Management**
- [ ] Add/Edit Property screen with full form
- [ ] Camera/gallery image picker ( multiplatform: `compose-multiplatform-image-picker`)
- [ ] Image preview & delete before upload
- [ ] Location picker integration (Google Places API)
- [ ] Create, update, delete operations
- [ ] Owner dashboard ("My Properties" screen)
- [ ] Property status toggles (featured, verified, sold, rented)

**Sprint 1.4: Search & Filters**
- [ ] Filter bottom sheet/modal UI
- [ ] All filter fields: type, saleType, price range, bedrooms, bathrooms, area, furnished
- [ ] Reset/Apply logic
- [ ] Wire up PropertyApi.searchProperties()
- [ ] Preserve filter state on navigation

---

### **PHASE 2: Engagement & Retention** (Weeks 4-6)
**Goal:** Users can save favorites, view maps, and get verified.

**Sprint 2.1: Favorites System**
- [ ] FavoritesViewModel with StateFlow
- [ ] Heart icon toggle on property cards & detail
- [ ] Guest login prompt (navigation to auth)
- [ ] Favorites screen (list/grid)
- [ ] Remove from favorites swipe action
- [ ] Sync with backend (POST/DELETE favorites)

**Sprint 2.2: Map-Based Search**
- [ ] Add Google Maps dependency (`compose-maps`)
- [ ] Request location permissions (Android/iOS)
- [ ] Get current GPS location
- [ ] Add MapScreen to bottom nav
- [ ] Display property markers with clustering
- [ ] Tapping marker shows property preview
- [ ] Filter by radius (configurable km)
- [ ] Add `GET /properties/nearby` endpoint on backend (if not done)

**Sprint 2.3: User Verification**
- [ ] ID upload screen (camera + gallery)
- [ ] Document type selection (ID, Passport, Driver License)
- [ ] Upload to S3 (backend presigned URL)
- [ ] Submit to identification endpoint
- [ ] OCR results preview screen (read-only if success, editable if failed)
- [ ] Verification status badge on profile
- [ ] Retry flow for rejected documents

---

### **PHASE 3: Advanced Features** (Weeks 7-9)
**Goal:** Monetization & discovery features.

**Sprint 3.1: Short Video Feed**
- [ ] Research KMM video player options (ExoPlayer via `androidx.media3`, AVPlayer for iOS)
- [ ] Build ToursScreen with `LazyColumn` + fullscreen videos
- [ ] Vertical swipe with autoplay/pause on visibility
- [ ] Video caching (store last 5 videos)
- [ ] Add backend: POST `/properties/{id}/video`, GET `/videos/trending`
- [ ] Like, comment, share, save overlays
- [ ] Property detail overlay from video

**Sprint 3.2: Payments & Unlocks**
- [ ] Design payment flow UI (lock modal, pricing, checkout)
- [ ] Integrate payment SDK (Flutterwave/Stripe)
- [ ] Backend: Payment unlock endpoint + webhook
- [ ] Property detail: hide owner contact unless paid
- [ ] Show "locked" state with blur effect
- [ ] Receipt/success screen
- [ ] Transaction history in profile

**Sprint 3.3: Notifications & Real-time**
- [ ] WebSocket integration ( backendpusher/Redis)
- [ ] Push notifications (Firebase Cloud Messaging, APNs)
- [ ] Notification screen (list of alerts)
- [ ] In-app notification center
- [ ] Real-time property status updates (sold, new, price drop)

---

### **PHASE 4: Polish & Production Readiness** (Weeks 10-12)
**Goal:** Quality, performance, and deployment.

**Sprint 4.1: UI/UX Polish**
- [ ] Ensure pixel-perfect Figma design compliance
- [ ] Dark theme implementation across all screens
- [ ] Accessibility: content descriptions, font scaling
- [ ] Loading & error states for all screens
- [ ] Empty states (no properties, no favorites, no search results)
- [ ] Smooth animations & transitions
- [ ] Compose UI testing with Compose Testing Library

**Sprint 4.2: Performance & Offline**
- [ ] Optimize image loading (Coil with disk & memory caching)
- [ ] Pagination with prefetching
- [ ] Offline-first: cached properties viewable without network
- [ ] Queue failed mutations (create property, favorites) for sync
- [ ] Reduce APK size (ProGuard/R8, remove unused resources)
- [ ] Memory usage profiling

**Sprint 4.3: iOS Build & Testing**
- [ ] Fix iOS build issues (SKIE, framework setup)
- [ ] Test on iOS simulator & physical device
- [ ] Handle iOS-specific permissions (camera, location, photos)
- [ ] App icon & splash screen for iOS
- [ ] iOS code signing & provisioning profile

**Sprint 4.4: QA & Bug Fixes**
- [ ] Cross-device testing (Android: various sizes, API levels)
- [ ] Edge cases: zero properties, slow network, server errors
- [ ] Security audit: token storage, API security
- [ ] Beta testing (internal testers on Firebase App Distribution / TestFlight)
- [ ] Fix all P1/P2 bugs
- [ ] Prepare release build (proguard, signing)

**Sprint 4.5: Deployment**
- [ ] Generate signed APK/AAB for Android
- [ ] Upload to Google Play Console (closed testing)
- [ ] Build iOS IPA & upload to App Store Connect (TestFlight)
- [ ] Configure monitoring (Crashlytics, Sentry)
- [ ] Documentation: API integration docs, architecture decision records (ADRs)

---

## 🏗️ Architecture & Tech Stack Reference

### Mobile App (Kotlin Multiplatform)
```
composeApp/
├── src/
│   ├── commonMain/
│   │   ├── kotlin/com/shortspark/emaliestates/
│   │   │   ├── App.kt                    # Entry point, KMAuth, Coil, NavGraph
│   │   │   ├── navigation/               # Navigation graphs & routes
│   │   │   ├── domain/                   # Business logic, models
│   │   │   ├── data/
│   │   │   │   ├── local/               # SQLDelight DB
│   │   │   │   │   ├── LocalDatabase.kt
│   │   │   │   │   └── *.sq (schema)
│   │   │   │   ├── remote/              # API clients (Ktor)
│   │   │   │   │   ├── AuthApi.kt
│   │   │   │   │   ├── PropertyApi.kt
│   │   │   │   │   └── HttpClientFactory.kt
│   │   │   │   ├── repository/          # Repository pattern
│   │   │   │   └── *SDK.kt              # Business orchestration (AuthSDK, PropertySDK)
│   │   │   ├── di/
│   │   │   │   └── KoinModule.kt       # Dependency injection
│   │   │   ├── theme/                   # Material3 theme, colors, typography
│   │   │   ├── util/
│   │   │   │   ├── components/         # Reusable composables
│   │   │   │   │   ├── auth/           # Login/signup UI components
│   │   │   │   │   └── common/         # Buttons, text fields, loading dialogs
│   │   │   │   └── helpers/            # DateHelper, Logger, Constants
│   │   │   ├── auth/presentation/      # Auth screens
│   │   │   ├── auth/viewModel/         # Auth ViewModels
│   │   │   ├── home/presentation/      # Main screens (Home, Map, Tours, Profile)
│   │   │   ├── home/viewModel/
│   │   │   ├── property/presentation/  # Property detail
│   │   │   └── property/viewModel/
│   │   └── resources/                  # Images, strings, fonts
│   ├── androidMain/                    # Android-specific (drivers, manifest)
│   └── iosMain/                        # iOS-specific (Darwin driver)
├── iosApp/                             # Xcode project (SwiftUI entry)
├── build.gradle.kts                    # KMP config, dependencies, BuildKonfig
├── local.properties                    # API keys, secrets (gitignored)
└── settings.gradle.kts
```

### Backend (Nest.js + Prisma)
```
eMali_backend/
├── src/
│   ├── auth/               # JWT, Passport strategies, AuthService
│   ├── identification/     # ID upload, OCR, verification
│   ├── properties/         # Property CRUD, search, filters
│   ├── favorites/          # User favorites
│   ├── payments/           # MoMo integration, transaction handling
│   ├── locations/          # Regions, districts, sectors, villages
│   ├── places/             # Cities, landmarks
│   ├── categories/         # Property categories (house, land, apartment)
│   ├── users/              # User management
│   ├── notifications/      # WebSocket real-time
│   ├── strategies/         # OAuth (Google, Facebook)
│   ├── prisma/             # Schema, migrations, seeds
│   └── main.ts
├── prisma/schema.prisma    # Full data model
├── .env                    # Config (DB, JWT, API keys)
└── README.md
```

### Key Dependencies

**Mobile (build.gradle.kts)**
- Kotlin 2.2.0
- Compose Multiplatform 1.9.1
- Koin 3.5.x (DI)
- Ktor 2.3.x (HTTP)
- SQLDelight 2.0.x (local DB)
- Coil 3.x (image loading)
- Navigation Compose
- KMAuth (Google OAuth)

**Backend (package.json)**
- Nest.js 10
- Prisma 5
- PostgreSQL
- Passport + JWT
- Swagger/OpenAPI
- AWS SDK (S3, Rekognition)
- Mailgun / Africa's Talking

---

## 🔌 API Endpoints Reference

### Already Implemented in Backend
| Feature | Endpoint | Method | Auth | Mobile Status |
|---------|----------|--------|------|---------------|
| Auth | `/api/v1/auth/login` | POST | No | ✅ Connected |
| Auth | `/api/v1/auth/signup` | POST | No | ✅ Connected |
| Auth | `/api/v1/auth/verify-otp` | POST | No | ✅ Connected |
| Auth | `/api/v1/auth/forgot-password` | POST | No | ✅ Connected |
| Auth | `/api/v1/auth/reset-password` | POST | No | ✅ Connected |
| Auth | `/api/v1/auth/google` | POST | No | ✅ Connected |
| Auth | `/api/v1/auth/refresh` | POST | Yes | ✅ Connected |
| Auth | `/api/v1/auth/logout` | POST | Yes | ✅ Connected |
| Properties | `/api/v1/properties` | GET | No | ✅ Partial |
| Properties | `/api/v1/properties` | POST | Yes | ⚠️ Missing UI |
| Properties | `/api/v1/properties/{id}` | GET | No | ✅ Connected |
| Properties | `/api/v1/properties/{id}` | PUT | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/{id}` | DELETE | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/featured` | GET | No | ❌ Missing UI |
| Properties | `/api/v1/properties/search` | GET | No | ❌ Missing UI |
| Properties | `/api/v1/properties/category/{id}` | GET | No | ❌ Missing UI |
| Properties | `/api/v1/properties/place/{id}` | GET | No | ❌ Missing UI |
| Properties | `/api/v1/properties/owner/{id}` | GET | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/{id}/images` | POST | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/{id}/archive` | PUT | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/{id}/toggle-featured` | PUT | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/{id}/toggle-verified` | PUT | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/{id}/mark-sold` | PUT | Yes | ❌ Missing UI |
| Properties | `/api/v1/properties/{id}/mark-rented` | PUT | Yes | ❌ Missing UI |
| Favorites | `/api/v1/favorites` | POST | Yes | ❌ Missing UI |
| Favorites | `/api/v1/favorites/{id}` | DELETE | Yes | ❌ Missing UI |
| Favorites | `/api/v1/favorites/user/{userId}` | GET | Yes | ❌ Missing UI |
| Identification | `/api/v1/identifications` | POST | Yes | ❌ Missing UI |
| Identification | `/api/v1/identifications/{id}` | GET | Yes | ❌ Missing UI |
| Identification | `/api/v1/identifications/user/{userId}` | GET | Yes | ❌ Missing UI |
| Identification | `/api/v1/identifications/{id}` | PATCH | Yes | ❌ Missing UI |
| Identification | `/api/v1/identifications/{id}/verify` | PATCH | Yes | ❌ Missing UI |
| Payments | `/api/v1/payments/initiate` | POST | Yes | ❌ Not Implemented |
| Payments | `/api/v1/payments/verify` | POST | Yes | ❌ Not Implemented |
| Videos | (Need to be added) | | | ❌ Not Implemented |
| Nearby | (Need to add) | GET | No | ❌ Not Implemented |

---

## 📱 Current Screens & Implementation Status

### Completed Screens ✅ (Sprint 1.1+)
1. **SigninScreen** (`auth/presentation/SigninScreen.kt`)
2. **SignupScreen** + **Signup2Screen** (multi-step registration)
3. **VerifyOtpScreen** (OTP input)
4. **ForgotPasswordScreen** + **ChangePasswordScreen**
5. **ProfileScreen** (basic - extend with settings, verification status, my properties)
6. **HomeScreen** (`home/presentation/HomeScreen.kt`) ✅ **JUST COMPLETED**
   - Category filter horizontal scroll bar
   - Property grid with infinite scroll
   - Pull-to-refresh
   - Property cards with images, price, category, beds/baths
7. **CategoryChip**, **CategoryFilterBar**, **PropertyCard** components ✅

### In Progress / Partial 🟡
8. **PropertyDetailScreen** - Basic info shown, needs image carousel, amenities, actions
9. **MapScreen** - Next up (Sprint 2.2) - planning complete, library selected

### Missing / Placeholder 🔴
10. **ToursScreen** - Placeholder `Text("eTours Screen")`
11. **SearchScreen** - Placeholder `Text("Search Screen")`
12. **MyPropertiesScreen** - Not created yet
13. **AddEditPropertyScreen** - Not created yet
14. **FavoritesScreen** - Not created yet (backend ready)
15. **FilterScreen** - Reuse from HomeScreen?
16. **IdentificationUploadScreen** - Not created yet
17. **PaymentUnlockScreen** - Not created yet
18. **VideoFeedScreen** - Not created yet

---

## 🗺️ MapScreen Implementation Plan (Sprint 2.2)

**Priority:** Medium | **Status:** Planning Phase → Ready to Start

### Library Selection
✅ **GoogleMapCompose** by Nigorjeanluc (https://github.com/Nigorjeanluc/GoogleMapCompose)
- Cross-platform (Android + iOS) Compose-first wrapper
- Supports markers, camera control, info windows, location layer
- See detailed plan in separate file: `MAP_SCREEN_IMPLEMENTATION_PLAN.md`

### Implementation Phases
1. **Setup** (Day 1-2): Add dependency, API key, basic map
2. **Markers** (Day 3-4): Property pins, info windows, navigation
3. **Camera** (Day 5): Auto-fit bounds, map types
4. **Backend Integration** (Day 6): Ensure properties include lat/lng
5. **Filtering** (Day 7): Category/price filters on map
6. **Polish** (Day 8-10): Clustering, location layer, UX

### Critical Backend Dependency
⚠️ **BLOCKER:** Backend must return `lat` and `lng` fields in Property response OR we must join Location table to expose coordinates.

**Action:** Confirm with backend team if `/properties` endpoint returns coordinates. If not, implement join or add flat fields.

### Acceptance Criteria
- ✅ Map loads with Google Maps styling
- ✅ All properties show as markers
- ✅ Tap marker → info window with title/price
- ✅ Tap info window → navigate to detail
- ✅ Camera auto-fits all markers on launch
- ✅ Current location button (optional but nice)
- ✅ Filter by category updates markers

---

---

## 🎨 Figma Design System

**Figma File:** [eMali Estates Designs](https://www.figma.com/design/TquEc9YNZ33iIfhWlrKRXV/?node-id=0-1&p=f&t=Nsgu0oaz7E8JbHbz-0)

### Design Tokens to Extract
- **Colors:** Primary, secondary, background, surface, on-surface, error, etc.
- **Typography:** Font families (likely Inter/Roboto), sizes, weights for H1-H6, body, caption
- **Spacing:** 4dp grid system (4, 8, 12, 16, 24, 32, 48, etc.)
- **Components:**
  - Buttons (primary, secondary, outline, text)
  - Text fields (outlined, filled)
  - Cards (property cards, video cards)
  - Bottom navigation bar
  - Floating action button (FAB)
  - Chips (amenities, categories)
  - Dialogs (payment, filter)
  - Bottom sheets
  - Loading indicators
  - Avatars (verified badge)

### Screens to Implement (from Figma)
1. **Authentication Suite:** Login, Signup, Forgot Password, OTP
2. **Onboarding:** (if any - not in epics)
3. **Home:** Property listing (grid/list toggle), featured section
4. **Property Detail:** Image carousel, description, amenities, contact actions
5. **Map:** Full-screen map with pins, search bar, filter button
6. **eTours:** Vertical video feed with interaction overlays
7. **Profile:** User info, verification status, my properties, favorites, settings
8. **My Properties:** List with edit/delete, add new property flow
9. **Add/Edit Property:** Multi-step form with image upload
10. **Search & Filters:** Search bar, filter panel
11. **Favorites:** Saved properties grid
12. **Payments:** Lock modal, checkout, receipt
13. **Identification:** Camera capture, OCR preview, status tracking

---

## 🔧 Critical Gaps & Blockers

### Mobile Development Gaps

1. **Permissions Handling**
   - Camera for ID upload & property images
   - Location for map search
   - Gallery for image picker
   - Need to implement platform-specific permission requests in KMM

2. **Image Picker & Camera**
   - Library: `compose-multiplatform-image-picker` or platform-specific APIs
   - Compress images before upload (reduce to <5MB for backend)

3. **Video Playback**
   - Research KMM-compatible video player (ExoPlayer Android, AVPlayer iOS)
   - Compose wrapper for seamless integration

4. **Payment SDKs**
   - Flutterwave or Stripe mobile SDKs not KMM-native
   - May need expect/actual implementations for Android/iOS

5. **Google Maps in Compose Multiplatform**
   - Use `compose-maps` library (Google Maps SDK for Android, Google Maps iOS SDK)
   - Requires API keys for both platforms

6. **Push Notifications**
   - Firebase Cloud Messaging (FCM) for Android
   - Apple Push Notification Service (APNs) for iOS
   - Need KMP wrapper or platform-specific implementations

7. **Offline-First Strategy**
   - Currently: PropertySDK has 5-minute cache
   - Need: Queue for mutations when offline (create property, favorites)
   - Need: Better conflict resolution on sync

---

### Backend Dependencies (To Complete)

1. **GPS/Search Endpoint** (`/properties/nearby`)
   - Implement haversine formula or PostGIS `ST_DWithin`
   - Add GIST index on Location(lat, lng)
   - Test with large dataset

2. **Video Module**
   - Create Video model in Prisma
   - Upload endpoint (S3 presigned URL)
   - Streaming endpoint with range requests
   - Trending algorithm service

3. **Payment Unlock**
   - Add PaymentUnlock model linking user, property, payment
   - Modify Property response to mask contact info based on unlock status
   - Add middleware to check unlock before returning sensitive fields
   - Webhook handler for payment confirmation

4. **Real-time Notifications (WebSocket)**
   - Setup Socket.io or native WebSocket in Nest
   - Events: new property, price change, message, verified, etc.
   - iOS/Android clients to maintain persistent connection

---

## 🧪 Testing Strategy

### Unit Tests (Kotlin)
- **ViewModel Tests:** Test business logic with fake repositories
- **UseCase/Interactor Tests:** If using clean architecture
- **Repository Tests:** With in-memory database

### Integration Tests
- **API Integration:** MockWebServer for Ktor responses
- **Database Tests:** SQLDelight with in-memory driver

### UI Tests (Compose)
- **Compose Testing:** Create UI tests for critical screens
- **Navigation Testing:** Test navigation graph flows
- **Screenshot Tests:** Ensure pixel-perfect Figma compliance

### Backend Tests (Already Exists)
- ✅ Unit tests with Jest (in `test/` and alongside modules)
- ✅ E2E tests with supertest
- ✅ Database seeding for test isolation

---

## 📦 Configuration & Secrets

### BuildKonfig (Mobile)
Properties in `local.properties` ( NEVER commit):
```properties
webClientId=your_google_oauth_client_id
webClientSecret=your_google_oauth_client_secret
supabaseUrl=your_supabase_url
supabaseKey=your_supabase_anon_key
apiEndpoint=https://api.emaliestates.com
```

### Backend `.env`
```env
DATABASE_URL=postgresql://user:pass@host:5432/emali
JWT_SECRET=your_jwt_secret_32_chars_minimum
GOOGLE_CLIENT_ID=your_google_oauth_client_id
GOOGLE_CLIENT_SECRET=your_google_oauth_client_secret
MAILGUN_API_KEY=your_mailgun_key
MAILGUN_DOMAIN=your.domain.com
MOMO_API_KEY=your_momo_key
S3_BUCKET=emali-property-images
AWS_REGION=eu-west-1
AWS_ACCESS_KEY_ID=your_key
AWS_SECRET_ACCESS_KEY=your_secret
FRONTEND_URL=https://app.emaliestates.com
```

---

## 🚀 Milestones & Timeline (12 Weeks)

| Week | Milestone | Deliverables |
|------|-----------|--------------|
| **W1-3** | Phase 1: Property Browse | Home listing, detail, search, create/edit, owner dashboard |
| **W4** | Phase 2: Engagement | Favorites, map search basics, verification upload |
| **W5-6** | Phase 2 Complete | GPS Nearby, verification flow complete |
| **W7-8** | Phase 3: Advanced | Video feed, payment unlock |
| **W9** | Phase 4: Polish | UI polish, accessibility, animations |
| **W10** | QA & Testing | Cross-device testing, bug bash, performance profiling |
| **W11** | Deployment Prep | Release builds, Play Console, TestFlight, monitoring setup |
| **W12** | Beta Launch | Internal beta, final fixes, production ready |

---

## ✅ Success Metrics (from Jira)

### From SCRUM-2 (Auth)
- 95% first login success rate
- <2% auth-related API error rate
- 90% OTP verification completion on first attempt
- <3 min average password reset time

### From SCRUM-3 (Identification)
- 90% successful ID upload + OCR extraction
- <5% manual correction rate
- <24h average verification turnaround

### From SCRUM-4 (Properties)
- 90% property creation success rate
- <3s average search response time
- 80% of properties have complete metadata & images
- >4.3/5 user satisfaction for browsing

### From SCRUM-7 (Payments)
- 95% payment success rate
- <30s average unlock time after payment
- <2% disputes/refunds

---

## 📚 Resources & References

### Documentation
- [Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)
- [Nest.js Docs](https://docs.nestjs.com/)
- [Prisma Docs](https://www.prisma.io/docs/)

### Repository Structure
- Mobile: `/home/igor/AndroidStudioProjects/emali-App/`
- Backend: `~/Documents/Backends/eMali_backend/`
- Figma: https://www.figma.com/design/TquEc9YNZ33iIfhWlrKRXV/

### API Documentation
- Backend Swagger: `http://localhost:3000/api-docs` (when backend running)
- Postman Collection: Request from backend team

---

## 🎯 Next Immediate Actions

1. **Pick up from current state:** Auth screens done → Start **Home/Property Listing** implementation
2. **Review Figma designs** with designer to confirm:
   - Property card layout & specs
   - Color palette & typography
   - Detail page wireframes
   - Create/Add property flow
3. **Backend coordination:**
   - Confirm `/properties/nearby` endpoint implementation plan
   - Discuss video upload & streaming architecture
   - Finalize payment provider and unlock flow
4. **Setup tasks:**
   - Create GitHub project board or Jira tickets for remaining subtasks
   - Break down each sprint into 1-3 day tasks
   - Assign owners if team > 1 developer
5. **Environment:**
   - Ensure backend is running locally (or staging)
   - Update `local.properties` with API endpoint
   - Test existing auth endpoints working

---

## 📝 Notes

- The app follows **Clean Architecture** principles loosely (data, domain, presentation layers)
- **State management:** ViewModel exposing `StateFlow` / custom `RequestState` sealed class
- **Error handling:** Consistent `RequestState.Error` with user-friendly messages
- **Caching:** PropertySDK implements 5-minute cache with SQLDelight
- **Navigation:** Navigation Compose with nested graphs (auth vs base)
- **Authentication:** JWT with auto-refresh via `HttpClientFactory` interceptor
- **Images:** Coil with Ktor network fetcher for cross-platform

---

**Keep this README updated as progress is made. Mark completed sprints, update percentages, and note blockers.**

**Questions?** Reach out to the ShortSpark team or refer to CLAUDE.md files in both mobile and backend repos.

</code>