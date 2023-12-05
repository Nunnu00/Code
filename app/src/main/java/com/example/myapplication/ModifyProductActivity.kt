package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.FirebaseDatabase

class ModifyProductActivity: AppCompatActivity() {
    //판매글 수정 액티비티
    private val articleList = mutableListOf<ArticleModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_product)

        val articleModel: ArticleModel = intent.getSerializableExtra("articleModel") as ArticleModel
        val articleKey: String? = intent.getStringExtra("articleKey")

        //Toast.makeText(this, articleModel.itemId, Toast.LENGTH_SHORT).show()
        //로그 확인용
        Log.d("ModifyProductActivity", "articleModel: $articleModel")
        Log.d("ModifyProductActivity", "Received articleModel: $articleModel, articleKey: $articleKey")

        bind(articleModel)

        //사용자 email 가져오기
        val currentUser = Firebase.auth.currentUser
        val userEmail = currentUser?.email
        val email = userEmail.toString()

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            // 수정된 데이터를 Firebase Realtime Database에 업데이트
            modifyArticle(
                articleModel.sellerId,
                findViewById<EditText>(R.id.modifytitle).text.toString(),
                findViewById<EditText>(R.id.modifyprice).text.toString(),
                articleModel.itemId,
                findViewById<EditText>(R.id.informationTextView).text.toString(),
                findViewById<EditText>(R.id.modifyfilter).text.toString(),
                email
            )
            articleList.clear()
            finish()
        }

    }
    private fun bind(articleModel: ArticleModel) {
        val titleEditText = findViewById<EditText>(R.id.modifytitle)
        val priceEditText = findViewById<EditText>(R.id.modifyprice)
        val informationText = findViewById<EditText>(R.id.informationTextView)
        val filterText = findViewById<EditText>(R.id.modifyfilter)

        titleEditText.setText(articleModel.title)
        priceEditText.setText(articleModel.price)
        informationText.setText(articleModel.information)
        filterText.setText(articleModel.filter)
    }

    private fun modifyArticle(
        //제품 정보 수정
        sellerId: String,
        title: String,
        price: String,
        itemId: String,
        information: String,
        filter: String,
        email: String
    ) {
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), price, itemId, information, filter, email)

        val articleModel: ArticleModel = intent.getSerializableExtra("articleModel") as ArticleModel

        val articleRef = FirebaseDatabase.getInstance().getReference("Articles").child(articleModel.itemId)

        articleRef.setValue(model)
            .addOnSuccessListener {
                Toast.makeText(this, "판매글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "수정에 실패했습니다. 오류: $e", Toast.LENGTH_SHORT).show()
            }
    }

}