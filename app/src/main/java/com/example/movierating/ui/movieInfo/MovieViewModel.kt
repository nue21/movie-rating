package com.example.movierating.ui.movieInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movierating.data.Movie
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getRandomGenre(): Map<String, List<Movie>> {
        val moviesSnapshot = firestore.collection("movies").get().await()
        // 문서 ID를 각 Movie 객체에 할당
        val movies = moviesSnapshot.documents.mapNotNull { document ->
            document.toObject(Movie::class.java)?.copy(DOCID = document.id)
        }

        //각 영화의 첫번째 장르를 기준으로 그룹화
        val groupedMovies = movies.groupBy { it.genre?.split(",")?.firstOrNull() ?: "기타" }
        val randomGenres = groupedMovies.keys.shuffled().take(10) //랜덤으로 10개의 장르 선택

        // 장르당 20개의 랜덤 영화 선택
        return groupedMovies.filterKeys { it in randomGenres }
            .mapValues { (_, movieList) -> movieList.shuffled().take(20) }
    }
}

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {
    private val _moviesByGenre = MutableLiveData<Map<String, List<Movie>>>()
    val moviesByGenre: LiveData<Map<String, List<Movie>>> get() = _moviesByGenre

    init {
        loadMoviesByGenre()
    }

    private fun loadMoviesByGenre(){
        viewModelScope.launch {
            try {
                _moviesByGenre.value = repository.getRandomGenre()
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}