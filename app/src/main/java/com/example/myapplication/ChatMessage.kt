package com.example.myapplication

data class ChatMessage (
    //채팅 메시지 정보
    val time: String,
    val senderId: String,
    val message: String
){
    constructor():this("","","")
}