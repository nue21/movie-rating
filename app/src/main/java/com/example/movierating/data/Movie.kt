package com.example.movierating.data

data class Movie(
    val DOCID: String = "",
    val title: String = "",
    val nation: String = "",
    val directors: List<String> = emptyList(),
    val actors: List<String> = emptyList(),
    val plots: List<String> = emptyList(),
    val runtime: String? = null,
    val rating: String? = null,
    val genre: String? = null,
    val posters: String? = null
) {
    // Movie 객체를 Map으로 변환
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "nation" to nation,
            "directors" to directors,
            "actors" to actors,
            "plots" to plots,
            "runtime" to runtime,
            "rating" to rating,
            "genre" to genre,
            "posters" to posters
        )
    }
}