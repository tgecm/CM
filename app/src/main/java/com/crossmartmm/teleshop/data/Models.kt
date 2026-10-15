package com.crossmartmm.teleshop.data

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val discountPrice: Double? = null,
    val stock: Int,
    val category: String,
    val imageUrl: String = "",
    val isActive: Boolean = true
)

enum class OrderStatus(val label: String) {
    PENDING("Pending"),
    VERIFYING_PAYMENT("Payment Check"),
    CONFIRMED("Confirmed"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
)

data class Order(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val paymentMethod: String,
    val paymentSlipUrl: String? = null,
    val status: OrderStatus,
    val createdAt: String
)

data class Customer(
    val id: String,
    val telegramId: String,
    val name: String,
    val username: String,
    val phone: String,
    val totalOrders: Int,
    val totalSpent: Double,
    val isBlocked: Boolean = false
)

data class PaymentMethodConfig(
    val id: String,
    val providerName: String,
    val accountName: String,
    val accountNumber: String,
    val isEnabled: Boolean
)

data class ShopSettings(
    val shopName: String = "CrossMart MM",
    val botToken: String = "",
    val adminChatId: String = "",
    val backendBaseUrl: String = "https://www.telegramecommerce.shop/api/",
    val currency: String = "MMK",
    val deliveryFee: Double = 2500.0,
    val maintenanceMode: Boolean = false
)
