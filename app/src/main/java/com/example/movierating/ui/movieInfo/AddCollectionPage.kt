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
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.data.Collections
import com.example.movierating.data.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollectionPage(modifier: Modifier = Modifier, navController: NavController, docId: String?){
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()
    var collectionList by remember { mutableStateOf<List<String>>(emptyList()) }
    var newCollectionName by remember { mutableStateOf(TextFieldValue("")) }
    var isEditing by remember { mutableStateOf(false) }
    var collections by remember { mutableStateOf<List<Collections>>(emptyList()) }
    var selectedCollectionId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            firestore.collection("collections")
                .whereEqualTo("userId", currentUser.uid) // userId로 필터링
                .get()
                .addOnSuccessListener { collectionDocuments ->
                    val fetchedCollections = collectionDocuments.documents.mapNotNull { doc ->
                        doc.toObject(Collections::class.java)
                    }
                    collections = fetchedCollections
                }
        }
    }

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
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    if (!docId.isNullOrBlank() && !selectedCollectionId.isNullOrBlank()){
                        firestore.collection("collections")
                            .document(selectedCollectionId!!)
                            .update("movieList", FieldValue.arrayUnion(docId))
                            .addOnSuccessListener {
                                // 저장 성공 후 선택 해제
                                selectedCollectionId = null
                            }
                            .addOnFailureListener {
                                println("Failed to update movieList: ${it.message}")
                            }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Confirm",
                        tint = if (selectedCollectionId.isNullOrBlank()) Color.Gray else Color.Black
                    )
                }
            },
            modifier = Modifier.height(56.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

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
                    IconButton(
                        onClick = {
                            if (newCollectionName.text.isNotBlank() && currentUser != null) {
                                val newCollectionId = System.currentTimeMillis().toString()
                                val newCollection = Collections(
                                    userId = currentUser.uid,
                                    collectionId = newCollectionId,
                                    collectionName = newCollectionName.text,
                                    updatedTime = Timestamp.now(),
                                    movieList = emptyList()
                                )
                                firestore.collection("collections")
                                    .document(newCollectionId)
                                    .set(newCollection.toMap())
                                    .addOnSuccessListener {
                                        // 유저의 collectionList 업데이트
                                        firestore.collection("user")
                                            .document(currentUser.uid)
                                            .update("collectionList", FieldValue.arrayUnion(newCollectionId))
                                            .addOnSuccessListener {
                                                collections = collections + newCollection
                                                newCollectionName = TextFieldValue("")
                                                isEditing = false
                                            }
                                            .addOnFailureListener {
                                                println("Failed to update user's collectionList: ${it.message}")
                                            }
                                    }
                            }
                        }
                    ){
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = "Add", tint = Color.Black)
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
        CollectionList(
            collectionList = collections,
            selectedCollectionId = selectedCollectionId,
            onCollectionClick = { clickedCollectionId ->
                selectedCollectionId = clickedCollectionId
            }
        )
    }
}

// 컬렉션 목록 컴포저블
@Composable
fun CollectionList(
    collectionList: List<Collections>,
    selectedCollectionId: String?,
    onCollectionClick: (String?) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(collectionList) { collection ->
            val isSelected = collection.collectionId == selectedCollectionId
            var posterUrl by remember { mutableStateOf<String?>(null) }
            val firstMovieDocId = collection.movieList.firstOrNull()

            LaunchedEffect(firstMovieDocId){
                if (!firstMovieDocId.isNullOrBlank()) {
                    // Firestore에서 해당 DOCID의 영화 데이터 가져오기
                    firestore.collection("movies")
                        .document(firstMovieDocId)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val poster = document.getString("posters")
                                posterUrl = poster
                            }
                        }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelected) {
                            onCollectionClick(null)
                        } else {
                            onCollectionClick(collection.collectionId)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .background(if (isSelected) Color.LightGray else Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!posterUrl.isNullOrBlank()) {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(posterUrl),
                        contentDescription = "Movie Poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    )
                }
                else{
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (isSelected) Color.DarkGray.copy(alpha = 0.5f) else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = collection.collectionName,
                    fontSize = 16.sp,
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}