package com.example.movierating.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun WatchlistPage(){
    val movies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val filteredMovies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val isFilterApplied = remember { mutableStateOf(false) } // 필터 상태
    val isEditing = remember { mutableStateOf(false) } // 편집 모드 상태

    // 필터 버튼 클릭 시 처리
    val onFilterClick = {
        isFilterApplied.value = !isFilterApplied.value
        filteredMovies.value = if (isFilterApplied.value) {
            movies.value.filter { it.rating == null || it.rating == "" } // 평가되지 않은 영화 필터링
        } else {
            movies.value // 필터 해제 시 모든 영화 표시
        }
    }

    // 수정 아이콘 클릭 시 처리
    val onEditClick = {
        isEditing.value = !isEditing.value
    }

    // Firestore 데이터 가져오기
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedMovies = fetchMoviesFromFirestore() // Firestore에서 영화 목록 가져오기
            movies.value = fetchedMovies
            isLoading.value = false // 데이터 로드 완료 후 로딩 상태 해제
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onFilterClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if(isFilterApplied.value) Color.Black else Color.LightGray
            ),
            modifier = Modifier
                .width(160.dp)
                .height(36.dp)
                .padding(vertical = 2.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            if (isFilterApplied.value) {
                Icon(
                    imageVector = Icons.Outlined.Close, // X 아이콘
                    contentDescription = "Close Filter",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if(isFilterApplied.value)"편집취소" else "평가되지 않은 영화",
                color = if(isFilterApplied.value)Color.White else Color.Black,
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
            CircularProgressIndicator() // 로딩 중 표시
        }
    } else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(movies.value) { movie ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ){
                        Image(
                            painter = rememberAsyncImagePainter(movie.posters),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Movie Poster",
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(2f/3f)
                                .clip(RoundedCornerShape(8.dp))
                        )
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
    }
}

