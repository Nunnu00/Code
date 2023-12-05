package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.DBKey.Companion.CHAT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.myapplication.databinding.ChattingroomFragmentBinding

class ChattingRoomFragment:Fragment(R.layout.chattingroom_fragment) {
    //2번째 채팅방 프래그먼트

    private var binding: ChattingroomFragmentBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chattingList = mutableListOf<ChattingListModel>()
    private lateinit var chatDB: DatabaseReference
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChattinglistBinding = ChattingroomFragmentBinding.bind(view)
        binding = fragmentChattinglistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { ChatRoom ->
            val intent = Intent(requireContext(),ChattingActivity::class.java)
            intent.putExtra("key", ChatRoom.key)
            startActivity(intent)

        })

        chattingList.clear()

        fragmentChattinglistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentChattinglistBinding.chatListRecyclerView.adapter = chatListAdapter

        if (auth.currentUser == null){
            return
        }

        //채팅방 정보 가져와서 보여주기
        chatDB = Firebase.database.reference.child(auth.currentUser!!.uid).child(CHAT)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val model = it.getValue(ChattingListModel::class.java)
                    model ?: return
                    chattingList.add(model)
                }

                chatListAdapter.submitList(chattingList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}