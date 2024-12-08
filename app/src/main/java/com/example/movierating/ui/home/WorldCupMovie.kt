package com.example.movierating.ui.home

import com.google.firebase.Timestamp

data class WorldCupMovie(
    var DOCID: String = "",
    val title: String = "",
    val posters: String? = null,
    val comment: String? = null,
    val updatedTime: Timestamp? = null,
    val score: Double? = null,
    )
