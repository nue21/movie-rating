package com.example.movierating.ui.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movierating.data.Collections
import com.example.movierating.data.Movie
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CollectionTab(navController: NavController) {
    // user의 컬렉션 리스트
    val collections = remember { mutableStateOf<List<Collections>>(emptyList()) }
    // ( Collection : List<Movie> ) 로 Map 형식으로 되어있다. 각 컬렉션에 속한 Movie 객체들
    val collectionWithMovies = remember { mutableStateOf<List<Pair<Collections, List<Movie>>>>(emptyList()) }
    // 전체 영화
    val movies = remember { mutableStateOf<List<Movie>>(emptyList())}
    // 비동기로 영화 데이터 및 유저 데이터를 받아오는 동안 작동한다
    val isLoading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val selectedCollections = remember { mutableStateListOf<Collections>() }

    // 팝업 메뉴의 상태 (열림/닫힘 여부)
    val isMenuExpanded = remember { mutableStateOf(false) }
    // 편집 기능 작동 상태 여부
    val isEditSelected = remember { mutableStateOf(false)}

    val showDeleteDialog = remember { mutableStateOf(false)}
    // 새 컬렉션 추가 기능이 작동할 떄 true
    val addingNewCollection = remember { mutableStateOf(false) }
    val newCollectionTitle = remember { mutableStateOf("") }

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val userData = remember { mutableStateOf<UserData?>(null) }

    LaunchedEffect(user) {
        user?.let {
            val userId = it.uid
            // Firestore에서 해당 userId로 문서 조회
            db.collection("user")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // 첫 번째 문서를 UserData 객체로 변환
                        val document = documents.documents.first()
                        userData.value = document.toObject(UserData::class.java)
                    } else {
                        Log.d("Firestore", "No matching user found")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error querying user data", e)
                }
        }
    }

    // Firestore에서 데이터를 가져오는 로직
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Firestore에서 컬렉션 데이터 가져오기
            val collectionTemp = fetchCollectionMoviesFromFirestore()
            movies.value = fetchMoviesFromFirestore()

            // userData가 null이 아닌 경우에만 필터링
            val userId = userData.value?.userId
            if (userId != null) {
                val filteredCollections = collectionTemp.filter { it.userId == userId }

                // collections 업데이트
                collections.value = filteredCollections
            } else {
                collections.value = emptyList() // userId가 null인 경우 빈 리스트로 설정
            }

            // collectionWithMovies 값 설정 (비동기 작업 완료 후)
            collectionWithMovies.value = collections.value.map { collection ->
                val filteredMovies = collection.movieList.mapNotNull { movieId ->
                    movies.value.find { it.DOCID == movieId }
                }
                collection to filteredMovies // Collections 객체와 해당 Movies 리스트를 쌍으로 저장
            }

            // 비동기 작업이 완료되었음을 표시
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End // 우측 정렬
        ) {
            Box {
                if (!isEditSelected.value) {
                    IconButton(onClick = { isMenuExpanded.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded.value,
                        onDismissRequest = { isMenuExpanded.value = false },
                        modifier = Modifier.offset(x = 0.dp, y = 0.dp) // 메뉴를 아이콘 아래로 이동
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded.value = false
                                isEditSelected.value = true
                                // "컬렉션 편집" 동작
                            },
                            text = { Text("컬렉션 편집") }
                        )
                    }
                }
                // edit이 선택되었을 때
                else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Button(
                                onClick = {
                                    // 편집 취소
                                    selectedCollections.clear()
                                    isEditSelected.value = false
                                }, // 코멘트 표시 토글
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null
                                )
                                Text(
                                    text = "편집 취소"
                                )
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                            Button(
                                onClick = {
                                    // 편집 완료
                                    selectedCollections.clear()
                                    isEditSelected.value = false
                                }, // 코멘트 표시 토글
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                                Text(
                                    text = "편집 완료"
                                )
                            }
                        }
                        IconButton(
                            onClick = { showDeleteDialog.value = true}
                        ) { // 삭제 버튼. 삭제 다이얼로그 띄움
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            if (addingNewCollection.value) {
                // 새로운
            }
            // 삭제 다이얼로그
            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    title = { Text("컬렉션 삭제") },
                    text = { Text("선택한 컬렉션을 삭제하시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // 선택한 컬렉션을 DB에서 삭제
                                val selectedCollectionIds =
                                    selectedCollections.map { it.collectionId }
                                deleteCollectionsFromFirestore(selectedCollectionIds)

                                // 삭제 후 상태 업데이트
                                collections.value = collections.value.filterNot { collection ->
                                    selectedCollectionIds.contains(collection.collectionId)
                                }
                                collectionWithMovies.value =
                                    collectionWithMovies.value.filterNot { (collection, _) ->
                                        selectedCollectionIds.contains(collection.collectionId)
                                    }

                                // 삭제 후 상태 초기화
                                selectedCollections.clear()
                                showDeleteDialog.value = false
                            }
                        ) {
                            Text("삭제")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog.value = false }) {
                            Text("취소")
                        }
                    }
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 한 줄에 두 개의 셀
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // 셀 간의 가로 간격
                verticalArrangement = Arrangement.spacedBy(16.dp) // 줄 간의 세로 간격
            ) {
                items(collectionWithMovies.value) { (collection, filteredMovies) ->
                    CollectionCard(
                        movies = filteredMovies, // 필터링된 영화 리스트 전달
                        isSelected = selectedCollections.contains(collection),
                        isEdit = isEditSelected.value,
                        collections = collection,
                        onClick = {
                            // 편집 상태면 클릭 시 테두리 생김
                            if (isEditSelected.value) {
                                if (selectedCollections.contains(collection)) {
                                    selectedCollections.remove(collection)
                                } else {
                                    selectedCollections.add(collection)
                                }
                            } else {
                                navController.navigate("collectionDetailPage/${collection.collectionId}")
                            }
                        }
                    )
                }
            }
        }
    }
}

fun addCollectionToFirestore(collection: Collections) {
    val db = FirebaseFirestore.getInstance()

    // Firestore에 저장할 데이터 준비
    val collectionMap = collection.toMap()

    try {
        // "Collections" 컬렉션에 새로운 문서를 추가 (자동 ID 생성)
        db.collection("collections")
            .add(collectionMap) // 비동기적으로 Firestore 작업 완료까지 대기

        println("Collection added successfully")
    } catch (e: Exception) {
        println("Error adding collection: $e")
    }
}


fun deleteCollectionsFromFirestore(collectionIds: List<String>) {
    // Firestore에서 컬렉션 삭제
    val db = FirebaseFirestore.getInstance()
    collectionIds.forEach { collectionId ->
        db.collection("collections").document(collectionId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "컬렉션 삭제 성공: $collectionId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "컬렉션 삭제 실패", e)
            }
    }
}

fun updateCollectionNameInDb(newName: String, collectionId: String) {
    // Firebase Firestore의 컬렉션에 접근
    val db = FirebaseFirestore.getInstance()

    // 업데이트할 컬렉션의 문서에 접근
    val collectionRef = db.collection("collections").document(collectionId)

    // 컬렉션 이름 업데이트
    collectionRef.update("collectionName", newName)
        .addOnSuccessListener {
            // 업데이트 성공 시 처리
            Log.d("Firestore", "Collection name updated successfully")
        }
        .addOnFailureListener { e ->
            // 오류 발생 시 처리
            Log.e("Firestore", "Error updating collection name", e)
        }
}

@Composable
fun CollectionCard(
    movies: List<Movie>,
    isSelected: Boolean,    // true : 테두리 / false : 테두리 X
    onClick: () -> Unit,  // edit 이 true 인 상태에서 click 하면 테두리 on off / edit 이 false면 해당 컬렉션 페이지로 이동
    isEdit: Boolean,    // true : 이름 편집 / false : 기본 컬렉션 이름 출력
    collections: Collections
) {
    var title = remember { mutableStateOf(collections.collectionName) }
    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 0.dp, // 선택 여부에 따라 테두리 두께 변경
                color = if (isSelected) Color.Gray else Color.White, // 선택 여부에 따라 색상 변경
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 영화 포스터 섹션
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // movies가 비어있는 경우 대체 UI 출력
                if (movies.isNotEmpty() && movies[0].posters != null) {
                    movies[0].posters?.let { MovieImage(it) }
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 120.dp) // 고정 크기 설정
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray), // 회색 배경
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image",
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // 컬렉션 제목
            if (!isEdit) {
                Text(
                    text = title.value,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            else {
                Row(

                ) {
                    Spacer(modifier = Modifier.padding(18.dp))
                    TextField(
                        value = title.value,
                        onValueChange = { title.value = it },
                        modifier = Modifier.width(100.dp).height(56.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                        placeholder = { Text(text = title.value) }
                    )
                    IconButton(
                        onClick = {
                            updateCollectionNameInDb(title.value, collections.collectionId)
                        }
                    ) { // 삭제 버튼. 삭제 다이얼로그 띄움
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
