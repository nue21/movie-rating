package com.example.movierating.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movierating.data.Movie
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // 현재 로그인된 사용자 정보 가져오기
    suspend fun getCurrentUserData(): UserData? {
        val user = auth.currentUser
        return if (user != null)
            getUserData(user)
        else
            null
    }

    // 특정 사용자 정보 가져오기
    suspend fun getUserData(user: FirebaseUser): UserData {
        // "user" collection의 uid를 docId로 하는 문서 불러오기
        val document = firestore.collection("user").document(user.uid).get().await()
        val userData = document.toObject(UserData::class.java)

        return user.run {
            UserData(
                userId = uid,
                username = userData?.username ?: displayName,
                profilePictureUrl = userData?.profilePictureUrl ?: photoUrl?.toString(),
                collectionList = userData?.collectionList ?: emptyList(),
                movieRatedList = userData?.movieRatedList ?: emptyList(),
                wishList = userData?.wishList ?: emptyList()
            )
        }
    }

    fun updateWishList(userId: String, movieId: String, add: Boolean, onComplete: (Boolean) -> Unit) {
        val userDocRef = firestore.collection("user").document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            val currentWishList = snapshot.get("wishList") as? List<String> ?: emptyList()

            val updatedWishList = if (add) {
                currentWishList + movieId
            } else {
                currentWishList - movieId
            }

            transaction.update(userDocRef, "wishList", updatedWishList)
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _userData: MutableLiveData<UserData?> = MutableLiveData(null)
    val userData: LiveData<UserData?> get() = _userData
    init {
        loadUserData()
    }

    private fun loadUserData() {
        // viewModelScope 내에서 suspend 함수 호출
        viewModelScope.launch {
            try {
                // 실제 데이터를 로딩하는 로직
                _userData.value = repository.getCurrentUserData()
            } catch (e: Exception) {
                // 예외 처리
                e.printStackTrace()
            }
        }
    }

    fun resetUserData() {
        _userData.value = null
    }

    // updateUserData() 함수도 필요할 것으로 보임
}