package com.crossmartmm.teleshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossmartmm.teleshop.data.Product
import com.crossmartmm.teleshop.ui.components.*
import com.crossmartmm.teleshop.viewmodel.AdminViewModel

@Composable
fun ProductsScreen(viewModel: AdminViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val filteredProducts = viewModel.products.filter { product ->
        val matchesCat = selectedCategory == "All" || product.category == selectedCategory
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                product.id.contains(searchQuery, ignoreCase = true)
        matchesCat && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingProduct = null
                    showEditDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Product") },
                text = { Text("Add Product") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search product name or ID..."
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        leadingIcon = if (selectedCategory == category) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (filteredProducts.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Inventory2,
                    title = "No Products Found",
                    subtitle = "Try adjusting your search or add a new product."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onEdit = {
                                editingProduct = product
                                showEditDialog = true
                            },
                            onDelete = { productToDelete = product },
                            onToggleActive = { viewModel.toggleProductAvailability(product.id) }
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        ProductFormDialog(
            initialProduct = editingProduct,
            categories = viewModel.categories.filter { it != "All" },
            onDismiss = { showEditDialog = false },
            onSave = { product ->
                viewModel.addOrUpdateProduct(product)
                showEditDialog = false
            }
        )
    }

    if (productToDelete != null) {
        ConfirmDialog(
            title = "Delete Product",
            message = "Are you sure you want to delete '${productToDelete?.name}'?",
            confirmText = "Delete",
            isDanger = true,
            onConfirm = {
                productToDelete?.let { viewModel.deleteProduct(it.id) }
                productToDelete = null
            },
            onDismiss = { productToDelete = null }
        )
    }
}

@Composable
private fun ProductFormDialog(
    initialProduct: Product?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var price by remember { mutableStateOf(initialProduct?.price?.toLong()?.toString() ?: "") }
    var discountPrice by remember { mutableStateOf(initialProduct?.discountPrice?.toLong()?.toString() ?: "") }
    var stock by remember { mutableStateOf(initialProduct?.stock?.toString() ?: "10") }
    var category by remember { mutableStateOf(initialProduct?.category ?: categories.firstOrNull() ?: "General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialProduct == null) "Add New Product" else "Edit Product",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LabeledTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Product Name",
                    leadingIcon = Icons.Default.ShoppingBag
                )
                LabeledTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    singleLine = false
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabeledTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = "Price",
                        modifier = Modifier.weight(1f)
                    )
                    LabeledTextField(
                        value = discountPrice,
                        onValueChange = { discountPrice = it },
                        label = "Sale Price",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabeledTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = "Stock Qty",
                        modifier = Modifier.weight(1f)
                    )
                    LabeledTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = "Category",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank()) {
                        onSave(
                            Product(
                                id = initialProduct?.id ?: "PRD-${System.currentTimeMillis().toString().takeLast(4)}",
                                name = name.trim(),
                                description = description.trim(),
                                price = price.toDoubleOrNull() ?: 0.0,
                                discountPrice = discountPrice.toDoubleOrNull(),
                                stock = stock.toIntOrNull() ?: 0,
                                category = category.trim().ifEmpty { "General" },
                                isActive = initialProduct?.isActive ?: true
                            )
                        )
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
