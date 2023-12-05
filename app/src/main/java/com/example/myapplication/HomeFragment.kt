package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.DBKey.Companion.ARTICLES
import com.example.myapplication.DBKey.Companion.USER
import com.example.myapplication.databinding.HomeFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment(R.layout.home_fragment) {

    //1번째 홈 프래그먼트, 판매글 확인

    private var binding: HomeFragmentBinding?= null
    private lateinit var articleAdapter: ProductAdapter

    private val auth: FirebaseAuth by lazy {
        Firebase.auth //모름
    }

    private val articleList = mutableListOf<ArticleModel>()
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference

    //val db: FirebaseFirestore = Firebase.firestore

    private fun updateAdapterData() {
        articleList.clear()
        articleDB.addChildEventListener(listener)
    }

    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            val key = snapshot.key

            System.out.println(key)

            if (!articleList.contains(articleModel)) {
                //중복 데이터가 없으면 추가
                articleList.add(articleModel)
                articleAdapter.submitList(articleList)
                articleAdapter.updateData(articleList)
            } else {
                //중복 데이터가 있으면 해당 데이터 수정
                val index = articleList.indexOf(articleModel)
                articleList[index] = articleModel
                articleAdapter.updateData(articleList)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            updateAdapterData()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentHomeBinding = HomeFragmentBinding.bind(view)
        binding = fragmentHomeBinding

        articleDB = Firebase.database.reference.child(ARTICLES)
        userDB = Firebase.database.reference.child(USER)

        articleAdapter = ProductAdapter(onItemClicked = { articleModel: ArticleModel ->
            context?.let {
                if (auth.currentUser != null) {
                    if (auth.currentUser?.uid != articleModel.sellerId) {
                        //다른 사람이 올린 글을 누르면 판매 정보 보기 가능+채팅 기능
                        val intent = Intent(it, ProductActivity::class.java)
                        intent.putExtra("articleModel", articleModel)
                        startActivity(intent)

                    } else {
                        //내가 올린 아이템이면 판매 글 수정 가능
                        val intent = Intent(it, ModifyProductActivity::class.java)
                        intent.putExtra("articleModel", articleModel) // ArticleModel을 전달

                        startActivity(intent)
                        //Snackbar.make(view, "내가 올린 아이템 입니다.", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
                }
            }

        })

        fragmentHomeBinding.itemRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.itemRecyclerView.adapter = articleAdapter

        fragmentHomeBinding.floatingActionButton.setOnClickListener {
            //판매글 추가 버튼
            context?.let {
                if (auth.currentUser != null) {
                    //로그인이 되어 있다면 AddProduct로 이동해서 판매글 추가 가능
                    val intent = Intent(it, AddProductActivity::class.java)
                    startActivity(intent)
                } else {
                    //로그인이 되어 있지 않을 시
                    Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
                }

            }
        }

        view.findViewById<Button>(R.id.filterButton).setOnClickListener {
            showFilterDialog()
        }

        articleList.clear()
        articleDB.addChildEventListener(listener)
        articleAdapter.notifyDataSetChanged()

    }

    private fun showFilterDialog() {
        //다이얼로그로 필터 기능
        val saleStatuses = resources.getStringArray(R.array.sale_statuses)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Sale Status")
            .setItems(saleStatuses) { _, which ->
                handleFilterSelection(which)
            }
        builder.create().show()
    }

    private fun handleFilterSelection(selectedIndex: Int) {
        when (selectedIndex) {
            0 -> {
                // 모든 판매 상태 선택
                fetchAllArticles()
            }
            1 -> {
                // 판매중 선택
                fetchArticlesBySaleStatus(true)
            }
            2 -> {
                // 판매 완료 선택
                fetchArticlesBySaleStatus(false)
            }
        }
    }
    private fun fetchAllArticles() {
        articleList.clear()
        articleDB.addChildEventListener(listener)
    }

    private fun fetchArticlesBySaleStatus(onSale: Boolean) {
        articleDB.removeEventListener(listener)

        val query = if (onSale) {
            //판매중 선택일 시 filter(판매여부)가 o인거 찾기
            Firebase.database.reference.child("Articles")
                .orderByChild("filter")
                .equalTo("o")

        } else {
            //판매 완료 선택일 시 filter(판매여부)가 x인거 찾기
            Firebase.database.reference.child("Articles")
                .orderByChild("filter")// data를 찾기
                .equalTo("x")

        }
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                articleList.clear()
                for (childSnapshot in snapshot.children) {
                    val articleModel = childSnapshot.getValue(ArticleModel::class.java)
                    articleModel?.let {
                        Log.d("HomeFragment", "Fetched article: $articleModel")
                        articleList.add(articleModel)
                    }
                }

                articleAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching articles: ${error.message}")
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        //삭제용, 근데 사용X
        super.onDestroyView()
        articleDB.removeEventListener(listener)
    }

}