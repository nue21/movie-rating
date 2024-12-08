package com.example.movierating.data

import com.google.firebase.Timestamp
import java.time.LocalDateTime

data class MovieRated(
    val movie: String = "", // movie 객체의 DOCID를 가져올 예정입니다.
    val score: Double? = null,
    val comment: String? = null,
    val updatedTime: Timestamp? = null, // 시간 아닌 생성 순서를 넣어 볼까 고민입니다.
    val userId: String = "", // 평가 유저의 ID를 받아옵니다
) {
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "movie" to movie,
            "score" to score,
            "comment" to comment,
            "updatedTime" to updatedTime,
            "userId" to userId
        )
    }
}