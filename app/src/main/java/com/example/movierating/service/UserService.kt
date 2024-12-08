package com.example.movierating.service

import android.util.Log
import com.example.movierating.data.MovieRated
import com.example.movierating.data.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserService {
    private val firestore = FirebaseFirestore.getInstance()

    // MovieRated 저장
    suspend fun saveMovieRated(movieRated: MovieRated): String? {
        val movieRatedCollection = firestore.collection("movieRated")
        val docRef = movieRatedCollection.add(movieRated.toMap()).await()
        return docRef.id // 저장된 문서의 ID 반환
    }

    // User의 movieRatedList 업데이트
    suspend fun addMovieRatedToUser(userId: String, movieId: String, rating: Float) {
        // 'users/{userId}'와 같은 경로를 사용해야 합니다.
        val userDocRef = firestore.collection("user").document(userId) // 문서 참조를 정확하게 설정

        // 여기에 실제로 데이터를 저장하거나 업데이트하는 로직을 추가합니다
        val movieRatingData = hashMapOf(
            "movieId" to movieId,
            "rating" to rating
        )

        // 영화 평점을 유저 문서에 추가
        userDocRef.update("ratedMovies", FieldValue.arrayUnion(movieRatingData))
            .addOnSuccessListener {
                Log.d("UserService", "Movie rating added successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("UserService", "Error adding movie rating: $e")
            }
    }

    // 유저 정보 가져오기
    suspend fun fetchUser(userId: String): User {
        val userSnapshot = firestore.collection("users").document(userId).get().await()
        return userSnapshot.toObject(User::class.java) ?: User()
    }
}
