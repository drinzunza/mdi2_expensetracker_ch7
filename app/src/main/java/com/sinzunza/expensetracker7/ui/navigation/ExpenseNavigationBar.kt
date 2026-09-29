package com.sinzunza.expensetracker7.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun ExpenseNavigationBar(
    currentRoute: String?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        TopLevelDestination.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    onDestinationSelected(destination)
                },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconRes),
                        contentDescription = destination.label,
                    )
                },
                label = {
                    Text(destination.label)
                },
                alwaysShowLabel = true,
            )
        }
    }
}
