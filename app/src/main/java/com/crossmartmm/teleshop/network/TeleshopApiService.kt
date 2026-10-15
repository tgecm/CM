package com.crossmartmm.teleshop.network

import com.crossmartmm.teleshop.data.Customer
import com.crossmartmm.teleshop.data.Order
import com.crossmartmm.teleshop.data.Product
import retrofit2.http.*

interface TeleshopApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: Product): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String)

    @GET("orders")
    suspend fun getOrders(): List<Order>

    @PATCH("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body statusPayload: Map<String, String>
    ): Order

    @GET("customers")
    suspend fun getCustomers(): List<Customer>

    @POST("broadcast")
    suspend fun sendBroadcast(@Body payload: Map<String, String>)
}
