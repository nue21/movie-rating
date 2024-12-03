package com.example.movierating.data

data class User(
    val userId: String = "", // google auth에서 받아올 고유 ID(식별자)
    val userName: String? = "",   // google auth에서 받아올 닉네임(추후 변경 가능)
    // val userEmail: String = "", // google auth에서 받아옴
    val profilePictureUrl: String? = "",

    val collectionList: List<String> = emptyList(), // collection 문서 ID 값
    val wishList: List<String> = emptyList(), // Movie 문서 ID 값
    val movieRatedList: List<String> = emptyList() // Movie 문서 ID 값
) {
    fun toMap() :Map<String, Any?>{
        return mapOf (
            "userId" to userId,
            "userName" to userName,
            "profilePictureUrl" to profilePictureUrl,
            "collectionList" to collectionList,
            "wishList" to wishList,
            "movieRatedList" to movieRatedList
        )
    }
}