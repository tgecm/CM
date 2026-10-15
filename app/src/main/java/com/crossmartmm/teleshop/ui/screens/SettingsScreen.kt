package com.crossmartmm.teleshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.crossmartmm.teleshop.ui.components.LabeledTextField
import com.crossmartmm.teleshop.ui.components.SectionHeader
import com.crossmartmm.teleshop.ui.components.SettingsRow
import com.crossmartmm.teleshop.ui.theme.SuccessGreen
import com.crossmartmm.teleshop.viewmodel.AdminViewModel

@Composable
fun SettingsScreen(viewModel: AdminViewModel) {
    var shopName by remember { mutableStateOf(viewModel.shopSettings.shopName) }
    var botToken by remember { mutableStateOf(viewModel.shopSettings.botToken) }
    var adminChatId by remember { mutableStateOf(viewModel.shopSettings.adminChatId) }
    var backendBaseUrl by remember { mutableStateOf(viewModel.shopSettings.backendBaseUrl) }
    var currency by remember { mutableStateOf(viewModel.shopSettings.currency) }
    var deliveryFee by remember { mutableStateOf(viewModel.shopSettings.deliveryFee.toLong().toString()) }
    var maintenanceMode by remember { mutableStateOf(viewModel.shopSettings.maintenanceMode) }
    var savedSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(title = "Shop & Telegram Bot Configuration")

                    LabeledTextField(
                        value = shopName,
                        onValueChange = { shopName = it; savedSuccess = false },
                        label = "Shop Name",
                        leadingIcon = Icons.Default.Storefront
                    )

                    LabeledTextField(
                        value = botToken,
                        onValueChange = { botToken = it; savedSuccess = false },
                        label = "Telegram Bot Token",
                        leadingIcon = Icons.Default.Key
                    )

                    LabeledTextField(
                        value = adminChatId,
                        onValueChange = { adminChatId = it; savedSuccess = false },
                        label = "Admin Telegram Chat ID",
                        leadingIcon = Icons.Default.AdminPanelSettings
                    )

                    LabeledTextField(
                        value = backendBaseUrl,
                        onValueChange = { backendBaseUrl = it; savedSuccess = false },
                        label = "Backend Base API URL",
                        leadingIcon = Icons.Default.CloudSync
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LabeledTextField(
                            value = currency,
                            onValueChange = { currency = it; savedSuccess = false },
                            label = "Currency",
                            leadingIcon = Icons.Default.Payments,
                            modifier = Modifier.weight(1f)
                        )
                        LabeledTextField(
                            value = deliveryFee,
                            onValueChange = { deliveryFee = it; savedSuccess = false },
                            label = "Delivery Fee",
                            leadingIcon = Icons.Default.LocalShipping,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    SettingsRow(
                        title = "Maintenance Mode",
                        subtitle = "Temporarily pause customer ordering via Telegram bot",
                        icon = Icons.Default.BuildCircle,
                        checked = maintenanceMode,
                        onCheckedChange = {
                            maintenanceMode = it
                            savedSuccess = false
                        }
                    )

                    Button(
                        onClick = {
                            viewModel.updateSettings(
                                viewModel.shopSettings.copy(
                                    shopName = shopName.trim(),
                                    botToken = botToken.trim(),
                                    adminChatId = adminChatId.trim(),
                                    backendBaseUrl = backendBaseUrl.trim(),
                                    currency = currency.trim().ifEmpty { "MMK" },
                                    deliveryFee = deliveryFee.toDoubleOrNull() ?: 0.0,
                                    maintenanceMode = maintenanceMode
                                )
                            )
                            savedSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Settings")
                    }

                    if (savedSuccess) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Configuration saved successfully.",
                                color = SuccessGreen,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Payment Methods (${viewModel.paymentMethods.size})")
        }

        items(viewModel.paymentMethods, key = { it.id }) { method ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = method.providerName,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = method.providerName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${method.accountName} •${method.accountNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = method.isEnabled,
                        onCheckedChange = { viewModel.togglePaymentMethod(method.id) }
                    )
                }
            }
        }
    }
}
