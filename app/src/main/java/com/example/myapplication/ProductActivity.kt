package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.DBKey.Companion.CHAT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProductActivity : AppCompatActivity() {
    //제품 판매글을 클릭 시 확인하는 액티비티

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    val userDB = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val articleModel: ArticleModel = intent.getSerializableExtra("articleModel") as ArticleModel

        bind(articleModel)
        val button = findViewById<Button>(R.id.chatButton).setOnClickListener {
            //채팅하기 버튼을 누를 경우
            val chatRoom = ChattingListModel(
                buyerId = auth.currentUser!!.uid,
                sellerId = articleModel.sellerId,
                itemTitle = articleModel.title,
                key = System.currentTimeMillis()
            )
            //파이어베이스 데이터베이스에 저장
            userDB.child(auth.currentUser!!.uid)
                .child(CHAT)
                .push()
                .setValue(chatRoom)

            userDB.child(articleModel.sellerId)
                .child(CHAT)
                .push()
                .setValue(chatRoom)

            AlertDialog.Builder(this)
                .setTitle("상상부기")
                .setMessage("채팅방이 생성되었어요.\n채팅방 목록에서 확인해주세요!")
                .setPositiveButton("확인") { _, _ ->
                    finish()
                }
                .create()
                .show()
        }

    }
    private fun bind(articleModel: ArticleModel) {
        //제품 판매글 확인
        val titleEditText = findViewById<TextView>(R.id.modifytitle)
        val priceEditText = findViewById<TextView>(R.id.modifyprice)
        val informationText = findViewById<TextView>(R.id.informationTextView)
        val filterText = findViewById<TextView>(R.id.filterEditText)
        val emailText = findViewById<TextView>(R.id.emailText)

        titleEditText.text = "글 제목: " + articleModel.title
        priceEditText.text = "가격: " + articleModel.price
        informationText.text = "제품 정보: " + articleModel.information
        filterText.text = "판매 여부: " + articleModel.filter
        emailText.text = "판매자: " + articleModel.email
    }
}