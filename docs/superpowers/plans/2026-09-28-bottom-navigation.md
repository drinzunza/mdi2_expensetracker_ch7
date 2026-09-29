# Expense Tracker Bottom Navigation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a reusable Material 3 bottom navigation bar for Expenses, Dashboard, Categories, and Settings while hiding it on expense editor routes.

**Architecture:** Keep the existing single `NavHostController` and place its `NavHost` inside a root `Scaffold`. Derive both bottom-bar visibility and selected state from the current route; navigate between top-level destinations with saved state and single-top behavior.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Compose UI tests.

**Spec:** `docs/superpowers/specs/2026-09-28-bottom-navigation-design.md`

## Global Constraints

- Work directly on the currently checked-out branch.
- Do not create branches, worktrees, or commits; leave every change uncommitted for review.
- Continue using the existing Navigation Compose dependency.
- Do not add an icon dependency; use local vector drawables.
- Keep `expense/new` and `expense/{id}` as the existing editor routes.
- Dashboard, Categories, and Settings remain placeholders in this milestone.

## Review Focus

- A `null` or unknown current route must hide the bottom navigation bar; pin this in Task 1's route-rule test.
- Both expense editor routes must hide the bottom bar; pin this in Task 1's route-rule test, including a concrete `expense/42` route.
- Selecting the active tab repeatedly must not duplicate destinations; pin this in Task 4's navigation integration test.
- Returning from New Expense must reveal Expenses and its bottom bar again; pin this in Task 4's navigation integration test.
- All four destinations must have readable labels, selected semantics, and functional click callbacks; pin this in Task 2's Compose test.

---

### Task 1: Top-level destination model and route rules

**Files:**
- Create: `app/src/main/java/com/sinzunza/expensetracker7/ui/navigation/TopLevelDestination.kt`
- Create: `app/src/main/res/drawable/nav_expenses.xml`
- Create: `app/src/main/res/drawable/nav_dashboard.xml`
- Create: `app/src/main/res/drawable/nav_categories.xml`
- Create: `app/src/main/res/drawable/nav_settings.xml`
- Create: `app/src/test/java/com/sinzunza/expensetracker7/ui/navigation/TopLevelDestinationTest.kt`

**Interfaces:**
- Produces: `data class TopLevelDestination(val route: String, val label: String, @DrawableRes val iconRes: Int)`.
- Produces: `val topLevelDestinations: List<TopLevelDestination>` in Expenses, Dashboard, Categories, Settings order.
- Produces: `fun isTopLevelRoute(route: String?): Boolean`.
- Produces: `object ExpenseRoutes` containing `EXPENSES`, `DASHBOARD`, `CATEGORIES`, `SETTINGS`, `NEW_EXPENSE`, `EDIT_EXPENSE`, and `fun editExpense(id: Long): String`.

- [ ] **Step 1: Write the failing destination and visibility unit tests**

Test that the labels and routes appear in the required order. Test `isTopLevelRoute` with all four root routes, `null`, `expense/new`, `expense/{id}`, `expense/42`, and an unknown route.

- [ ] **Step 2: Run the unit test and confirm it fails because the model does not exist**

Run: `./gradlew testDebugUnitTest --tests '*TopLevelDestinationTest'`

Expected: FAIL during test compilation because `TopLevelDestination`, `ExpenseRoutes`, and `isTopLevelRoute` are unresolved.

- [ ] **Step 3: Implement the route constants, ordered destination list, and visibility rule**

Use the exact interfaces above. `isTopLevelRoute` must compare only against the four exact top-level route strings.

- [ ] **Step 4: Add the four 24dp local vector drawables**

Use black single-color vector paths without a resource-level tint; the selected/unselected tint will be supplied by `NavigationBarItem` through `Icon`.

- [ ] **Step 5: Run the unit test and full unit-test suite**

Run: `./gradlew testDebugUnitTest`

Expected: BUILD SUCCESSFUL with `TopLevelDestinationTest` passing.

### Task 2: Reusable Material 3 navigation bar

**Files:**
- Create: `app/src/main/java/com/sinzunza/expensetracker7/ui/navigation/ExpenseNavigationBar.kt`
- Create: `app/src/androidTest/java/com/sinzunza/expensetracker7/ui/navigation/ExpenseNavigationBarTest.kt`

**Interfaces:**
- Consumes: `TopLevelDestination` and `topLevelDestinations` from Task 1.
- Produces: `@Composable fun ExpenseNavigationBar(currentRoute: String?, onDestinationSelected: (TopLevelDestination) -> Unit, modifier: Modifier = Modifier)`.

- [ ] **Step 1: Write the failing Compose UI tests**

Render `ExpenseNavigationBar` with `currentRoute = ExpenseRoutes.EXPENSES`. Assert all four labels exist, Expenses is selected, Dashboard is not selected, and clicking Categories invokes the callback with the Categories destination.

- [ ] **Step 2: Run the instrumented test and confirm it fails because the component does not exist**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.sinzunza.expensetracker7.ui.navigation.ExpenseNavigationBarTest`

Expected: FAIL during android-test compilation because `ExpenseNavigationBar` is unresolved.

- [ ] **Step 3: Implement `ExpenseNavigationBar`**

Render `NavigationBar` and one `NavigationBarItem` per destination. Use `painterResource(destination.iconRes)`, the destination label for both visible text and icon content description, exact route equality for `selected`, and forward the destination through `onDestinationSelected`.

- [ ] **Step 4: Run the component test**

Run the command from Step 2.

Expected: BUILD SUCCESSFUL with the navigation-bar test passing.

### Task 3: Placeholder top-level screens

**Files:**
- Create: `app/src/main/java/com/sinzunza/expensetracker7/ui/screens/DashboardScreen.kt`
- Create: `app/src/main/java/com/sinzunza/expensetracker7/ui/screens/CategoriesScreen.kt`
- Create: `app/src/main/java/com/sinzunza/expensetracker7/ui/screens/SettingsScreen.kt`
- Create: `app/src/androidTest/java/com/sinzunza/expensetracker7/ui/screens/TopLevelPlaceholderScreensTest.kt`

**Interfaces:**
- Consumes: the existing `ExpenseTopAppBar`.
- Produces: `@Composable fun DashboardScreen()`, `@Composable fun CategoriesScreen()`, and `@Composable fun SettingsScreen()`.

- [ ] **Step 1: Write the failing placeholder-screen Compose tests**

Render each screen independently and assert its title and exact explanatory text are visible: `Charts and summaries will appear here.`, `Create and manage expense categories here.`, and `App preferences will appear here.`

- [ ] **Step 2: Run the test and confirm it fails because the screens do not exist**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.sinzunza.expensetracker7.ui.screens.TopLevelPlaceholderScreensTest`

Expected: FAIL during android-test compilation with unresolved screen functions.

- [ ] **Step 3: Implement the three screens**

Each screen uses `Scaffold`, `ExpenseTopAppBar`, and centered content. Use the titles and exact explanatory copy specified in Step 1.

- [ ] **Step 4: Run the placeholder-screen test**

Run the command from Step 2.

Expected: BUILD SUCCESSFUL with all three screen cases passing.

### Task 4: Integrate the root scaffold and navigation behavior

**Files:**
- Modify: `app/src/main/java/com/sinzunza/expensetracker7/ui/navigation/ExpenseNavHost.kt`
- Create: `app/src/androidTest/java/com/sinzunza/expensetracker7/ui/navigation/ExpenseNavHostTest.kt`

**Interfaces:**
- Consumes: `ExpenseRoutes`, `topLevelDestinations`, `isTopLevelRoute`, `ExpenseNavigationBar`, and the three placeholder screens.
- Preserves: `@Composable fun ExpenseNavHost(viewModel: ExpenseViewModel)`.

- [ ] **Step 1: Write the failing navigation integration tests**

Use a lightweight fake `ExpenseDao`, the real `ExpenseRepository`, and `ExpenseViewModel`. Assert the initial Expenses tab and switch through Dashboard/Categories/Settings. Select Dashboard twice and press Back once; Expenses must appear, proving that the second selection did not add another Dashboard entry. Open New Expense, verify the four tab labels are absent, select Back, and verify Expenses plus the tab labels are visible again.

- [ ] **Step 2: Run the navigation test and confirm it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.sinzunza.expensetracker7.ui.navigation.ExpenseNavHostTest`

Expected: FAIL because the four-destination shell is not integrated.

- [ ] **Step 3: Replace private route strings with `ExpenseRoutes`**

Use `ExpenseRoutes.editExpense(id)` when navigating to an existing expense and retain `NavType.LongType` for the `id` argument.

- [ ] **Step 4: Wrap the `NavHost` in the root `Scaffold`**

Observe `currentBackStackEntryAsState`, obtain `currentRoute`, render `ExpenseNavigationBar` only when `isTopLevelRoute(currentRoute)` is true, and apply the scaffold's `innerPadding` to the `NavHost` modifier.

- [ ] **Step 5: Add the three top-level destinations to `NavHost`**

Map `DASHBOARD`, `CATEGORIES`, and `SETTINGS` to their matching screens. Keep Expenses as the start destination.

- [ ] **Step 6: Implement top-level tab navigation options**

On selection call `navigate(destination.route)` with `popUpTo(navController.graph.findStartDestination().id) { saveState = true }`, `launchSingleTop = true`, and `restoreState = true`.

- [ ] **Step 7: Run the navigation integration test and all instrumented tests**

Run: `./gradlew connectedDebugAndroidTest`

Expected: BUILD SUCCESSFUL with editor-route visibility and tab switching tests passing.

- [ ] **Step 8: Run final build and static verification**

Run: `./gradlew testDebugUnitTest assembleDebug`

Expected: BUILD SUCCESSFUL. Confirm `git diff --check` produces no output and inspect `git diff` without committing.
