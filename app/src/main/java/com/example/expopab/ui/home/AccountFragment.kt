package com.example.expopab.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.expopab.databinding.FragmentAccountBinding
import com.example.expopab.ui.auth.SignInActivity
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
            // Disable the button to prevent multiple clicks
            binding.signOutButton.isEnabled = false

            try {
                // Sign out from Firebase
                Firebase.auth.signOut()

                // Navigate to SignIn activity
                val intent = Intent(requireContext(), SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } catch (e: Exception) {
                // Re-enable the button if there's an error
                binding.signOutButton.isEnabled = true
                // Show error message
                Toast.makeText(context, "Sign out failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}