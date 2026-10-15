package com.crossmartmm.teleshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossmartmm.teleshop.ui.AdminRoute
import com.crossmartmm.teleshop.ui.components.*
import com.crossmartmm.teleshop.ui.theme.*
import com.crossmartmm.teleshop.viewmodel.AdminViewModel

@Composable
fun DashboardScreen(
    viewModel: AdminViewModel,
    onNavigate: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.shopSettings.shopName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Telegram E-Commerce Admin Panel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    FilledIconButton(
                        onClick = { onNavigate(AdminRoute.Broadcast.route) }
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = "Broadcast")
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Revenue",
                        value = formatCurrency(viewModel.totalRevenue, viewModel.shopSettings.currency),
                        subtitle = "${viewModel.orders.size} Total Orders",
                        icon = Icons.Default.AccountBalanceWallet,
                        accentColor = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pending Orders",
                        value = "${viewModel.pendingOrdersCount}",
                        subtitle = "Action Required",
                        icon = Icons.Default.PendingActions,
                        accentColor = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Products",
                        value = "${viewModel.products.size}",
                        subtitle = "${viewModel.lowStockProductsCount} Low Stock",
                        icon = Icons.Default.Inventory2,
                        accentColor = AccentTeal,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Customers",
                        value = "${viewModel.customers.size}",
                        subtitle = "Active Subscribers",
                        icon = Icons.Default.PeopleAlt,
                        accentColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            SectionHeader(title = "Quick Actions")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(AdminRoute.Products.route) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddBox, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Products")
                }
                OutlinedButton(
                    onClick = { onNavigate(AdminRoute.Categories.route) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Categories")
                }
                OutlinedButton(
                    onClick = { onNavigate(AdminRoute.Broadcast.route) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast")
                }
            }
        }

        item {
            SectionHeader(
                title = "Recent Orders",
                actionLabel = "View All",
                onActionClick = { onNavigate(AdminRoute.Orders.route) }
            )
        }

        items(viewModel.orders.take(3), key = { it.id }) { order ->
            OrderCard(
                order = order,
                onViewDetails = { onNavigate(AdminRoute.Orders.route) }
            )
        }
    }
}
