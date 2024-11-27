package com.example.movierating.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movierating.data.Collection
import com.example.movierating.data.Movie
import kotlinx.coroutines.launch

@Composable
fun CollectionTabContent(navController: NavController) {
    val collections = remember { mutableStateListOf<List<Movie>>() } // List<Movie> 의 List. 후에 컬렉션들의 movie 배열이 들어갈 예정
    val isLoading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // 팝업 메뉴의 상태 (열림/닫힘 여부)
    val isMenuExpanded = remember { mutableStateOf(false) }
    val isEditSelected = remember { mutableStateOf(false)}
    val showDeleteDialog = remember { mutableStateOf(false)}
    val selectedCollections = remember { mutableStateListOf<List<Movie>>() }
    val addingNewCollection = remember { mutableStateOf(false) }
    val newCollectionTitle = remember { mutableStateOf("") }

    // Firestore에서 데이터를 가져오는 로직
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedMovies = fetchMoviesFromFirestore()
            collections.addAll(
                listOf(
                    fetchedMovies.take(4), // 첫 번째 컬렉션 (최애영화)
                    fetchedMovies.takeLast(4) // 두 번째 컬렉션 (애니모음)
                    // 지금은 전체 데이터베이스에서 임의로 가져옴. 나중엔 컬렉션에서 가져올 예정
                )
            )
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
                                collections.add(emptyList())
                                addingNewCollection.value = true
                                // "새 컬렉션 추가" 동작
                            },
                            text = { Text("새 컬렉션 추가") }
                        )
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
                        IconButton(onClick = { showDeleteDialog.value = true}) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    title = { Text("컬렉션 삭제") },
                    text = { Text("선택한 컬렉션을 삭제하시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                collections.removeAll(selectedCollections)
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
                items(collections) { collection ->
                    CollectionCard(
                        movies = collection,
                        isSelected = selectedCollections.contains(collection),
                        isEdit = isEditSelected.value,
                        onClick = {
                            if (isEditSelected.value) {
                                if (selectedCollections.contains(collection)) {
                                    selectedCollections.remove(collection)
                                } else {
                                    selectedCollections.add(collection)
                                }
                            } else {
                                navController.navigate("collection")
                            }
                        }
                    )
                }
            }

        }
    }
}
