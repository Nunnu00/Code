package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat.*
import com.example.myapplication.DBKey.Companion.ARTICLES
import com.example.myapplication.DBKey.Companion.USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddProductActivity: AppCompatActivity() {
    //제품 판매글 추가 액티비티

    //private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth //모름
    }

    //private val storage: FirebaseStorage by lazy {
    //    Firebase.storage
    //}

    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(ARTICLES)
    }

    //private val userDB: DatabaseReference by lazy {
    //    Firebase.database.reference.child(USER)
    //}

    //private val db: FirebaseFirestore by lazy {
    //    Firebase.firestore
    //}

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.modifytitle).text.toString()
            val price = findViewById<EditText>(R.id.modifyprice).text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()
            val itemId = sellerId.toString()
            val information = findViewById<EditText>(R.id.informationTextView).text.toString()
            val currentUser = Firebase.auth.currentUser
            val userEmail = currentUser?.email
            val email = userEmail.toString()
            val filter = "o"

            if (title.isEmpty()) {
                //제목 란이 비어 있을 시
                Toast.makeText(this, "제목 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (price.isEmpty()) {
                //가격 란이 비어 있을 시
                Toast.makeText(this, "가격 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //제품 판매글 등록
            addProduct(sellerId, title, price, itemId, information, filter, email)

        }
    }


    private fun addProduct(
        sellerId: String,
        title: String,
        price: String,
        itemId: String,
        information: String,
        filter: String,
        email: String
    ) {

        //제품 정보들을 저장
        val model = ArticleModel(
            sellerId,
            title,
            System.currentTimeMillis(),
            price,
            itemId,
            information,
            filter,
            email
        )
        val newItemRef = articleDB.push()

        newItemRef.setValue(model).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // newItemRef.setValue(model)를 통해 생성된 키값을 가져옴
                val newItemKey = newItemRef.key

                newItemKey?.let {
                    articleDB.child(newItemKey).child("itemId").setValue(newItemKey)

                    val intent = Intent(this@AddProductActivity, ModifyProductActivity::class.java)
                    intent.putExtra("articleId", newItemKey)

                    //Toast.makeText(this@AddProductActivity, newItemKey, Toast.LENGTH_LONG).show()
                }
            } else {
                //판매글 추가 오류 메시지
                Log.e("YourActivity", "데이터 추가 오류: ${task.exception}")
            }
        }

        Toast.makeText(this, "아이템이 등록되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }
}

    /*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }
}
*/