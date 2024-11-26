package com.example.movierating.data

data class User(
    val userId: String = "", // google auth에서 받아올 고유 ID(식별자)
    val userName: String = "",   // google auth에서 받아올 닉네임(추후 변경 가능)
    val userEmail: String = "", // google auth에서 받아옴

    val collectionList: List<String> = emptyList(),
    val wishList: List<String> = emptyList(),
    val movieRatedList: List<String> = emptyList()
) {
    fun toMap() :Map<String, Any?>{
        return mapOf (
            "userId" to userId,
            "userName" to userName,
            "collectionList" to collectionList,
            "wishList" to wishList,
            "movieRatedList" to movieRatedList
        )
    }
}