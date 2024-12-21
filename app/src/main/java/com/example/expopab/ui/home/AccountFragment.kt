package com.example.expopab.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.expopab.databinding.FragmentAccountBinding
import com.example.expopab.ui.auth.SettingsActivity
import com.example.expopab.ui.auth.SignInActivity
import com.example.expopab.ui.auth.SignOutActivity
import com.example.expopab.ui.auth.WelcomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.auth.currentUser?.let { user ->
            // Load basic user info
            binding.emailText.text = user.email

            // Format and set creation date
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            val creationDate = dateFormat.format(user.metadata?.creationTimestamp?.let {
                java.util.Date(it)
            })
            binding.creationDateText.text = "Account created on: $creationDate"

            // Load full name from Firestore
            loadUserName(user.uid)
        }

        binding.signOutButton.setOnClickListener {
            // Navigate to SignOut activity for confirmation
            val intent = Intent(requireContext(), SignOutActivity::class.java)
            startActivity(intent)
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserName(userId: String) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fullName = document.getString("fullName") ?: ""
                    binding.fullNameText.text = fullName  // Make sure you have this TextView in your layout
                }
            }
            .addOnFailureListener {
                // Handle error if needed
            }
    }

    override fun onResume() {
        super.onResume()
        Firebase.auth.currentUser?.let { user ->
            loadUserName(user.uid)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}