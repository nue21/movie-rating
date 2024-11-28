package com.example.movierating.ui.signIn

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
