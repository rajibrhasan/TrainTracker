package com.example.location.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.location.Model.Station
import com.example.location.Repository.StationRepository
import kotlinx.coroutines.launch

class StationViewModel(private val repository: StationRepository) : ViewModel() {
    val myAllStations : LiveData<List<Station>> = repository.allStations.asLiveData()


    fun insert(st: Station) = viewModelScope.launch{
        repository.insert(st)
    }

    fun update(st: Station) = viewModelScope.launch{
        repository.update(st)
    }

    fun delete(st: Station) =  viewModelScope.launch{
        repository.delete(st)
    }

    fun deleteAllStations() = viewModelScope.launch {
        repository.deleteAllStations()
    }

    fun getAllTrains(): LiveData<List<Station>>? {
        return myAllStations
    }

    fun getMyStation(name:String):Station {

        return repository.getStation(name)
    }
}


class StationViewModelFactory(private val repository: StationRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

