package com.crossmartmm.teleshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossmartmm.teleshop.data.Customer
import com.crossmartmm.teleshop.ui.components.*
import com.crossmartmm.teleshop.ui.theme.DangerRed
import com.crossmartmm.teleshop.ui.theme.SuccessGreen
import com.crossmartmm.teleshop.viewmodel.AdminViewModel

@Composable
fun CustomersScreen(viewModel: AdminViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = viewModel.customers.filter { customer ->
        customer.name.contains(searchQuery, ignoreCase = true) ||
                customer.username.contains(searchQuery, ignoreCase = true) ||
                customer.phone.contains(searchQuery, ignoreCase = true) ||
                customer.telegramId.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search customer name, @username or phone..."
        )
        Spacer(modifier = Modifier.height(12.dp))

        SectionHeader(title = "Telegram Subscribers (${filteredCustomers.size})")
        Spacer(modifier = Modifier.height(8.dp))

        if (filteredCustomers.isEmpty()) {
            EmptyState(
                icon = Icons.Default.PeopleOutline,
                title = "No Customers Found",
                subtitle = "No matching subscriber records."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredCustomers, key = { it.id }) { customer ->
                    CustomerCard(
                        customer = customer,
                        onClick = { selectedCustomer = customer },
                        onToggleBlock = { viewModel.toggleBlockCustomer(customer.id) }
                    )
                }
            }
        }
    }

    if (selectedCustomer != null) {
        val current = viewModel.customers.find { it.id == selectedCustomer!!.id } ?: selectedCustomer!!
        AlertDialog(
            onDismissRequest = { selectedCustomer = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(current.name, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Customer ID: ${current.id}", style = MaterialTheme.typography.bodySmall)
                    Text("Telegram Username: ${current.username}")
                    Text("Telegram UID: ${current.telegramId}")
                    Text("Phone: ${current.phone}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Total Orders: ${current.totalOrders}", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Lifetime Spent: ${formatCurrency(current.totalSpent)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    StatusChip(
                        label = if (current.isBlocked) "Blocked Account" else "Active Account",
                        color = if (current.isBlocked) DangerRed else SuccessGreen
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleBlockCustomer(current.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (current.isBlocked) SuccessGreen else DangerRed
                    )
                ) {
                    Icon(
                        imageVector = if (current.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (current.isBlocked) "Unblock User" else "Block User")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCustomer = null }) {
                    Text("Close")
                }
            }
        )
    }
}
