package com.example.movierating.ui.movieInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollection(modifier: Modifier = Modifier,){
    val collections = listOf("최애영화", "애니모음")
    var newCollectionName by remember { mutableStateOf(TextFieldValue("")) }
    var isEditing by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "저장 위치",
                    fontSize = 18.sp,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Confirm",
                        tint = Color.Black
                    )
                }
            },
            modifier = Modifier.height(56.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        // 새 컬렉션 추가 섹션
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isEditing = true  }
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add Collection",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                if (isEditing) {
                    // 입력 필드 (밑줄 색상 변경)
                    TextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        placeholder = { Text("새 컬렉션 추가", color = Color.Gray) },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Black,  // 포커스 시 밑줄 색상
                            unfocusedIndicatorColor = Color.Black,  // 비포커스 시 밑줄 색상
                            cursorColor = Color.Black  // 커서 색상
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    if (newCollectionName.text.isNotEmpty()) {
                        IconButton(onClick = { newCollectionName = TextFieldValue("") }) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                }
                else{
                    Column {
                        Text(
                            text = "새 컬렉션 추가",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        // 밑줄 추가
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .height(1.dp)
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 컬렉션 목록 표시
        CollectionList(collections)
    }
}

// 컬렉션 목록 컴포저블
@Composable
fun CollectionList(collections: List<String>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(collections) { collection ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 빈 박스 추가
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = collection,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}