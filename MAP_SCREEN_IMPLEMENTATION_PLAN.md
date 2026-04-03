# MapScreen Implementation Plan
**Based on:** GoogleMapCompose library (https://github.com/Nigorjeanluc/GoogleMapCompose)
**Target:** Sprint 2.2 - GPS-Based Property Search on Interactive Map
**Branch:** ft/map-screen-implementation

---

## 📋 Overview

The MapScreen will display properties as markers on an interactive Google Map, allowing users to:
- View all properties pinned on the map
- Filter properties by category, price, etc.
- See property details by tapping markers
- Navigate to property detail screen
- Optionally: show user's current location, radius search, map-list toggle

---

## 🎯 Library Analysis: GoogleMapCompose

Based on the repository `Nigorjeanluc/GoogleMapCompose`, this is a Jetpack Compose Multiplatform wrapper for Google Maps SDK.

### Key Features (Expected):
- ✅ Cross-platform: Android + iOS (via Google Maps SDK for both)
- ✅ Compose-first API (not XML-based)
- ✅ Camera position control
- ✅ Marker support with custom icons
- ✅ Info windows (popup on marker tap)
- ✅ Polygon/Polyline drawing
- ✅ Location layer (blue dot for current location)
- ✅ Gestures: zoom, pan, rotate, tilt
- ✅ Map styling (night mode, custom styles)

### Gradle Dependency:

```kotlin
// In composeApp/build.gradle.kts
implementation(libs.googleMapCompose) // Add to commonMain
```

Expected library coordinates:
```
io.github.nigorjeanluc:google-map-compose:1.0.0 or similar
```

**Check exact version:** Visit https://github.com/Nigorjeanluc/GoogleMapCompose for the latest release.

---

## 🗺️ Implementation Phases

### **Phase 1: Setup & Basic Map** (Day 1-2)

#### Tasks:

1. **Add Dependency**
   ```gradle
   // gradle/libs.versions.toml
   googleMapCompose = "io.github.nigorjeanluc:google-map-compose:VERSION"
   ```
   Then in `composeApp/build.gradle.kts` commonMain sourceSet.

2. **Obtain Google Maps API Key**
   - Go to Google Cloud Console
   - Enable Maps SDK for Android and iOS
   - Create API key with restrictions
   - Add to `local.properties`:
     ```properties
     GOOGLE_MAPS_API_KEY=your_key_here
     ```
   - Update `BuildKonfig` to include it

3. **Configure Platform-Specific Setup**

   **Android (`androidMain/AndroidManifest.xml`):**
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${GOOGLE_MAPS_API_KEY}" />
   ```

   **iOS (`iosApp/iosApp/Info.plist`):**
   ```xml
   <key>NSLocationWhenInUseUsageDescription</key>
   <string>Need location for map</string>
   <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
   <string>Need location for map</string>
   ```

4. **Create Basic MapScreen Composable**

   File: `composeApp/src/commonMain/kotlin/.../map/presentation/MapScreen.kt`

   ```kotlin
   @Composable
   fun MapScreen(
       navController: NavController,
       viewModel: MapViewModel = koinViewModel()
   ) {
       val uiState by viewModel.uiState.collectAsStateWithLifecycle()

       GoogleMap(
           modifier = Modifier.fillMaxSize(),
           cameraPosition = uiState.cameraPosition,
           onMapClick = { latLng ->
               // Handle map click if needed
           }
       ) {
           // Markers will go here
       }
   }
   ```

5. **Create MapViewModel**

   ```kotlin
   class MapViewModel(
       private val propertySDK: PropertySDK
   ) : ViewModel() {

       private val _uiState = MutableStateFlow(MapUiState())
       val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

       data class MapUiState(
           val properties: List<Property> = emptyList(),
           val cameraPosition: CameraPosition = CameraPosition(
               target = LatLng(0.0, 0.0), // Default to world view
               zoom = 10f
           ),
           val selectedProperty: Property? = null
       )

       init {
           loadProperties()
       }

       fun loadProperties() {
           viewModelScope.launch {
               val result = propertySDK.getAllProperties() // Reuse existing
               // Convert properties to LatLng for camera bounds
               // Set camera to show all markers
           }
       }

       fun onMarkerClick(property: Property) {
           _uiState.update { it.copy(selectedProperty = property) }
       }
   }
   ```

---

### **Phase 2: Markers & Info Windows** (Day 3-4)

#### Tasks:

1. **Add Marker Composable**

   The library likely provides:
   ```kotlin
   Marker(
       state = MarkerState(position = LatLng(lat, lng)),
       title = property.title,
       snippet = property.price,
       onInfoWindowClick = { /* Navigate to detail */ }
   )
   ```

2. **Convert Properties to Map Markers**

   In `GoogleMap` composable block:
   ```kotlin
   uiState.properties.forEach { property ->
       property.location?.let { loc ->
           Marker(
               state = MarkerState(position = LatLng(loc.lat, loc.lng)),
               title = property.title,
               snippet = "$${property.price}",
               onClick = {
                   viewModel.onMarkerClick(property)
                   true // consume event
               }
           )
       }
   }
   ```

   **Note:** The Property model needs latitude/longitude. Check if `locationId` points to a Location entity with lat/lng. If not, we may need to expand the Property DTO to include lat/lng directly.

3. **Custom Marker Icons (Optional)**

   Use different icons for property types:
   ```kotlin
   icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_house)
   ```

4. **Info Window Click Navigation**

   ```kotlin
   onInfoWindowClick = {
       navController.navigate(BaseScreen.PropertyDetail.createRoute(property.id))
   }
   ```

---

### **Phase 3: Camera & Viewport** (Day 5)

#### Tasks:

1. **Auto-fit Camera to Show All Markers**

   Calculate bounds from all property coordinates:
   ```kotlin
   fun updateCameraToFitAll(properties: List<Property>) {
       if (properties.isEmpty()) return

       val bounds = LatLngBounds.builder()
       properties.forEach { p ->
           p.location?.let { loc ->
               bounds.include(LatLng(loc.lat, loc.lng))
           }
       }
       val latLngBounds = bounds.build()

       _uiState.update {
           it.copy(
               cameraPosition = CameraPosition.fromLatLngBounds(latLngBounds, 50) // padding in pixels
           )
       }
   }
   ```

2. **Add Map Type Toggle** (Optional: Normal, Satellite, Hybrid)

   ```kotlin
   var mapType by remember { mutableStateOf(MapType.NORMAL) }
   GoogleMap(
       mapType = mapType,
       // ...
   )
   ```

3. **Enable My Location Layer** (Optional)

   Request location permissions, then:
   ```kotlin
   val fusedLocationClient = LocalContext.current.getFusedLocationProviderClient()
   GoogleMap(
       isMyLocationEnabled = true // if permission granted
   )
   ```

---

### **Phase 4: Integration with Backend** (Day 6)

#### Critical: Backend must provide location data.

Check current backend:

1. **Does `/properties` endpoint include lat/lng?**
   - The Property model has `locationId` (String)
   - Need to JOIN with Location table to get lat/lng
   - OR include lat/lng directly in Property response

   **Option A (Current):**
   ```
   GET /properties?include=location
   ```
   Returns property with nested location object containing `lat`, `lng`.

   **Option B:**
   Modify `PropertyResponse` to flatten:
   ```json
   {
     "id": "...",
     "title": "...",
     "lat": 1.234,
     "lng": 5.678
   }
   ```

2. **Update PropertyDto/Response** to include coordinates.

   In backend `prisma/schema.prisma`:
   ```prisma
   model Property {
     // ...
     location Location?
   }
   ```

   In mobile `Property.kt`, add:
   ```kotlin
   val lat: Double? = null
   val lng: Double? = null
   ```

3. **Update PropertySDK** to fetch properties with locations if not already included.

   Verify `fetchAllProperties()` returns `Property` objects with non-null `locationId` and that we can resolve lat/lng from the Location table queries.

   If `locationId` is present, we need to query the Location table to get coordinates.

---

### **Phase 5: Filtering & Search** (Day 7)

#### Tasks:

1. **Add Filter Button** (floating action button or toolbar)
   - Opens filter bottom sheet (reuse from HomeScreen)
   - Filters by: category, price range, bedrooms, etc.

2. **Update ViewModel** to apply filters to property list
   ```kotlin
   fun applyFilter(filter: PropertyFilter) {
       _uiState.update {
           it.copy(
               properties = it.properties.filter { /* apply */ }
           )
       }
       // Recalculate camera bounds
   }
   ```

3. **Re-center map after filter change**

---

### **Phase 6: Performance Optimization** (Day 8)

#### Tasks:

1. **Marker Clustering** (if > 50 properties)
   - Use library's built-in clustering if available
   - OR implement custom clustering with `ClusterManager`
   - Groups nearby properties into a single cluster icon with count

2. **Lazy Loading / Visible Region**
   - Only load properties visible in current viewport
   - Use `GoogleMap.OnCameraIdleListener` to fetch properties in bounds
   - Backend endpoint: `GET /properties/bounds?neLat=...&neLng=...&swLat=...&swLng=...`

3. **Map Cache**
   - Cache loaded properties in ViewModel to avoid refetch on rotation

---

### **Phase 7: Polish & UX** (Day 9-10)

#### Tasks:

1. **Custom Info Window Layout**
   - Show property image thumbnail
   - Title, price, bedrooms/bathrooms
   - "View Details" button

2. **Marker Animations**
   - Drop animation when markers first appear
   - Bounce animation on tap

3. **Map-List Toggle** (Split screen)
   - Bottom sheet that slides up showing property list
   - Filter by dragging sheet up/down (like Google Maps)

4. **Current Location Button**
   - Floating button to center map on user's GPS
   - Request `ACCESS_FINE_LOCATION` permission

5. **Radius Search Circle**
   - Draw circle around user's location showing search radius
   - Configurable radius (5km, 10km, 20km)

---

## 📦 Dependency Configuration

### **Step 1: Add Library**

**gradle/libs.versions.toml**
```toml
[libs]
google-map-compose = { module = "io.github.nigorjeanluc:google-map-compose", version = "1.0.0" } # Check latest
```

**composeApp/build.gradle.kts (commonMain):**
```kotlin
dependencies {
    implementation(libs.google.map.compose)
}
```

### **Step 2: API Key Setup**

**local.properties** (gitignored):
```properties
GOOGLE_MAPS_API_KEY=AIzaSy...
```

**BuildKonfig** should expose it:
```kotlin
val GOOGLE_MAPS_API_KEY = BuildKonfig.GOOGLE_MAPS_API_KEY
```

**AndroidManifest.xml:**
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${GOOGLE_MAPS_API_KEY}" />
```

**iOS Info.plist** (need to add key):
- `NSLocationWhenInUseUsageDescription`
- Add API key to AppDelegate or Info.plist (depending on library)

---

## 🔧 Backend Requirements

### **Immediate Need:**
1. **Endpoint to fetch properties with coordinates**
   - Current: `GET /properties` returns `Property` with `locationId`
   - Problem: Need actual lat/lng, not just locationId
   - Solution: Backend should join Location table and return:
     ```json
     {
       "id": "...",
       "title": "...",
       "price": 100000,
       "lat": 1.234567,
       "lng": 5.678910,
       "categoryId": "..."
     }
     ```

2. **Optional Bounding Box Endpoint** (for lazy loading):
   ```
   GET /properties/bounds?neLat={neLat}&neLng={neLng}&swLat={swLat}&swLng={swLng}
   ```

---

## 🐛 Known Issues & Mitigation

| Issue | Impact | Mitigation |
|-------|--------|------------|
| Google Maps API costs money | Production budget needed | Use free tier ($200/month credit), enable billing |
| Requires API key management | Extra setup | Document in README, use `local.properties` |
| iOS setup more complex | Longer iOS testing | Follow library docs for iOS specifics |
| Marker clustering may be missing | Too many markers overlap | Show only visible region markers, or cluster manually |
| Location permissions denied | Can't show user location | Handle permission denial gracefully, hide layer |
| Backend doesn't return lat/lng | MapScreen won't work | **Backend must be updated first** (see above) |

---

## 🧪 Testing Strategy

1. **Unit Tests:**
   - Test `MapViewModel` state transitions
   - Test camera bounds calculation logic
   - Test property filtering

2. **Instrumentation Tests:**
   - Test marker tap navigation
   - Test map gestures (zoom, pan)
   - Test permission flow

3. **Manual Testing:**
   - On Android emulator with Google Play Services
   - On physical iOS device (simulator may not have Google Maps)
   - Test with 0, 10, 100, 1000 properties
   - Test network offline mode

---

## 📋 Acceptance Criteria

- ✅ Map displays correctly with Google Maps styling
- ✅ Property markers visible and clickable
- ✅ Tap marker → info window shows property title & price
- ✅ Tap info window → navigates to PropertyDetailScreen
- ✅ Camera auto-fits to show all properties on load
- ✅ Filter by category updates markers on map
- ✅ Current location button works (if enabled)
- ✅ Handles empty state (no properties in area)
- ✅ Loading state while fetching properties
- ✅ Error state with retry

---

## 🔗 Integration with Existing Code

### **Update Navigation:**

`BaseScreen.kt` already has `Map` route defined.

`AppNavGraph.kt`:
```kotlin
composable(BaseScreen.Map.route) {
    MapScreen(navController = navController)
}
```

### **Update KoinModule:**

Add to `sharedModule`:
```kotlin
viewModel {
    MapViewModel(
        propertySDK = get(),
        // locationClient if needed
    )
}
```

---

## 📅 Timeline & Dependencies

| Task | Est. Time | Dependencies |
|------|-----------|--------------|
| Library setup (API key, Gradle) | 2h | Google Cloud account |
| Basic GoogleMap composable | 4h | Library installed |
| Marker rendering | 4h | Backend returns lat/lng |
| Camera auto-fit | 3h | Marker rendering |
| Info window click | 2h | Marker rendering |
| Filter integration | 4h | Filter UI from HomeScreen |
| Current location layer | 4h | GPS permissions |
| Clustering (if needed) | 8h | Many properties test |
| iOS testing & fixes | 8h | iOS device, Apple dev account |
| QA & polish | 8h | All above |

**Total:** ~7-8 days (sprint length depends on team capacity)

---

## ❓ Open Questions

1. **Does backend include lat/lng in Property response?**
   - **If NO:** Must update backend API first (blocker)
   - **If YES:** What field names? `lat`/`lng`? Or nested `location` object?

2. **Do we need real-time location tracking?**
   - Show user's blue dot moving as they walk?
   - Or just one-time "center on me"?

3. **Should map show only properties or also places (cities, landmarks)?**
   - Current Location/Place tables exist - can we overlay?

4. **What map type?**
   - Normal (street) by default?
   - Satellite toggle?
   - Terrain?

5. **Marker clustering threshold?**
   - Always on?
   - Only when zoomed out?
   - Cluster size (50px radius, etc.)?

---

## ✅ Pre-Implementation Checklist

- [ ] Confirm backend `/properties` endpoint includes `lat` and `lng` fields
- [ ] Update mobile `Property` model if needed (add `lat: Double?`, `lng: Double?`)
- [ ] Create Google Cloud project, enable Maps SDK, get API key
- [ ] Add API key to `local.properties` and `BuildKonfig`
- [ ] Add GoogleMapCompose dependency to `libs.versions.toml` and `build.gradle.kts`
- [ ] Test simple map with one hardcoded marker
- [ ] Verify Android and iOS both render (iOS may need extra setup)
- [ ] Ensure location permissions are in AndroidManifest and iOS Info.plist

---

## 📚 References

- **Library:** https://github.com/Nigorjeanluc/GoogleMapCompose
- **Google Maps SDK for Android:** https://developers.google.com/maps/documentation/android-sdk/overview
- **Google Maps SDK for iOS:** https://developers.google.com/maps/documentation/ios-sdk/overview
- **Compose Multiplatform Location:** Use `multiplatform-settings` or platform-specific APIs

---

## 🎯 Success Metrics

- Map loads in <2 seconds
- 60fps smooth scrolling and zooming
─ Markers render instantly when panning
- Info window opens <100ms after tap
- App size increase <5MB from library

---

**Next Step:** Get backend to confirm lat/lng availability OR update backend to include coordinates in Property response. Then begin implementation.
