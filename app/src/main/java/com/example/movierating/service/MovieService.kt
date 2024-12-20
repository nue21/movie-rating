package com.example.movierating.service

import android.content.Context
import android.util.Log
import com.example.movierating.data.Movie
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MovieService(private val context: Context) {  // Context를 생성자에서 받아옵니다.
    private val gson = Gson()
    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스

    // JSON 안 Result 속 영화 정보들 -> Movie 객체 List 로 변환
    fun parseMoviesFromJson(json: String): List<Movie> {
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
                val docId = resultObject.get("DOCID")?.asString ?: ""
                val title = resultObject.get("title")?.asString?.trim() ?: ""
                val nation = resultObject.get("nation")?.asString ?: ""

                // directors 필드: 감독 이름만 추출
                val directors = resultObject.getAsJsonObject("directors")
                    ?.getAsJsonArray("director")?.map {
                        it.asJsonObject.get("directorNm")?.asString ?: ""
                    } ?: emptyList() // directors가 없으면 빈 리스트

                // actors 필드: 배우 이름만 추출
                val actors = resultObject.getAsJsonObject("actors")
                    ?.getAsJsonArray("actor")?.map {
                        it.asJsonObject.get("actorNm")?.asString ?: ""
                    } ?: emptyList() // actors가 없으면 빈 리스트

                // plots 필드: plotText만 추출
                val plots = resultObject.getAsJsonObject("plots")
                    ?.getAsJsonArray("plot")?.map {
                        it.asJsonObject.get("plotText")?.asString ?: ""
                    } ?: emptyList() // plots가 없으면 빈 리스트

                // 기타 필드
                val runtime = resultObject.get("runtime")?.asString ?: ""
                val rating = resultObject.get("rating")?.asString ?: ""
                val genre = resultObject.get("genre")?.asString ?: ""

                // posters 필드: 첫 번째 URL만 추출
                val posters = resultObject.get("posters")?.asString
                    ?.split("|")?.firstOrNull() ?: ""

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
                        posters = posters,
                        year = "2020"
                    )
                )
            }
        }
        return result
    }

    fun saveMoviesToFirestore(movies: List<Movie>) {
        movies.forEach { movie ->
            // Movie 객체에서 Map으로 변환
            val formatedMovie = movie.toMap()

            // Firestore에 컬렉션 생성 (movies) 및 Document 추가
            db.collection("movies").document(movie.DOCID)
                .set(formatedMovie)
                .addOnSuccessListener {
                    Log.d("firestore upload success","DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.d("firestore upload fail","Error adding document: $e")
                }
        }
    }

    // assets에서 파일을 읽는 메서드
    fun readFileFromAssets(fileName: String): String {
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?

        try {
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader.close()
        }

        return stringBuilder.toString()
    }
}
