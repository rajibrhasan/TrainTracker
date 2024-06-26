package com.example.location.Repository

import androidx.annotation.WorkerThread
import com.example.location.Database.StationDao
import com.example.location.Model.Station
import kotlinx.coroutines.flow.Flow

class StationRepository(private val stationDao : StationDao) {
    val allStations: Flow<List<Station>> = stationDao.getAllStations()


    @WorkerThread
    suspend fun insert(station : Station){
        stationDao.insert(station)
    }

    @WorkerThread
    suspend fun update(station : Station){
        stationDao.update(station)
    }

    @WorkerThread
    suspend fun delete(station : Station){
        stationDao.delete(station)
    }
    @WorkerThread
    suspend fun deleteAllStations(){
        stationDao.deleteAllStations()
    }

    @WorkerThread
    fun getStation(name : String) : Station{
        return stationDao.getStation(name)
    }

}
