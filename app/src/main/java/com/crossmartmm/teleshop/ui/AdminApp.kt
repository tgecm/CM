package com.crossmartmm.teleshop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.crossmartmm.teleshop.ui.screens.*
import com.crossmartmm.teleshop.viewmodel.AdminViewModel

sealed class AdminRoute(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : AdminRoute("dashboard", "Dashboard", Icons.Default.SpaceDashboard)
    data object Products : AdminRoute("products", "Products", Icons.Default.Inventory2)
    data object Categories : AdminRoute("categories", "Categories", Icons.Default.Category)
    data object Orders : AdminRoute("orders", "Orders", Icons.Default.ShoppingCartCheckout)
    data object Customers : AdminRoute("customers", "Customers", Icons.Default.PeopleAlt)
    data object Broadcast : AdminRoute("broadcast", "Broadcast", Icons.Default.Campaign)
    data object Settings : AdminRoute("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApp(viewModel: AdminViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AdminRoute.Dashboard.route

    val bottomNavItems = listOf(
        AdminRoute.Dashboard,
        AdminRoute.Products,
        AdminRoute.Orders,
        AdminRoute.Customers,
        AdminRoute.Settings
    )

    val allRoutes = listOf(
        AdminRoute.Dashboard,
        AdminRoute.Products,
        AdminRoute.Categories,
        AdminRoute.Orders,
        AdminRoute.Customers,
        AdminRoute.Broadcast,
        AdminRoute.Settings
    )

    val currentTitle = allRoutes.find { it.route == currentRoute }?.title ?: "Teleshop Admin"

    fun navigateSingleTop(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Root container paints the status-bar / notch strip as a solid BLACK safe zone.
    // The Scaffold is inset by the system bars so no content ever conflicts with the notch.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    title = {
                        Text(
                            text = currentTitle,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = { navigateSingleTop(AdminRoute.Categories.route) }) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = "Categories"
                            )
                        }
                        IconButton(onClick = { navigateSingleTop(AdminRoute.Broadcast.route) }) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Broadcast"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(windowInsets = WindowInsets(0, 0, 0, 0)) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigateSingleTop(item.route) },
                            icon = {
                                if (item == AdminRoute.Orders && viewModel.pendingOrdersCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge { Text("${viewModel.pendingOrdersCount}") }
                                        }
                                    ) {
                                        Icon(item.icon, contentDescription = item.title)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(
                    navController = navController,
                    startDestination = AdminRoute.Dashboard.route
                ) {
                    composable(AdminRoute.Dashboard.route) {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigate = { route -> navigateSingleTop(route) }
                        )
                    }
                    composable(AdminRoute.Products.route) {
                        ProductsScreen(viewModel = viewModel)
                    }
                    composable(AdminRoute.Categories.route) {
                        CategoriesScreen(viewModel = viewModel)
                    }
                    composable(AdminRoute.Orders.route) {
                        OrdersScreen(viewModel = viewModel)
                    }
                    composable(AdminRoute.Customers.route) {
                        CustomersScreen(viewModel = viewModel)
                    }
                    composable(AdminRoute.Broadcast.route) {
                        BroadcastScreen(viewModel = viewModel)
                    }
                    composable(AdminRoute.Settings.route) {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
