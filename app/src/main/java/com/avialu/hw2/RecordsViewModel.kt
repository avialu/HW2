package com.avialu.hw2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordsViewModel : ViewModel() {
    private val _selected = MutableLiveData<ScoreRecord?>()
    val selected: LiveData<ScoreRecord?> = _selected
    fun select(r: ScoreRecord) { _selected.value = r }
}
