package com.example.movierating.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun LikePage(modifier: Modifier = Modifier){
    val movieImgUrls = arrayOf(
        "http://file.koreafilm.or.kr/thm/02/99/18/55/tn_DPK022735.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/51/tn_DPF029805.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/50/tn_DPF029783.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/46/tn_DPF029705.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/43/tn_DPF029352.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/37/tn_DPF029035.jpg"
    )
    var isEditMode by rememberSaveable{ mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = Color.Transparent,
            contentColor = Color.Black,
            elevation = 0.dp,
        )

        Divider(color = Color.Gray, thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {isEditMode = !isEditMode}) {
                Text(
                    if(isEditMode) "선택삭제 ㆍ 편집완료" else "편집하기",
                    color = Color.Gray, fontSize = 12.sp)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.padding(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movieImgUrls.size) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(movieImgUrls[index]),
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .width(100.dp)
                            .height(150.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "영화 ${index + 1}",
                        fontSize = 12.sp
                    )
                    if (isEditMode) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Select",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    }
                }
            }
        }
    }
}