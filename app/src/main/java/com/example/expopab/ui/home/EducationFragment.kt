package com.example.expopab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expopab.databinding.FragmentEducationBinding
import com.example.expopab.model.EducationalContent
import com.example.expopab.ui.home.adapter.EducationalContentAdapter
import com.example.expopab.viewmodel.EducationViewModel
import kotlinx.coroutines.launch

class EducationFragment : Fragment() {
    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!
    private val adapter = EducationalContentAdapter()
    private val viewModel: EducationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.loadAllContent()  // Load all items
        observeEducationalContent()
    }

    private fun setupRecyclerView() {
        binding.contentRecyclerView.adapter = adapter
        binding.contentRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeEducationalContent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EducationUIState.Loading -> {
                        binding.contentRecyclerView.visibility = View.GONE
                        // Show loading if you have a loading view
                    }
                    is EducationUIState.Success -> {
                        binding.contentRecyclerView.visibility = View.VISIBLE
                        adapter.submitList(state.content)
                    }
                    is EducationUIState.Error -> {
                        binding.contentRecyclerView.visibility = View.GONE
                        // Show error if you have an error view
                    }
                    is EducationUIState.Empty -> {
                        binding.contentRecyclerView.visibility = View.GONE
                        // Show empty state if you have an empty state view
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}