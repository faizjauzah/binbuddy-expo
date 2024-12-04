package com.example.expopab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expopab.databinding.FragmentHomeBinding
import com.example.expopab.model.EducationalContent
import com.example.expopab.ui.home.adapter.EducationalContentAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = EducationalContentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        binding.contentRecyclerView.adapter = adapter
        binding.contentRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun loadDummyData() {
        val dummyContent = listOf(
            EducationalContent(
                "1",
                "Introduction to Android",
                "Learn the basics of Android development including activities, fragments, and layouts",
                "",
                "Android"
            ),
            EducationalContent(
                "2",
                "Kotlin Fundamentals",
                "Master Kotlin programming language with hands-on examples and best practices",
                "",
                "Programming"
            ),
            EducationalContent(
                "3",
                "UI Design Patterns",
                "Explore modern Android UI design patterns and Material Design guidelines",
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