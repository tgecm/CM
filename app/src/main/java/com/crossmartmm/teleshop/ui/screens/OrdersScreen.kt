package com.crossmartmm.teleshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossmartmm.teleshop.data.Order
import com.crossmartmm.teleshop.data.OrderStatus
import com.crossmartmm.teleshop.ui.components.*
import com.crossmartmm.teleshop.ui.theme.*
import com.crossmartmm.teleshop.viewmodel.AdminViewModel

@Composable
fun OrdersScreen(viewModel: AdminViewModel) {
    var selectedStatus by remember { mutableStateOf<OrderStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedOrderForDetail by remember { mutableStateOf<Order?>(null) }
    var selectedOrderForSlip by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = viewModel.orders.filter { order ->
        val matchesStatus = selectedStatus == null || order.status == selectedStatus
        val matchesQuery = order.id.contains(searchQuery, ignoreCase = true) ||
                order.customerName.contains(searchQuery, ignoreCase = true) ||
                order.customerPhone.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesQuery
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
            placeholder = "Search order ID, customer or phone..."
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null },
                    label = { Text("All (${viewModel.orders.size})") }
                )
            }
            items(OrderStatus.entries) { status ->
                val count = viewModel.orders.count { it.status == status }
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = status },
                    label = { Text("${status.label} ($count)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredOrders.isEmpty()) {
            EmptyState(
                icon = Icons.Default.ReceiptLong,
                title = "No Orders Found",
                subtitle = "No orders match the current filter criteria."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    val nextStatus = when (order.status) {
                        OrderStatus.PENDING, OrderStatus.VERIFYING_PAYMENT -> OrderStatus.CONFIRMED
                        OrderStatus.CONFIRMED -> OrderStatus.SHIPPED
                        OrderStatus.SHIPPED -> OrderStatus.DELIVERED
                        else -> null
                    }
                    OrderCard(
                        order = order,
                        onViewDetails = { selectedOrderForDetail = order },
                        onVerifySlip = if (order.paymentSlipUrl != null) {
                            { selectedOrderForSlip = order }
                        } else null,
                        onAdvanceStatus = if (nextStatus != null) {
                            { viewModel.updateOrderStatus(order.id, nextStatus) }
                        } else null,
                        nextStatusLabel = nextStatus?.let { "Mark ${it.label}" }
                    )
                }
            }
        }
    }

    if (selectedOrderForDetail != null) {
        val order = selectedOrderForDetail!!
        AlertDialog(
            onDismissRequest = { selectedOrderForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Order ${order.id}", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Customer: ${order.customerName}", fontWeight = FontWeight.SemiBold)
                    Text("Phone: ${order.customerPhone}", style = MaterialTheme.typography.bodySmall)
                    Text("Address: ${order.deliveryAddress}", style = MaterialTheme.typography.bodySmall)
                    Text("Created: ${order.createdAt}", style = MaterialTheme.typography.bodySmall)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Ordered Items:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}x ${item.productName}", style = MaterialTheme.typography.bodySmall)
                            Text(formatCurrency(item.quantity * item.unitPrice), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total (${order.paymentMethod})", fontWeight = FontWeight.Bold)
                        Text(formatCurrency(order.totalAmount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Change Status:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(OrderStatus.entries) { status ->
                            FilterChip(
                                selected = order.status == status,
                                onClick = {
                                    viewModel.updateOrderStatus(order.id, status)
                                    selectedOrderForDetail = order.copy(status = status)
                                },
                                label = { Text(status.label) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrderForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }

    if (selectedOrderForSlip != null) {
        val order = selectedOrderForSlip!!
        AlertDialog(
            onDismissRequest = { selectedOrderForSlip = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FactCheck, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment Slip Verification", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Order: ${order.id}", fontWeight = FontWeight.SemiBold)
                    Text("Payer: ${order.customerName} (${order.customerPhone})")
                    Text("Gateway: ${order.paymentMethod}")
                    Text(
                        text = "Amount: ${formatCurrency(order.totalAmount)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Slip Reference: ${order.paymentSlipUrl}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateOrderStatus(order.id, OrderStatus.CONFIRMED)
                        selectedOrderForSlip = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Approve")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.updateOrderStatus(order.id, OrderStatus.CANCELLED)
                        selectedOrderForSlip = null
                    }
                ) {
                    Text("Reject", color = DangerRed)
                }
            }
        )
    }
}
