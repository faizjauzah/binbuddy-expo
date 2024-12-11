package com.example.expopab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expopab.R
import com.example.expopab.databinding.FragmentHomeBinding
import com.example.expopab.ui.home.EducationUIState
import com.example.expopab.model.EducationalContent
import com.example.expopab.ui.home.adapter.EducationalContentAdapter
import com.example.expopab.ui.home.adapter.ReminderAdapter
import com.example.expopab.ui.home.data.ReminderTime
import com.example.expopab.viewmodel.EducationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val eduAdapter = EducationalContentAdapter()
    private val reminderList = mutableListOf<ReminderTime>()
    private lateinit var reminderAdapter: ReminderAdapter
    private val db = Firebase.firestore
    private val viewModel: EducationViewModel by viewModels()

    // Add this to store the listener registration
    private var reminderListener: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEducationRecyclerView()
        setupReminderRecyclerView()
        viewModel.loadPreviewContent()
        observeEducationalContent()
        setupClickListeners()
        loadUserName() // Add this line to load the user's name
    }

    // Add this function to load the user's name
    private fun loadUserName() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            binding.welcomeText.text = "Loading..." // Show loading state
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fullName = document.getString("fullName") ?: ""
                        binding.welcomeText.text = "Welcome, $fullName"
                    } else {
                        binding.welcomeText.text = "Welcome"
                    }
                }
                .addOnFailureListener {
                    binding.welcomeText.text = "Welcome"
                }
        } else {
            binding.welcomeText.text = "Welcome"
        }
    }

    private fun setupEducationRecyclerView() {
        binding.educationRecyclerView.adapter = eduAdapter
        binding.educationRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupReminderRecyclerView() {
        reminderAdapter = ReminderAdapter(reminderList) { }
        binding.reminderList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
        }
    }

    private fun observeEducationalContent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateEducationUIState(state)
            }
        }
    }

    private fun updateEducationUIState(state: EducationUIState) {
        binding.apply {
            when (state) {
                is EducationUIState.Loading -> {
                    educationLoadingBar.visibility = View.VISIBLE
                    educationRecyclerView.visibility = View.GONE
                    educationErrorText.visibility = View.GONE
                }
                is EducationUIState.Success -> {
                    educationLoadingBar.visibility = View.GONE
                    educationRecyclerView.visibility = View.VISIBLE
                    educationErrorText.visibility = View.GONE
                    eduAdapter.submitList(state.content)
                }
                is EducationUIState.Error -> {
                    educationLoadingBar.visibility = View.GONE
                    educationRecyclerView.visibility = View.GONE
                    educationErrorText.apply {
                        visibility = View.VISIBLE
                        text = state.message
                    }
                }
                is EducationUIState.Empty -> {
                    educationLoadingBar.visibility = View.GONE
                    educationRecyclerView.visibility = View.GONE
                    educationErrorText.apply {
                        visibility = View.VISIBLE
                        text = "No educational content available"
                    }
                }
            }
        }
    }

    private fun loadReminders() {
        // Store the listener registration
        reminderListener = db.collection("reminders")
            .whereEqualTo("userId", Firebase.auth.currentUser?.uid)
            .addSnapshotListener { snapshot, _ ->
                if (_binding == null) return@addSnapshotListener
                reminderList.clear()
                snapshot?.forEach { document ->
                    val reminder = document.toObject(ReminderTime::class.java)
                    reminderList.add(reminder)
                }
                reminderAdapter.notifyDataSetChanged()
                updateEmptyState()
            }
    }

    private fun updateEmptyState() {
        binding.emptyState.visibility = if (reminderList.isEmpty()) View.VISIBLE else View.GONE
        binding.reminderList.visibility = if (reminderList.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupClickListeners() {
        binding.seeAllButton.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav.selectedItemId = R.id.navigation_education
        }
    }

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the listener when the view is destroyed
        reminderListener?.remove()
        reminderListener = null
        _binding = null
    }
}