package com.example.expopab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expopab.R
import com.example.expopab.databinding.FragmentHomeBinding
import com.example.expopab.model.EducationalContent
import com.example.expopab.ui.home.adapter.EducationalContentAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val eduAdapter = EducationalContentAdapter()
    private val reminderList = mutableListOf<ReminderTime>()
    private lateinit var reminderAdapter: ReminderAdapter
    private val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEducationRecyclerView()
        setupReminderRecyclerView()
        loadDummyData()
        setupClickListeners()
    }

    private fun setupEducationRecyclerView() {
        binding.educationRecyclerView.adapter = eduAdapter
        binding.educationRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupReminderRecyclerView() {
        reminderAdapter = ReminderAdapter(reminderList) { }
        binding.reminderList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
        }
    }

    private fun loadReminders() {
        db.collection("reminders")
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

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    private fun setupClickListeners() {
        binding.seeAllButton.setOnClickListener {
            // Select Education tab in bottom navigation
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav.selectedItemId = R.id.navigation_education
        }
    }

    private fun loadDummyData() {
        val dummyContent = listOf(
            EducationalContent(
                "1",
                "Introduction to Android",
                "Learn the basics of Android development",
                "",
                "Android"
            ),
            EducationalContent(
                "2",
                "Kotlin Fundamentals",
                "Master Kotlin programming language",
                "",
                "Programming"
            ),
            EducationalContent(
                "3",
                "UI Design Patterns",
                "Explore modern Android UI patterns",
                "",
                "Design"
            )
        )
        eduAdapter.submitList(dummyContent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}