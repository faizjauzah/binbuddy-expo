package com.example.expopab.repository

import android.util.Log
import com.example.expopab.model.EducationalContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EducationalContentRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val contentCollection = firestore.collection("educational_content")

    fun getEducationalContent(limit: Int? = null): Flow<List<EducationalContent>> = callbackFlow {
        val query = if (limit != null) {
            contentCollection.limit(limit.toLong())
        } else {
            contentCollection
        }

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Repository", "Error fetching content: ${error.message}")
                return@addSnapshotListener
            }

            val contentList = snapshot?.documents?.mapNotNull { document ->
                try {
                    val content = document.toObject(EducationalContent::class.java)
                    content?.copy(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        category = document.getString("category") ?: "",
                        articleContent = document.getString("articleContent") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("Repository", "Error converting document: ${e.message}")
                    null
                }
            } ?: emptyList()

            // Log the content for debugging
            contentList.forEach { content ->
                Log.d("Repository", """
                    Content loaded:
                    ID: ${content.id}
                    Title: ${content.title}
                    Description: ${content.description}
                    Article Content: ${content.articleContent}
                """.trimIndent())
            }

            trySend(contentList)
        }

        awaitClose { subscription.remove() }
    }
}