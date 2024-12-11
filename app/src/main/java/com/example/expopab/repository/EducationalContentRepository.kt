package com.example.expopab.repository

import com.example.expopab.model.EducationalContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

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
                return@addSnapshotListener
            }

            val contentList = snapshot?.documents?.mapNotNull { document ->
                document.toObject(EducationalContent::class.java)?.copy(id = document.id)
            } ?: emptyList()

            trySend(contentList)
        }

        awaitClose { subscription.remove() }
    }
}