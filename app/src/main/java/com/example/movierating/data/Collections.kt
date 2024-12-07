package com.example.movierating.data

import com.google.firebase.Timestamp

data class Collections(
    val userId: String = "",
    val collectionId: String = "",
    val collectionName: String = "",
    val updatedTime: Timestamp? = null, // localDateTime -> Timestamp (firebase.Timestamp)

    var movieList: List<String> = emptyList() // movie 객체의 DOCID를 가져올 예정입니다.
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "collectionName" to collectionName,
            "movieList" to movieList,
            "updateTime" to updatedTime,
            "collectionId" to collectionId
        )
    }
}
