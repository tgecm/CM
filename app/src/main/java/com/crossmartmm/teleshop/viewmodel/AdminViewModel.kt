package com.crossmartmm.teleshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.crossmartmm.teleshop.data.*

class AdminViewModel : ViewModel() {

    var categories = mutableStateListOf("All", "Electronics", "Fashion", "Cosmetics", "Home & Living", "Accessories")
        private set

    var products = mutableStateListOf(
        Product("PRD-001", "OMA Liquid Dishwash 1L", "High concentration lemon scent liquid soap", 4500.0, 4000.0, 85, "Home & Living", "", true),
        Product("PRD-002", "Wireless ANC Earbuds Pro", "Active noise cancelling bluetooth 5.3 earbuds", 68000.0, null, 24, "Electronics", "", true),
        Product("PRD-003", "Super Star Cotton Tee", "100% combed cotton breathable unisex shirt", 18500.0, 16000.0, 42, "Fashion", "", true),
        Product("PRD-004", "Smart Fitness Watch Series 8", "AMOLED display heart-rate & sleep monitor", 95000.0, 89000.0, 7, "Electronics", "", true),
        Product("PRD-005", "Noodle Master Spicy Pack (5x)", "Authentic Mandalay style instant noodle pack", 6500.0, null, 120, "Home & Living", "", true)
    )
        private set

    var orders = mutableStateListOf(
        Order(
            id = "ORD-9021",
            customerId = "CUS-01",
            customerName = "Kyaw Swar",
            customerPhone = "09791234567",
            deliveryAddress = "Chanayethazan Tsp, 78th St, Mandalay",
            items = listOf(
                OrderItem("PRD-001", "OMA Liquid Dishwash 1L", 2, 4000.0),
                OrderItem("PRD-002", "Wireless ANC Earbuds Pro", 1, 68000.0)
            ),
            totalAmount = 76000.0,
            paymentMethod = "KBZPay",
            paymentSlipUrl = "SLIP_ATTACHED",
            status = OrderStatus.VERIFYING_PAYMENT,
            createdAt = "2026-10-14 14:20"
        ),
        Order(
            id = "ORD-9020",
            customerId = "CUS-02",
            customerName = "Su Myat Noe",
            customerPhone = "09965432100",
            deliveryAddress = "Mahaaungmyay Tsp, 35th St, Mandalay",
            items = listOf(
                OrderItem("PRD-003", "Super Star Cotton Tee", 3, 16000.0)
            ),
            totalAmount = 48000.0,
            paymentMethod = "AYA Pay",
            paymentSlipUrl = "SLIP_ATTACHED",
            status = OrderStatus.CONFIRMED,
            createdAt = "2026-10-14 11:05"
        ),
        Order(
            id = "ORD-9019",
            customerId = "CUS-03",
            customerName = "Min Khant",
            customerPhone = "09440011223",
            deliveryAddress = "Pyigyidagun Tsp, Mandalay",
            items = listOf(
                OrderItem("PRD-004", "Smart Fitness Watch Series 8", 1, 89000.0)
            ),
            totalAmount = 89000.0,
            paymentMethod = "WavePay",
            paymentSlipUrl = null,
            status = OrderStatus.PENDING,
            createdAt = "2026-10-13 19:45"
        ),
        Order(
            id = "ORD-9018",
            customerId = "CUS-04",
            customerName = "Hnin Wai",
            customerPhone = "09250099887",
            deliveryAddress = "Amarapura Tsp, Mandalay",
            items = listOf(
                OrderItem("PRD-005", "Noodle Master Spicy Pack (5x)", 4, 6500.0)
            ),
            totalAmount = 26000.0,
            paymentMethod = "Cash on Delivery",
            paymentSlipUrl = null,
            status = OrderStatus.DELIVERED,
            createdAt = "2026-10-12 09:30"
        )
    )
        private set

    var customers = mutableStateListOf(
        Customer("CUS-01", "55192019", "Kyaw Swar", "@kyawswar_mdy", "09791234567", 8, 345000.0, false),
        Customer("CUS-02", "66291823", "Su Myat Noe", "@sumyat_99", "09965432100", 5, 192000.0, false),
        Customer("CUS-03", "77819200", "Min Khant", "@minkhant_dev", "09440011223", 2, 115000.0, false),
        Customer("CUS-04", "88912344", "Hnin Wai", "@hninwai_shop", "09250099887", 12, 580000.0, false)
    )
        private set

    var paymentMethods = mutableStateListOf(
        PaymentMethodConfig("PM-1", "KBZPay", "U Sai Maung Lenn", "09790001111", true),
        PaymentMethodConfig("PM-2", "AYA Pay", "U Sai Maung Lenn", "09790001111", true),
        PaymentMethodConfig("PM-3", "WavePay", "U Sai Maung Lenn", "09790001111", true),
        PaymentMethodConfig("PM-4", "MMQR / CB Pay", "CrossMart Official", "2091-8821-9901", true)
    )
        private set

    var shopSettings by mutableStateOf(ShopSettings())
        private set

    var broadcastHistory = mutableStateListOf(
        "10-12: October Flash Sale 20% OFF on all Electronics!",
        "10-05: New arrival OMA Liquid Soap stock updated."
    )
        private set

    // Analytics Computed Properties
    val totalRevenue: Double
        get() = orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalAmount }

    val pendingOrdersCount: Int
        get() = orders.count { it.status == OrderStatus.PENDING || it.status == OrderStatus.VERIFYING_PAYMENT }

    val lowStockProductsCount: Int
        get() = products.count { it.stock <= 10 }

    // Product Actions
    fun addOrUpdateProduct(product: Product) {
        val index = products.indexOfFirst { it.id == product.id }
        if (index >= 0) {
            products[index] = product
        } else {
            products.add(0, product)
        }
    }

    fun deleteProduct(productId: String) {
        products.removeAll { it.id == productId }
    }

    fun toggleProductAvailability(productId: String) {
        val index = products.indexOfFirst { it.id == productId }
        if (index >= 0) {
            val current = products[index]
            products[index] = current.copy(isActive = !current.isActive)
        }
    }

    fun addCategory(category: String) {
        if (category.isNotBlank() && !categories.contains(category)) {
            categories.add(category)
        }
    }

    // Order Actions
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            orders[index] = orders[index].copy(status = newStatus)
        }
    }

    // Customer Actions
    fun toggleBlockCustomer(customerId: String) {
        val index = customers.indexOfFirst { it.id == customerId }
        if (index >= 0) {
            val current = customers[index]
            customers[index] = current.copy(isBlocked = !current.isBlocked)
        }
    }

    // Broadcast & Settings Actions
    fun sendBroadcastMessage(message: String) {
        if (message.isNotBlank()) {
            broadcastHistory.add(0, "Just Now: $message")
        }
    }

    fun updateSettings(newSettings: ShopSettings) {
        shopSettings = newSettings
    }

    fun togglePaymentMethod(id: String) {
        val index = paymentMethods.indexOfFirst { it.id == id }
        if (index >= 0) {
            val current = paymentMethods[index]
            paymentMethods[index] = current.copy(isEnabled = !current.isEnabled)
        }
    }
}
