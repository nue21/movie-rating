package com.example.movierating.ui.movieInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.R

@Composable
fun MovieInfo(modifier: Modifier = Modifier){
    Column(modifier = modifier.fillMaxSize()) {
        val movieImgUrls = arrayOf(
            "http://file.koreafilm.or.kr/thm/02/99/18/55/tn_DPK022735.jpg",
            "http://file.koreafilm.or.kr/thm/02/99/18/51/tn_DPF029805.jpg",
            "http://file.koreafilm.or.kr/thm/02/99/18/50/tn_DPF029783.jpg",
            "http://file.koreafilm.or.kr/thm/02/99/18/46/tn_DPF029705.jpg",
            "http://file.koreafilm.or.kr/thm/02/99/18/43/tn_DPF029352.jpg",
            "http://file.koreafilm.or.kr/thm/02/99/18/37/tn_DPF029035.jpg"
        )
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = Color.Transparent,
            contentColor = Color.Black,
            elevation = 0.dp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ){
            Row() {
                Image(
                    painter = rememberAsyncImagePainter(movieImgUrls[0]),
                    contentDescription = "movie",
                    modifier = Modifier
                        .width(200.dp)
                        .height(280.dp)
                        .size(120.dp, 123.dp),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "이프 온리", fontSize = 24.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "2004 · 코미디 · 남녀", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "1시간 35분", fontSize = 14.sp, color = Color.Gray)
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // 별점
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 1..5) {
                Icon(
                    painter = painterResource(R.drawable.ic_fullstar),
                    contentDescription = "Star Rating",
                    tint = if (i <= 3) Color.Red else Color.LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 버튼들 ("보고싶어요", "코멘트", "컬렉션")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(icon = Icons.Default.Add, label = "보고싶어요", backgroundColor = Color.LightGray)
            ActionButton(icon = Icons.Default.Edit, label = "코멘트", backgroundColor = Color.LightGray)
            ActionButton(icon = Icons.Default.Menu, label = "컬렉션", backgroundColor = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 영화 설명
        Text(
            text = "눈앞에서 사랑하는 연인 사만다를 잃은 이안. 다음 날 자신의 옆에서 자고 있는 사만다를 발견하고, 이내 정해진 운명을 바꿀 수 없단 걸 깨닫는 이안은 더 늦기 전에 자신의 진실을 전하려 한다.",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 14.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 감독 및 배우 정보
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = "감독", fontSize = 14.sp, color = Color.Gray)
            Text(text = "길 정거", fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "배우", fontSize = 14.sp, color = Color.Gray)
            Text(text = "제니퍼 러브 휴잇, 폴 니콜스 ... 더보기", fontSize = 14.sp, color = Color.Black)
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, backgroundColor: Color) {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(105.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, color = Color.White)
        }
    }
}