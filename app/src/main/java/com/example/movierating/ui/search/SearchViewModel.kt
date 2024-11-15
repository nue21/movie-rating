package com.example.movierating.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class SearchViewModel:ViewModel() {
    private var sharedPreferences by mutableStateOf<SharedPreferences?>(null)

    fun setSharedPreferences (context: Context) {
        sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        loadSearchHistory()
    }

    // 검색어
    var searchInput by mutableStateOf("")
        private set

    // 이전 검색어
    private var _searchHistory = mutableStateOf<List<String>>(emptyList())
    val searchHistory: State<List<String>> = _searchHistory

    // 검색어 업데이트
    fun updateSearchInput(newString: String) {
        searchInput = newString
    }


    // JSON 문자열을 리스트로 변환하여 불러오는 함수
    private fun loadSearchHistory() {
        // local에 저장된 search history JSON 형식으로 가져오기
        val jsonString = sharedPreferences?.getString("search_history", null)

        if (jsonString != null) {
            val type = object : TypeToken<List<String>>() {}.type
            _searchHistory.value = Gson().fromJson(jsonString, type) // JSON 문자열을 리스트로 변환
        } else {
            _searchHistory.value = emptyList() // 저장된 데이터가 없으면 빈 리스트 반환
        }
    }

    // search history 리셋 빈배열로 만들기
    fun resetSearchHistory() {
        // sharedPreferences이 null이 아닐 경우만
        Log.d("resetSearchHistory", (sharedPreferences?:"null").toString())

        sharedPreferences?.let {
            saveSearchHistory(it, emptyList())
            it.getString("search_history", null)
            _searchHistory.value = emptyList()
        }
    }

    // JSON 문자열을 리스트로 변환하여 불러오는 함수
    fun updateSearchHistory() {
        val updatedHistory = _searchHistory.value.toMutableList()
        // 검색 기록에 있는 검색어를 검색했으면 지우고 맨앞에 추가
        if(searchInput in _searchHistory.value) updatedHistory.remove(searchInput)
        // 검색 기록이 21개 이상이면 가장 마지막 검색어를 지움
        if (updatedHistory.size > 20) updatedHistory.removeLast()
        // 새로운 검색어를 리스트 앞에 추가
        updatedHistory.add(0, searchInput)

        // sharedPreferences이 null이 아닐 경우만
        sharedPreferences?.let {
            saveSearchHistory(it, updatedHistory)
            val jsonString = it.getString("search_history", null)

            if (jsonString != null) {
                val type = object : TypeToken<List<String>>() {}.type
                _searchHistory.value = Gson().fromJson(jsonString, type) // JSON 문자열을 리스트로 변환
            } else {
                _searchHistory.value = emptyList() // 저장된 데이터가 없으면 빈 리스트 반환
            }
        }
    }

    // 리스트를 JSON으로 저장하는 함수
    private fun saveSearchHistory(sharedPreferences: SharedPreferences, searchHistory: List<String>) {
        val editor = sharedPreferences.edit()
        val jsonString = Gson().toJson(searchHistory) // 리스트를 JSON 문자열로 변환
        editor.putString("search_history", jsonString)
        editor.commit()
    }
}