package com.example.myapplication

data class ChattingListModel(
    //채팅방 정보
    val buyerId: String,
    val sellerId : String,
    val itemTitle: String,
    val key: Long
){
    constructor(): this("","","",0)
}