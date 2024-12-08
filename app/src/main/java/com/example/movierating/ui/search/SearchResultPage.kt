package com.example.movierating.ui.search

import android.content.Context
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.data.Movie

@RequiresApi(35)
@Composable
fun SearchResultPage(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    backToSearchPage: () -> Unit,
    goToDetailPage: (String) -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }
    val searchInput by searchViewModel.searchInput.collectAsState()

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row (
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = {
                    backToSearchPage()
                    searchViewModel.updateSearchInput("")
                },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFE00909)
                )
            }
            SearchBar(
                searchInput = searchInput,
                changeSearchInput = { searchViewModel.updateSearchInput(it) },
                onClickSearch = {
                    searchViewModel.updateSearchHistory()
                },
                isSearching,
                changeSearchingState = { isSearching = it }
            )
        }
        SearchResultList(
            searchViewModel.resultMovies.value,
            goToDetailPage
        )
    }
}

fun getSubText (prodYear: String, runtime: String ): String {
    return prodYear + "ㆍ" + (runtime.toInt()/60) + "시간 " + (runtime.toInt()%60) + "분"
}

@Composable
fun SearchResultList (
    results: List<Movie>,
    goToDetailPage: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 데이터 리스트를 이용하여 항목을 추가
            items(results) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { goToDetailPage(item.DOCID) },
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .width(86.dp)
                            .height(123.dp)
                            .clip(RoundedCornerShape(5.dp)), // 이미지 둥근 모서리 처리
                        color = Color.LightGray // 이미지가 로드되지 않을 경우 색상
                    ) {
                        AsyncImage(
                            model = item.posters,
                            contentDescription = "Movie Thumbnail",
                            contentScale = ContentScale.Crop // 이미지 비율 유지하며 채우기
                        )
                    }
                    Column (
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.title?: "기본값",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(text = getSubText(item.year?:"",item.runtime?:"0"))
                    }
                }
            }
        }
    }
}