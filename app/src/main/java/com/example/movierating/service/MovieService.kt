package com.example.movierating.service

import com.example.movierating.data.Movie
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File

class MovieService() {
    private val gson = Gson()
    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스

    // JSON 안 Result 속 영화 정보들 -> Movie 객체 List 로 변환
    fun parseMoviesFromJson(json: String): List<Movie> {
        val gson = Gson()
        val result = mutableListOf<Movie>()

        // JSON 파싱 시작
        val rootObject = gson.fromJson(json, JsonObject::class.java)
        val dataArray = rootObject.getAsJsonArray("Data")

        // Data 배열 내부 탐색
        dataArray.forEach { dataElement ->
            val dataObject = dataElement.asJsonObject
            val resultArray = dataObject.getAsJsonArray("Result")

            // Result 배열 내부 탐색
            resultArray.forEach { resultElement ->
                val resultObject = resultElement.asJsonObject

                // Movie 데이터 클래스에 필요한 정보 추출
                val docId = resultObject.get("DOCID").asString
                val title = resultObject.get("title").asString
                val nation = resultObject.get("nation").asString

                // directors 필드: 감독 이름만 추출
                val directors = resultObject.getAsJsonObject("directors")
                    .getAsJsonArray("director")
                    .map { it.asJsonObject.get("directorNm").asString }

                // actors 필드: 배우 이름만 추출
                val actors = resultObject.getAsJsonObject("actors")
                    .getAsJsonArray("actor")
                    .map { it.asJsonObject.get("actorNm").asString }

                // plots 필드: plotText만 추출
                val plots = resultObject.getAsJsonObject("plots")
                    .getAsJsonArray("plot")
                    .map { it.asJsonObject.get("plotText").asString }

                // 기타 필드
                val runtime = resultObject.get("runtime")?.asString
                val rating = resultObject.get("rating")?.asString
                val genre = resultObject.get("genre")?.asString
                val posters = resultObject.get("posters")?.asString

                // Movie 객체 생성 및 리스트에 추가
                result.add(
                    Movie(
                        DOCID = docId,
                        title = title,
                        nation = nation,
                        directors = directors,
                        actors = actors,
                        plots = plots,
                        runtime = runtime,
                        rating = rating,
                        genre = genre,
                        posters = posters
                    )
                )
            }
        }
        return result
    }
    // Firestore에 Movie 데이터 저장
    fun saveMoviesToFirestore(movies: List<Movie>) {
        movies.forEach { movie ->
            // Firestore에 컬렉션 생성 (movies) 및 Document 추가
            db.collection("movies").add(movie)
                .addOnSuccessListener { documentReference ->
                    println("DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    println("Error adding document: $e")
                }
        }
    }

    fun readFile(filePath: String): String {
        // 파일 경로로부터 텍스트를 읽어 String으로 반환
        val file = File(filePath)

        return if (file.exists()) {
            file.readText() // 파일의 모든 내용을 읽어서 String으로 반환
        } else {
            "File not found" // 파일이 존재하지 않으면 오류 메시지 반환
        }
    }

}