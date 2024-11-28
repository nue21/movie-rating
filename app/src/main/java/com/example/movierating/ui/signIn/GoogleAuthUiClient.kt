package com.example.movierating.ui.signIn

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.example.movierating.ui.user.UserRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth
    private val firestore = FirebaseFirestore.getInstance()
    private val userRepository = UserRepository(auth, firestore)

    suspend fun signIn() : IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val authResult = auth.signInWithCredential(googleCredentials).await()
            // user가 null일 경우 예외 발생
            val user = authResult.user ?: throw IllegalStateException("User is null after successful authentication")

            // 최초 로그인 (신규 가입자의 경우)
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser)   // DB에 user data 세팅 필요
                registerNewUser(user)

            SignInResult(
                data = userRepository.getUserData(user),
                errorMessage = null
            )
        } catch(e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    private fun registerNewUser(newUser: FirebaseUser) {
        // DB에 저장될 userData 준비
        val userDataToDB = mapOf(
            "username" to newUser.displayName,
            "profilePictureUrl" to newUser.photoUrl?.toString(),
            "collectionList" to emptyList<String>(),
            "movieRatedList" to emptyList<String>(),
            "wishList" to emptyList<String>()
        )

        // user 컬렉션 아래 uid를 doc id로 한 Document 추가
        firestore.collection("user").document(newUser.uid)
            .set(userDataToDB)
            .addOnFailureListener { e ->
                throw IllegalStateException("firestore upload fail, Error adding document: $e")
            }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}