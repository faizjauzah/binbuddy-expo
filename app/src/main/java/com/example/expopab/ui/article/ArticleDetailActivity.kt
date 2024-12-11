package com.example.expopab.ui.article

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.expopab.R
import com.example.expopab.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make sure ActionBar is shown
        if (supportActionBar == null) {
            setSupportActionBar(binding.toolbar) // If you're using a Toolbar
        }

        // Now set up the back button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = intent.getStringExtra("title") ?: ""
        }

        // Get data from intent
        val titleText = intent.getStringExtra("title") ?: ""
        val contentText = intent.getStringExtra("articleContent") ?: ""  // Get articleContent
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        // Debug logs
        Log.d("ArticleDetailActivity", "Received title: $titleText")
        Log.d("ArticleDetailActivity", "Received content: $contentText")

        // Set up views
        with(binding) {
            articleTitle.text = titleText
            articleContent.text = contentText  // Make sure this TextView exists in your layout

            // Load image using Glide
            if (imageUrl.isNotEmpty()) {
                Glide.with(this@ArticleDetailActivity)
                    .load(imageUrl)
                    .placeholder(R.drawable.bg_educontent)
                    .error(R.drawable.bg_educontent)
                    .centerCrop()
                    .into(articleImage)
            }
        }

        // Set up action bar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(titleText)
        }
    }

    // Handle back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Use the new approach instead of onBackPressed()
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}