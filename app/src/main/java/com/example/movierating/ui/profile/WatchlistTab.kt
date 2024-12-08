package com.example.movierating.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.data.Movie
import com.example.movierating.data.User
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun WatchlistTab(){
    val movies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val filteredMovies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val isFilterApplied = remember { mutableStateOf(false) } // 필터 상태
    val isEditing = remember { mutableStateOf(false) } // 편집 모드 상태
    val isDeleteDialogOpen = remember { mutableStateOf(false) } // 삭제 팝업 상태
    val movieToDelete = remember { mutableStateOf<Movie?>(null) } // 삭제할 영화 정보
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()
    val selectedMovies = remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            firestore.collection("user")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.documents.first().toObject(User::class.java)
                        user?.wishList?.let { wishList ->
                            loadMoviesFromWishList(wishList, movies, isLoading)
                        }
                    } else {
                        Log.e("WatchlistTab", "No user data found")
                        isLoading.value = false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("WatchlistTab", "Failed to load user data", exception)
                    isLoading.value = false
                }
        }
    }

    // 필터 버튼 클릭 시 처리
    val onFilterClick = {
        /*filteredMovies.value = if (isFilterApplied.value) {
            movies.value.filter { it.rating == null || it.rating == "" } // 평가되지 않은 영화 필터링
        } else {
            movies.value // 필터 해제 시 모든 영화 표시
        }*/
        if(isEditing.value){
            selectedMovies.value = emptySet()
            isEditing.value = false
            isFilterApplied.value = isFilterApplied.value
        }
        else{
            isFilterApplied.value = !isFilterApplied.value
        }
    }

    // 수정 아이콘 클릭 시 처리
    val onEditClick = {
        if(isEditing.value){
            isDeleteDialogOpen.value = true
        }
        else{
            isEditing.value = true
        }
    }

    /*val onDeleteClick = { movie: Movie ->
        movieToDelete.value = movie
        isDeleteDialogOpen.value = true
    }*/

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onFilterClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if(isFilterApplied.value || isEditing.value) Color.Black else Color.LightGray
            ),
            modifier = Modifier
                .width(160.dp)
                .height(36.dp)
                .padding(vertical = 2.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            if (isFilterApplied.value || isEditing.value) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close Filter",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if(isEditing.value)"편집 취소" else "평가되지 않은 영화",
                color = if(isFilterApplied.value || isEditing.value)Color.White else Color.Black,
                fontSize = 12.sp
            )
        }
        IconButton(
            onClick = onEditClick,
            modifier = Modifier.size(36.dp)
        ) {
            if(isEditing.value){
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray
                )
            }else{
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = Color.Gray
                )
            }
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movies.value) { movie ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(2f / 3f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedMovies.value.contains(movie.DOCID)) 2.dp else 0.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = isEditing.value) {
                                    toggleSelection(movie.DOCID, selectedMovies)
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(movie.posters),
                                contentScale = ContentScale.Crop,
                                contentDescription = "Movie Poster",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            text = movie.title,
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
        if (isDeleteDialogOpen.value) {
            DeleteDialog(
                onCancel = { isDeleteDialogOpen.value = false },
                onDelete = {
                    // 삭제 로직은 나중에 구현
                    isDeleteDialogOpen.value = false
                }
            )
        }
    }
}

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onDelete: () -> Unit
){
    AlertDialog(
        onDismissRequest = {},
        title = { Text("영화 삭제") },
        text = { Text("선택한 영화가 보고싶어요 목록에서 삭제됩니다.") },
        modifier = modifier,
        confirmButton = {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp)
            ){
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Blue)
                ) {
                    Text(text = "취소")
                }
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text(text = "삭제")
                }
            }
        }
    )
}

private fun toggleSelection(movieId: String, selectedMovies: MutableState<Set<String>>) {
    val updatedSet = selectedMovies.value.toMutableSet()
    if (updatedSet.contains(movieId)) {
        updatedSet.remove(movieId)
    } else {
        updatedSet.add(movieId)
    }
    selectedMovies.value = updatedSet
}

private fun loadMoviesFromWishList(
    wishList: List<String>,
    movies: MutableState<List<Movie>>,
    isLoading: MutableState<Boolean>
) {
    val firestore = FirebaseFirestore.getInstance()
    val movieList = mutableListOf<Movie>()
    val remainingCount = mutableStateOf(wishList.size)

    wishList.forEach { movieId ->
        firestore.collection("movies")
            .document(movieId)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(Movie::class.java)?.let { movie ->
                    movieList.add(movie)
                }
                remainingCount.value -= 1
                if (remainingCount.value == 0) {
                    movies.value = movieList
                    isLoading.value = false
                }
            }
            .addOnFailureListener { exception ->
                Log.e("WatchlistTab", "Failed to load movie $movieId", exception)
                remainingCount.value -= 1
                if (remainingCount.value == 0) {
                    isLoading.value = false
                }
            }
    }
}