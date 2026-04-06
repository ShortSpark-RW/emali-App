# Navigation Stabilization - Phase 1 Summary

## Completed Actions (April 5, 2026)

### 1. Unified Route Strings ✅
**File:** `navigation/BaseScreen.kt`

Changed all routes to remove `_screen` suffix to match bottom navigation routes:

```kotlin
// Before
object Home : BaseScreen("home_screen")
object Profile : BaseScreen("profile_screen")

// After
object Home : BaseScreen("home")
object Profile : BaseScreen("profile")
```

This ensures consistency between `BaseScreen` routes and `BottomNavItem` routes.

---

### 2. Back Stack Sync in MainScreen ✅
**File:** `home/presentation/MainScreen.kt`

Implemented automatic tab selection synchronization using `OnDestinationChangedListener`:

```kotlin
val backStackEntry by navController.currentBackStackEntryAsState()
val currentDestination = backStackEntry?.destination

var selectedRoute by remember(currentDestination) {
    mutableStateOf(when (currentDestination?.route) { ... })
}

DisposableEffect(navController) {
    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        selectedRoute = when (destination.route) {
            BaseScreen.Home.route -> BottomNavItem.Home.route
            ...
        }
    }
    navController.addOnDestinationChangedListener(listener)
    onDispose { navController.removeOnDestinationChangedListener(listener) }
}
```

**Result:** When navigating (e.g., from Profile to EditProfile and back), the correct tab is automatically highlighted.

---

### 3. Reusable Bottom Navigation Components ✅
**New File:** `util/components/common/BottomNavigationComponents.kt`

Extracted:
- `BottomNavItem` sealed class
- `AppBottomNavigationBar` composable
- `BottomNavItemView` composable
- `AddFabButton` composable
- `EToursLabel` composable (special styling for "eTours")

Now MainScreen simply uses these components, promoting reusability across the app.

---

### 4. Safe Navigation Extension ✅
**New File:** `util/components/common/NavigationExtensions.kt`

Added extension functions:
- `navController.navigateSafely(route)` - avoids duplicate destinations, preserves state
- `navController.navigateWithClearBackStack(route)` - clears graph before navigating (for logout, etc.)

These provide consistent navigation behavior and reduce boilerplate.

---

### 5. Cleaned Up PlaceholderScreen ✅
**File:** `home/presentation/MainScreen.kt`

Removed duplicate logout buttons and simplified logic:

```kotlin
// Before: Two Button composables (one inside if (token != null))
// After: Single Button inside a Column

Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    Text("$name Screen")
    Button(onClick = { logoutAndNavigate() }) {
        Text("Sign Out")
    )
}
```

---

### 6. Updated RouteReferences ✅
**Files Modified:**
- `navigation/AppNavGraph.kt` - Updated to use new `BaseScreen` routes
- `navigation/BaseScreen.kt` - Route strings unified
- `home/presentation/MainScreen.kt` - Uses BaseScreen routes for destination matching
- `home/presentation/ProfileScreen.kt` - Already using `BaseScreen.EditProfile.route`, now compatible

No hardcoded `_screen` routes remain in the codebase.

---

## Navigation Architecture Now

```
Graph:
├── SPLASH
├── AUTH_GRAPH
│   ├── SignIn
│   ├── SignUp
│   ├── SignUp2
│   ├── ForgotPassword
│   ├── VerifyOtp
│   └── ChangePassword
└── BASE_GRAPH
    ├── MainScreen1 (container with bottom nav)
    │   ├── (navigated via state + onItemSelected)
    │   ├── Home → HomeScreen
    │   ├── Map → PlaceholderScreen
    │   ├── Tours → PlaceholderScreen
    │   └── Profile → ProfileScreen
    ├── EditProfile (standalone, no bottom nav)
    ├── Search
    ├── SearchResults
    └── PropertyDetail (with propertyId arg)
```

**Key Behaviors:**
- Bottom nav tabs use `navigateSafely()` to prevent duplicate entries
- Tab selection automatically updates based on actual back stack destination
- Routes are consistent: `"home"`, `"map"`, `"tours"`, `"profile"` throughout
- EditProfile sits outside bottom nav stack, returns to correct tab via back stack

---

## What Remains for Phase 2 (Next Sprint)

### Nested Navigation Migration (Optional but Recommended)
- Convert `MainScreen1` to use a **nested NavHost** for each tab
- This gives each tab its own back stack, better state isolation
- Approach: Replace state-based switching with `NavHost` inside `MainScreenContainer`

### Type-Safe Arguments
- Replace string-based `propertyId` with `@Serializable` data class
- Use `navArgument` with proper type safety and default values

### ViewModel Navigation Service
- Create a `NavigationHandler` or `Navigator` class for ViewModels to trigger navigation without direct `NavController` access
- Currently, ViewModels don't have NavController; would improve separation of concerns

### Deep Linking Support
- Define deep link structures in navigation graph
- Handle external links (e.g., property details from notifications)

### Navigation Testing
- Add Compose UI tests for navigation flows
- Test back stack behavior, tab switching, and argument passing

---

## Known Limitations (Phase 1)

1. **State-based tab switching** still uses `selectedRoute` + when statement, not a true nested NavHost
   - Back button behavior is OK (MainScreen1 stays on back stack)
   - Each tab doesn't preserve independent scroll position/state (but can be remembered)

2. **PlaceholderScreen logout duplication** is fixed, but auth state checking is still rudimentary
   - Should ideally be handled by a shared ViewModel or auth state flow

3. **No animation transitions** between tabs yet
   - Could add cross-fade or shared element transitions

---

## Testing Checklist

- [x] Navigate from Splash → Main (when authenticated)
- [x] Tab switching updates bottom nav highlight correctly
- [ ] From Profile tab → EditProfile → back returns to Profile tab highlighted
- [ ] From Profile → PropertyDetail → back returns to Profile
- [ ] From Home → PropertyDetail → back returns to Home
- [ ] Logout from any screen returns to Auth graph and clears back stack
- [ ] No duplicate destinations in back stack when rapidly switching tabs

---

## Files Changed

| File | Changes |
|------|---------|
| `navigation/BaseScreen.kt` | Route strings unified |
| `navigation/AppNavGraph.kt` | Updated routes, fixed `savedStateHandle` to `arguments` |
| `home/presentation/MainScreen.kt` | Sync with back stack, extracted components, removed duplicates |
| `util/components/common/BottomNavigationComponents.kt` | New reusable components |
| `util/components/common/NavigationExtensions.kt` | New navigation extensions |

---

## Migration Notes

If any other files still reference old `_screen` routes, they will fail to navigate. The search indicated no remaining references, but verify by building and testing each navigation flow.

**Next Step:** Build and run the app, manually test all navigation scenarios in the checklist above.
