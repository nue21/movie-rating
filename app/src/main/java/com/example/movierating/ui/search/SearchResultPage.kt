package com.example.movierating.ui.search

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun SearchResultPage(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    backToSearchPage: () -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }

    val map1: Map<String, String> = mapOf("title" to "그 여름날의 거짓말", "prodYear" to "2024", "runtime" to "138", "poster" to "http://file.koreafilm.or.kr/thm/02/99/18/53/tn_DPK022632.jpg")
    val map2: Map<String, String> = mapOf("title" to "여름이 끝날 무렵의 라트라비아타", "prodYear" to "2023", "runtime" to "115", "poster" to "http://file.koreafilm.or.kr/thm/02/99/18/53/tn_DPK022638.jpg")
    val map3: Map<String, String> = mapOf("title" to "여름을 향한 터널, 이별의 출구", "prodYear" to "2022", "runtime" to "83", "poster" to "http://file.koreafilm.or.kr/thm/02/99/18/16/tn_DPF027820.jpg")

    val results = listOf(map1, map2, map3)

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
                searchInput = searchViewModel.searchInput,
                changeSearchInput = { searchViewModel.updateSearchInput(it) },
                onClickSearch = {
                    searchViewModel.updateSearchHistory()
                },
                isSearching,
                changeSearchingState = { isSearching = it }
            )
        }
        SearchResultList(
            results
        )
    }
}

fun getSubText (prodYear: String, runtime: String ): String {
    return prodYear + "ㆍ" + (runtime.toInt()/60) + "시간 " + (runtime.toInt()%60) + "분"
}

@Composable
fun SearchResultList (
    results: List<Map<String, String>>
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
                        .clickable { },
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(item["poster"]),
                        contentDescription = "movie",
                        modifier = Modifier
                            .size(86.dp, 123.dp)
                            .clip(RoundedCornerShape(5.dp)),
                    )
                    Column (
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item["title"] ?: "기본값",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(text = getSubText(item["prodYear"]?:"",item["runtime"]?:"0"))
                    }
                }
            }
        }
    }
}