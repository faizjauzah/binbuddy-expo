package com.example.expopab.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.expopab.R
import com.example.expopab.databinding.ItemEducationalContentBinding
import com.example.expopab.model.EducationalContent

class EducationalContentAdapter(
    private val onItemClick: (EducationalContent) -> Unit = {}  // Add click handler
) : RecyclerView.Adapter<EducationalContentAdapter.ContentViewHolder>() {
    private var contents = listOf<EducationalContent>()

    class ContentViewHolder(private val binding: ItemEducationalContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(content: EducationalContent, onItemClick: (EducationalContent) -> Unit) {
            binding.apply {
                contentTitle.text = content.title
                contentDescription.text = content.description
                contentCategory.text = content.category

                // Add click listener to the whole card
                root.setOnClickListener {
                    onItemClick(content)
                }

                // Load image using Glide
                if (content.imageUrl.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(content.imageUrl)
                        .placeholder(R.drawable.bg_educontent)
                        .error(R.drawable.bg_educontent)
                        .centerCrop()
                        .into(contentImage)
                } else {
                    contentImage.setImageResource(R.drawable.bg_educontent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemEducationalContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(contents[position], onItemClick)
    }

    override fun getItemCount() = contents.size

    fun submitList(newContents: List<EducationalContent>) {
        contents = newContents
        notifyDataSetChanged()
    }
}