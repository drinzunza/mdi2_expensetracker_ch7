# Expense Tracker Bottom Navigation Design

## Objective

Add a Material 3 bottom navigation bar with four top-level destinations: Expenses, Dashboard, Categories, and Settings. Keep the existing expense list and editor flow working, and hide the bottom navigation bar while creating or editing an expense.

## Scope

This milestone adds the navigation shell only. Dashboard, Categories, and Settings start as simple placeholder screens. Category persistence, category creation, and the expense-category dropdown belong to the next milestone.

## Navigation approach

Use the existing Navigation Compose library and one `NavHostController`. A root `Scaffold` owns the bottom `NavigationBar`, while the existing `NavHost` owns all routes.

Top-level routes:

- `expenses`
- `dashboard`
- `categories`
- `settings`

Detail routes:

- `expense/new`
- `expense/{id}`

The bottom bar is visible only when the current route is one of the four top-level routes. It is hidden on both detail routes.

## Components

### Top-level destination model

A small model defines the route, label, and local vector drawable for each tab. A single ordered list controls the order shown in the navigation bar.

### ExpenseNavigationBar

A reusable composable receives the current destination and a destination-selection callback. It renders one `NavigationBarItem` per top-level destination and derives the selected state from the active navigation route.

### Placeholder screens

Dashboard, Categories, and Settings each receive a minimal screen with the shared `ExpenseTopAppBar` and explanatory content. These screens establish stable destinations without introducing unfinished business logic.

### ExpenseNavHost

`ExpenseNavHost` retains ownership of the `NavHostController`. Its root `Scaffold` renders `ExpenseNavigationBar` conditionally. The `NavHost` consumes the scaffold padding so content does not appear under the navigation bar.

## Navigation behavior

Selecting a tab navigates to its top-level route with:

- `launchSingleTop = true` to avoid duplicate copies.
- `restoreState = true` to restore a previously selected destination.
- `popUpTo` the graph start destination with `saveState = true` to keep the top-level back stack controlled.

The selected tab is derived from `currentBackStackEntryAsState`; it is not stored separately. This prevents the visual selection from disagreeing with the actual route.

Opening New Expense or Edit Expense continues to use the current routes. Returning from either editor returns to Expenses and reveals the bottom navigation bar again.

## Icons

Use four local vector drawables loaded with `painterResource`. This avoids adding the large, legacy `material-icons-extended` artifact. Each icon receives a content description matching its tab label.

## Insets and layout

The root `Scaffold` passes its `innerPadding` to the `NavHost` through `Modifier.padding(innerPadding)`. Material 3's `NavigationBar` handles bottom system insets.

## Verification

Verify the following behaviors:

1. The app starts on Expenses and highlights Expenses.
2. Each tab opens its matching screen and becomes selected.
3. Repeatedly selecting a tab does not create duplicate destinations.
4. Switching tabs and returning restores the destination state supported by the current screen.
5. New Expense and Edit Expense hide the bottom bar.
6. Back from an editor returns to Expenses and restores the bottom bar.
7. The app compiles and launches without adding another dependency.

