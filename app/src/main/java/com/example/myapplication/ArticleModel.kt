package com.example.myapplication

import java.io.Serializable

data class ArticleModel(
    //제품 정보
    val sellerId: String,
    val title: String,
    //val product : String,
    val createdAt: Long,
    val price: String,
    var itemId: String,
    val information: String,
    val filter: String,
    val email: String
): Serializable
{
    constructor(): this("", "", 0, "", "", "", "", "")
}