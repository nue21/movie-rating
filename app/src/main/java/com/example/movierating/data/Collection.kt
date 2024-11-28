package com.example.movierating.data

import java.time.LocalDateTime

data class Collection (
    val collectionId : String = "",
    val collectionName : String = "",
    val updatedTime: LocalDateTime,

    val movieList : List<String> = emptyList() // movie 객체의 DOCID를 가져올 예정입니다.
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "collectionName" to collectionName,
            "movieList" to movieList
        )
    }
}