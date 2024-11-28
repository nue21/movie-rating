package com.example.movierating.data

import java.time.LocalDateTime

data class MovieRated(
    val movie: String = "", // movie 객체의 DOCID를 가져올 예정입니다.
    val score: Double? = null,
    val comment: String? = null,
    val updatedTime: LocalDateTime // 시간 아닌 생성 순서를 넣어 볼까 고민입니다.
) {
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "movie" to movie,
            "score" to score,
            "comment" to comment,
            "updatedTime" to updatedTime
        )
    }
}