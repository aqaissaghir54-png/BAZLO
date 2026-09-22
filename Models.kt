package com.bazlo.shared

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Long = 0,
    val category: String = "Other",
    val imageUrl: String = "",
    val stock: Int = 0
)

data class Order(
    val id: String = "",
    val customerName: String = "",
    val customerUid: String = "",
    val total: Long = 0,
    val status: String = "pending",
    val createdAt: Long = 0,
    val items: List<Map<String, Any>> = emptyList()
)
