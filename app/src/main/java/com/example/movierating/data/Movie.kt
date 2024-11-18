package com.example.movierating.data

data class Movie(
    val DOCID: String,
    val title: String,
    val nation: String,
    val directors: List<String>,
    val actors: List<String>,
    val plots: List<String>,
    val runtime: String?,
    val rating: String?,
    val genre: String?,
    val posters: String?
)
