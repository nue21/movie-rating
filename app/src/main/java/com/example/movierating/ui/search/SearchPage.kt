package com.example.movierating.ui.search

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movierating.ui.theme.MovieRatingTheme
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalFocusManager
import com.example.movierating.data.Movie
import com.google.firebase.firestore.FirebaseFirestore


@RequiresApi(35)
@Composable
fun SearchPage (
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    goToResultPage: () -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }
    // 검색어 상태 관리
    val searchInput by searchViewModel.searchInput.collectAsState()
    val moviesRef = FirebaseFirestore.getInstance().collection("movies")
    var autoComplete = searchViewModel.resultMovies
    // Debounced 검색어 상태를 저장
    var debouncedInput by remember { mutableStateOf("") }

    // Debounce 로직 추가
//    LaunchedEffect(searchInput) {
//        delay(300L) // 300ms 대기 (사용자가 입력을 멈출 때까지 대기)
//        debouncedInput = searchInput
//    }

//    LaunchedEffect(debouncedInput) {
//        if(debouncedInput.isBlank()) return@LaunchedEffect
//
//        val querySnapshot = moviesRef
//            .whereGreaterThanOrEqualTo("title", debouncedInput)
//            .whereLessThanOrEqualTo("title", debouncedInput + '\uf8ff')
//            .limit(10) // 결과 수 제한
//            .get()
//            .await()
//
//        if (querySnapshot.isEmpty) {
//            Log.d("MovieSearch", "No movies found: $debouncedInput")
//            autoComplete = emptyList()
//        } else {
//            val movieList = querySnapshot.mapNotNull { document ->
//                document.toObject(Movie::class.java)
//            }
//            autoComplete = movieList
//        }
//    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            searchInput = searchInput,
            changeSearchInput = { searchViewModel.updateSearchInput(it) },
            onClickSearch = {
                searchViewModel.updateSearchHistory()
                goToResultPage()
            },
            isSearching,
            changeSearchingState = { isSearching = it }
        )
        
        if(isSearching) {
            SearchAutoComplete(
                autoComplete,
                onClickAutoComplete = {
                    searchViewModel.updateSearchInput(it)
                    searchViewModel.updateSearchHistory()
                    goToResultPage()
                }
            )
        } else {
            SearchHistoryList(
                searchViewModel.searchHistory,
                resetSearchHistory = { searchViewModel.resetSearchHistory() },
                onClickSearchHistory = {
                    searchViewModel.updateSearchInput(it)
                    searchViewModel.updateSearchHistory()
                    goToResultPage()
                }
            )
        }
    }
}

@Composable
fun SearchAutoComplete (
    autoComplete: State<List<Movie>>,
    onClickAutoComplete: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // 데이터 리스트를 이용하여 항목을 추가
            items(autoComplete.value) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickAutoComplete(item.title) },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = item.title)
                }
            }
        }
    }
}

@Composable
fun SearchHistoryList (
    searchHistory: State<List<String>>,
    resetSearchHistory: () -> Unit,
    onClickSearchHistory: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "최근 검색어")
            TextButton(
                onClick = resetSearchHistory,
                contentPadding = PaddingValues(0.dp), // 기본 패딩 제거
            ) {
                Text(
                    text = "모두 삭제",
                    color = Color(0xFFE00909)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 데이터 리스트를 이용하여 항목을 추가
            items(searchHistory.value) { item ->
                Row (
                    modifier = Modifier
                        .clickable { onClickSearchHistory(item) },
                ) {
                    Text(text = item)
                }
            }
        }
    }
}


@Composable
fun SearchBar (
    searchInput: String,
    changeSearchInput: (String) -> Unit,
    onClickSearch: () -> Unit,
    isSearching: Boolean,
    changeSearchingState : (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current    // 키보드 포커스 관리

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF2F2F2))
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "search",
            modifier = Modifier.size(20.dp),
            tint = Color(0xFFE00909)
        )
        BasicTextField(
            value = searchInput,
            onValueChange = { changeSearchInput(it) },
            modifier = Modifier
                .padding(0.dp)
                .height(20.dp)
                .weight(1f)
                .onFocusChanged { focusState ->
                    changeSearchingState(focusState.isFocused)
                },
            textStyle = TextStyle(
                fontSize = 14.sp
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onClickSearch()
                }
            )
        )
        if (isSearching) {
            IconButton(
                onClick = {
                    changeSearchInput("")
                    changeSearchingState(false)
                    focusManager.clearFocus()
                },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = "clear",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFE00909)
                )
            }
        }

    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieRatingTheme {
//        SearchAutoComplete()
    }
}
