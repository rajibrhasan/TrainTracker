package com.example.location.ViewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.location.Model.Train
import com.example.location.Model.TrainSuggestion
import com.example.location.Repository.TrainRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

import kotlinx.coroutines.launch

class TrainViewModel(private val repository: TrainRepository):ViewModel() {
    val myAllTrains : LiveData<List<Train>> = repository.allTrains.asLiveData()
    val myAllTrainNames : LiveData<List<TrainSuggestion>> = repository.allTrainNames.asLiveData()

    fun insert(train: Train) = viewModelScope.launch{
        repository.insert(train)
    }

    fun update(note: Train) = viewModelScope.launch{
        repository.update(note)
    }

    fun delete(note: Train) =  viewModelScope.launch{
        repository.delete(note)
    }

    fun deleteAllTrains() = viewModelScope.launch {
        repository.deleteAllTrains()
    }

    fun getAllTrains(): LiveData<List<Train>>? {
        return myAllTrains
    }

    suspend fun getMyTrain(id:Int):Train? {

        val deferred: Deferred<Train> = viewModelScope.async {
            repository.getMyTrain(id)
        }
        return deferred.await()
    }


}

class TrainViewModelFactory(private val repository: TrainRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

