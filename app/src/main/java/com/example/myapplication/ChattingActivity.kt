package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DBKey.Companion.CHATTING
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ChattingActivity : AppCompatActivity() {
    //채팅방 액티비티

    private val chattingList = mutableListOf<ChatMessage>()
    private val adapter = ChattingAdapter()
    private var chatDB: DatabaseReference? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        val chatting = intent.getLongExtra("key", -1)

        chatDB = Firebase.database.reference.child(CHATTING).child(chatting.toString())

        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage ?: return

                chattingList.add(chatMessage)
                adapter.submitList(chattingList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        findViewById<RecyclerView>(R.id.chattingRecyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.chattingRecyclerView).layoutManager = LinearLayoutManager(this)

        //채팅 정보
        val date = Date(System.currentTimeMillis())
        val time = SimpleDateFormat("yyyy-MM-dd kk:mm", Locale("ko", "KR"))
            .format(date)
        val currentUser = Firebase.auth.currentUser
        val userEmail = currentUser?.email
        val email = userEmail.toString()

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            //보내기 버튼을 누르면 message와 email, time이 저장
            val messagetext = findViewById<EditText>(R.id.messageText)

            val chatMessage = ChatMessage(
                senderId = email,
                message = findViewById<EditText>(R.id.messageText).text.toString(),
                time = time
            )
            chatDB?.push()?.setValue(chatMessage)

            //채팅을 보내면 메시지 창이 비워짐
            messagetext.setText("")
        }
    }
}