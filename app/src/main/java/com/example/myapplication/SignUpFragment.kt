package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.MypageFragmentBinding
import com.example.myapplication.databinding.SignupFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment(R.layout.signup_fragment) {
    //3번째 회원가입 프래그먼트

    private lateinit var binding: SignupFragmentBinding
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMyPageBinding = SignupFragmentBinding.bind(view)
        binding = fragmentMyPageBinding

        fragmentMyPageBinding.signUp.setOnClickListener {
            binding?.let { binding ->
                val id = binding.loginId.text.toString()
                val pw = binding.loginPw.text.toString()

                auth.createUserWithEmailAndPassword(id, pw)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            //회원가입 성공일 경우
                            Toast.makeText(context, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                            val nextIntent = Intent(requireContext(), MainActivity::class.java)
                            //성공하면 MainActivity start
                            startActivity(nextIntent)
                            requireActivity().finish()
                        } else {
                            //회원가입 실패일 경우
                            Toast.makeText(
                                context,
                                "회원가입에 실패했습니다. 비밀번호는 6자리 이상으로 해주세요. 이미 가입된 이메일일 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }



}