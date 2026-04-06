# Navigation Implementation Analysis

## Current Structure (as of April 2026)

### Navigation Graphs
```
AppNavGraph.kt
├── Graph.SPLASH ("splash")
├── Graph.AUTHENTICATION ("auth_graph") [nested]
│   ├── AuthScreen.SignIn ("sign_in_screen")
│   ├── AuthScreen.SignUp ("sign_up_screen")
│   ├── AuthScreen.ForgotPassword ("forgot_password_screen")
│   ├── AuthScreen.VerifyOtp ("verify_otp_screen")
│   ├── AuthScreen.ChangePassword ("change_password_screen")
│   └── AuthScreen.SignUp2 ("sign_up_2_screen")
└── Graph.BASE ("base_graph") [nested]
    ├── BaseScreen.Home ("home_screen") → MainScreen1(navController)
    ├── BaseScreen.Map ("map_screen") → Text("Map Screen")
    ├── BaseScreen.Tours ("tours_screen") → Text("Tours Screen")
    ├── BaseScreen.Profile ("profile_screen") → ProfileScreen(navController)
    ├── BaseScreen.EditProfile ("edit_profile_screen") → EditProfileScreen(navController)
    ├── BaseScreen.Search ("search_screen") → SearchScreen(navController)
    ├── "search/results" → SearchResultsScreen(navController)
    └── BaseScreen.PropertyDetail ("properties/{propertyId}") → PropertyDetailScreen(propertyId, navController)
```

### MainScreen1 Architecture
```kotlin
// Bottom Nav Routes (different from BaseScreen routes!)
sealed class BottomNavItem(
    val route: String,  // "home", "map", "tours", "profile"
    ...
)

// MainScreen1() composable
@Composable
fun MainScreen1(navController: NavController) {
    var selectedRoute by mutableStateOf(BottomNavItem.Home.route)  // STATE-BASED, not navigation

    Scaffold(bottomBar = { AppBottomNavigationBar(...) }) {
        when (selectedRoute) {
            BottomNavItem.Home.route -> HomeScreen(...)
            BottomNavItem.Map.route -> PlaceholderScreen("Map")
            BottomNavItem.Tours.route -> PlaceholderScreen("eTours")
            BottomNavItem.Profile.route -> ProfileScreen(...)
        }
    }
}
```

---

## Identified Issues

### 1. Route Inconsistency
- **BottomNavItem routes**: `"home"`, `"map"`, `"tours"`, `"profile"` (no `_screen` suffix)
- **BaseScreen routes**: `"home_screen"`, `"map_screen"`, `"tours_screen"`, `"profile_screen"`
- This leads to confusion about which route to use for navigation

**Example Problem**:
- From EditProfile, `navController.popBackStack()` returns to `BaseScreen.Profile.route` (`profile_screen`)
- But MainScreen1 expects `"profile"` (without suffix) to show the Profile tab
- Currently works because `ProfileScreen` is both a direct destination AND shown in MainScreen1

### 2. Mixed Navigation Patterns
- **MainScreen1** uses **state-based tab switching** (no NavHost inside)
- **Base graph** defines MainScreen1 as a destination with nested content
- When you navigate to `profile_screen` directly, you bypass MainScreen1 entirely
- This creates two different ways to show ProfileScreen:
  - Via MainScreen1 (tab selection) → stays on back stack as MainScreen1
  - Via direct navigation → standalone destination, no bottom nav

**Back Stack Issues**:
```
Scenario: User is on Profile (via tab) → EditProfile → Save → popBackStack()
Expected: Returns to MainScreen1 with Profile tab selected
Actual: Returns to MainScreen1 BUT selectedRoute is whatever was last selected (could be Home)
```

### 3. PlaceholderScreen Smell
- `PlaceholderScreen` (MainScreen.kt:222) contains logout buttons and auth logic
- Duplicate buttons appear (lines 245-258 and 260-274)
- Should be moved to a proper ViewModel

### 4. Missing Type-Safe Arguments
- Only `PropertyDetail` uses `navArgument` with `savedStateHandle`
- No typed-safe wrappers (Compose Navigation supports Kotlin serialization)
- Arguments are passed as strings without validation

### 5. Navigation Logic Scattered
- Navigation decisions in:
  - `SplashScreen` (AppNavGraph.kt:53-70)
  - `MainScreen1` (MainScreen.kt:80-91)
  - `ProfileScreen` (bottom nav not used, relies on MainScreen1)
  - `PlaceholderScreen` (logout logic)
- No centralized navigation service / handler

---

## Recommended Refactoring

### Option A: Keep Current Pattern, Fix Inconsistencies (QUICK FIX)

**Steps**:
1. **Unify route strings**:
   ```kotlin
   // BaseScreen.kt
   sealed class BaseScreen(val route: String) {
       object Home : BaseScreen(route = "home")     // removed _screen suffix
       object Map : BaseScreen(route = "map")
       object Tours : BaseScreen(route = "tours")
       object Profile : BaseScreen(route = "profile")
       ...
   }
   ```

2. **Add back stack listener to MainScreen1** to sync selectedRoute with actual navigation:
   ```kotlin
   @Composable
   fun MainScreen1(navController: NavController) {
       val backStackEntry by navController.currentBackStackEntryAsState()
       val currentRoute = backStackEntry?.destination?.route

       var selectedRoute by remember {
           mutableStateOf(
               when (currentRoute) {
                   BaseScreen.Home.route -> BottomNavItem.Home.route
                   BaseScreen.Map.route -> BottomNavItem.Map.route
                   BaseScreen.Tours.route -> BottomNavItem.Tours.route
                   BaseScreen.Profile.route -> BottomNavItem.Profile.route
                   else -> BottomNavItem.Home.route
               }
           )
       }

       // Update selectedRoute when navigation occurs
       DisposableEffect(navController) {
           val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
               selectedRoute = when (destination.route) {
                   BaseScreen.Home.route -> BottomNavItem.Home.route
                   ...
                   else -> selectedRoute
               }
           }
           navController.addOnDestinationChangedListener(listener)
           onDispose { navController.removeOnDestinationChangedListener(listener) }
       }

       Scaffold(
           bottomBar = { AppBottomNavigationBar(...) },
           floatingActionButton = { AddFabButton() }
       ) {
           // Render based on selectedRoute OR use nested NavHost (see Option B)
       }
   }
   ```

3. **Remove duplicate logout buttons** from PlaceholderScreen.

4. **Convert PlaceholderScreen** to a proper generic placeholder with proper ViewModel.

---

### Option B: Full Nested Navigation (RECOMMENDED FOR SCALABILITY)

Instead of state-based tab switching, use a **nested NavHost** inside the BASE graph:

```kotlin
// AppNavGraph.kt
navigation(
    route = Graph.BASE,
    startDestination = BaseScreen.MainContainer.route  // New entry point
) {
    // Main container with bottom nav
    composable(BaseScreen.MainContainer.route) { backStackEntry ->
        MainScreenContainer(navController, backStackEntry)  // Contains nested NavHost
    }

    // All tab destinations as separate routes
    composable(BaseScreen.Home.route) { HomeScreen(...) }
    composable(BaseScreen.Map.route) { MapScreen(...) }
    composable(BaseScreen.Tours.route) { ToursScreen(...) }
    composable(BaseScreen.Profile.route) { ProfileScreen(...) }

    // Standalone screens (not in bottom nav)
    composable(BaseScreen.EditProfile.route) { EditProfileScreen(...) }
    composable(BaseScreen.Search.route) { SearchScreen(...) }
    composable("search/results") { SearchResultsScreen(...) }
    composable(BaseScreen.PropertyDetail.route, arguments = ...) { ... }
}
```

```kotlin
// MainScreenContainer.kt (new file)
@Composable
fun MainScreenContainer(navController: NavController, backStackEntry: NavBackStackEntry) {
    val nestedNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = nestedNavController,  // Use nested controller
                items = bottomNavItems
            )
        },
        floatingActionButton = { AddFabButton(...) }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = BaseScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BaseScreen.Home.route) { HomeScreen(...) }
            composable(BaseScreen.Map.route) { MapScreen(...) }
            composable(BaseScreen.Tours.route) { ToursScreen(...) }
            composable(BaseScreen.Profile.route) { ProfileScreen(...) }
        }
    }
}
```

**Benefits of Option B**:
- ✅ Clean separation: nested NavHost handles bottom nav
- ✅ Proper back stack: each tab has its own back stack
- ✅ Consistent routing: all destinations use the same `BaseScreen.route` strings
- ✅ Deep linking works naturally
- ✅ State restoration on configuration change
- ✅ No need to manually sync `selectedRoute` with navigation

**Migration Steps**:
1. Add `BaseScreen.MainContainer` (or rename existing `Home` to be the container)
2. Move MainScreen1's content to `MainScreenContainer`
3. Update `AppNavGraph` to use nested NavHost pattern
4. Remove state-based tab switching; rely on `nestedNavController.navigate(route)`
5. Update bottom nav item click handlers to call `nestedNavController.navigate(route)` with `popUpTo` to avoid building large back stack
6. Remove `selectedRoute` state entirely

---

### Option C: Hybrid (Current Pattern Improved)

If you want to keep state-based switching (simpler, fewer recompositions), at minimum:

1. **Unify route strings** (as in Option A step 1)
2. **Update BaseScreen** to remove `_screen` suffix everywhere
3. **Update bottom nav routes** to use `BaseScreen` routes directly:
   ```kotlin
   sealed class BottomNavItem(
       val screen: BaseScreen,  // Type-safe reference
       val label: String,
       ...
   ) {
       object Home : BottomNavItem(BaseScreen.Home, "Home", ...)
       object Map : BottomNavItem(BaseScreen.Map, "Map", ...)
       ...
   }
   ```
4. **Update AppNavGraph** to remove `MainScreen1` as a destination; instead make BASE graph's start destination the first tab route directly, with MainScreenContainer as the visual wrapper that doesn't need to be a destination itself:
   ```kotlin
   navigation(route = Graph.BASE, startDestination = BaseScreen.Home.route) {
       // Use a container that always shows bottom nav
       composable(BaseScreen.Home.route) { HomeScreen(...) }
       composable(BaseScreen.Map.route) { MapScreen(...) }
       ... // No MainScreen1 destination needed
   }
   ```
   But this would require restructuring how bottom nav is shown.

**Simplest Quick Fix**: Do steps 1-2 (unify route strings) and fix the `selectedRoute` sync with back stack as shown in Option A. This resolves the immediate inconsistency with minimal changes.

---

## Recommended Approach for This Project

Given the current state (mid-development, feature branch `ft/home-screen-refactor`), I recommend:

**Phase 1: Quick Stabilization (Immediate)**
- ✅ Unify route strings: Remove `_screen` suffix from all `BaseScreen` routes
- ✅ Fix `selectedRoute` sync in MainScreen1 using `DisposableEffect` with `OnDestinationChangedListener`
- ✅ Remove duplicate logout buttons from PlaceholderScreen
- ✅ Add `AppBottomNavigationBar` as a reusable component in `util/components/common/NavigationComponents.kt`

**Phase 2: Refactor to Nested Navigation (Sprint)**
- ✅ Create `MainScreenContainer` with nested NavHost
- ✅ Update `AppNavGraph.BASE` to remove `MainScreen1` destination, replace with container
- ✅ Move BottomNavItem definition to a shared file (e.g., `navigation/BottomNavItems.kt`)
- ✅ Add type-safe arguments using `@Serializable` + `navArgument` with default values

**Phase 3: Polish**
- ✅ Add navigation analytics/history tracking
- ✅ Implement proper deep linking support
- ✅ Add navigation unit tests using `compose-test` rule
- ✅ Create a NavigationService/Handler for ViewModel-to-Navigation communication

---

## Type-Safe Navigation Arguments Example

```kotlin
// 1. Define serializable arguments
@Serializable
data class PropertyDetailArgs(val propertyId: String)

// 2. Use in BaseScreen
object PropertyDetail : BaseScreen("properties/{args}") {
    fun createRoute(args: PropertyDetailArgs) = "properties/${args.propertyId}"
}

// 3. Define NavArgument with default/serialization
val propertyDetailArgs = listOf(
    navArgument("args") { type = NavType.StringType }
)

// 4. Retrieve usingSavedStateHandle with type-safe deserialization
@Composable
fun PropertyDetailScreen(viewModel: PropertyDetailViewModel = hiltViewModel()) {
    val args = PropertyDetailArgs.fromNavArguments(/* get from navController */)
    // Or use: val args = navController.previousBackStackEntry?.arguments?.get("args") as String
}
```

---

## Centralized Navigation Helper

Create a `NavigationUtils.kt`:

```kotlin
object NavigationUtils {
    fun NavController.navigateSafely(route: String) {
        try {
            navigate(route) {
                launchSingleTop = true
                restoreState = true
                // Avoid multiple copies of same destination
                popUpTo(graph.startDestinationId) {
                    saveState = true
                }
            }
        } catch (e: Exception) {
            // Log navigation error
        }
    }

    fun NavController.navigateWithClearBackStack(route: String) {
        navigate(route) {
            popUpTo(graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
}
```

---

## Conclusion

The current navigation works but has **route inconsistency** between `BaseScreen` and `BottomNavItem`. Fixing this with **Option A (Quick Fix)** enables stable operation for the current sprint. For long-term maintainability, **Option B (Nested Navigation)** is the recommended architecture.

**Priority Actions**:
1. Match route strings between `BaseScreen` and `BottomNavItem`
2. Sync `selectedRoute` with actual navigation back stack
3. Extract `AppBottomNavigationBar` as a reusable component
4. Remove code duplication (logout buttons)
5. Plan migration to nested NavHost in next sprint
