package com.ramith.foodrunner.model

data class User(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userMobileNumber: String,
    val userDeliveryAddress: String,
    val userPassword: String
)