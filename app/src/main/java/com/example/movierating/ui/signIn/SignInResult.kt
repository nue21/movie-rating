package com.example.movierating.ui.signIn

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String = "",
    val username: String? = "",
    val profilePictureUrl: String? = "",
    val collectionList: List<String>? = emptyList(),
    val wishList: List<String>? = emptyList(),
    val movieRatedList: List<String>? = emptyList()
)

fun saveUserData() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
        val userId = currentUser.uid
        val firestore = FirebaseFirestore.getInstance()

        // 사용자 문서가 존재하는지 확인
        firestore.collection("user")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                if (documentSnapshot.exists()) {
                    // 문서가 이미 존재하면 데이터를 저장하지 않음
                    println("User document already exists. No need to create.")
                } else {
                    // 문서가 존재하지 않으면 새로 생성
                    val userData = UserData(
                        userId = userId,
                        username = "영린이",   // 예시로 하드코딩된 값, 실제 앱에서는 입력받은 값
                        profilePictureUrl = currentUser.photoUrl?.toString(),
                        collectionList = emptyList(),  // 예시
                        wishList = emptyList(),       // 예시
                        movieRatedList = emptyList()   // 예시
                    )

                    firestore.collection("user")
                        .document(userId)  // 사용자 UID를 문서 ID로 사용
                        .set(userData)
                        .addOnSuccessListener {
                            // 데이터 저장 성공
                            println("User data saved successfully")
                        }
                        .addOnFailureListener { exception ->
                            // 데이터 저장 실패
                            println("Error saving user data: ${exception.message}")
                        }
                }
            }
            .addOnFailureListener { exception ->
                // 문서 가져오기 실패
                println("Error checking if document exists: ${exception.message}")
            }
    }
}