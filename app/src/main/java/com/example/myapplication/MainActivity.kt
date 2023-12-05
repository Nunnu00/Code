package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val activityMainBinding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val homeFragment = HomeFragment()
        val myPageFragment = MyPageFragment()
        val chattingRoomFragment = ChattingRoomFragment()
        val signupFragment = SignUpFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        selectFragment(homeFragment)

        bottomNavigationView.setOnItemSelectedListener { MenuItem ->
            when (MenuItem.itemId) {
                //각각의 프래그먼트 메뉴
                R.id.home -> selectFragment(homeFragment)
                R.id.chattingList -> selectFragment(chattingRoomFragment)
                R.id.myPage -> selectFragment(myPageFragment)
                R.id.signUpPage -> selectFragment(signupFragment)

            }
            true
        }
    }

    private fun selectFragment(fragment : Fragment) {
        //프래그먼트 선택 시 변경
        Log.d("MainActivity","${fragment}")
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.selectFragment,fragment)
                commit()
            }
    }
}