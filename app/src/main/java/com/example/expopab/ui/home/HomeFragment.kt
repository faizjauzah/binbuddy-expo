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

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = EducationalContentAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDummyData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        binding.educationRecyclerView.adapter = adapter
        binding.educationRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
        adapter.submitList(dummyContent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}