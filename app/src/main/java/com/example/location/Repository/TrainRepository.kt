package com.example.location.Repository


import androidx.annotation.WorkerThread
import com.example.location.Database.TrainDAO
import com.example.location.Model.Train
import com.example.location.Model.TrainSuggestion
import kotlinx.coroutines.flow.Flow

class TrainRepository(private val trainDao : TrainDAO){
    val allTrains: Flow<List<Train>> = trainDao.getAllTrains()
    val allTrainNames : Flow<List<TrainSuggestion>> = trainDao.getTrainNames()

    @WorkerThread
    suspend fun insert(note : Train){
        trainDao.insert(note)
    }

    @WorkerThread
    suspend fun update(note : Train){
        trainDao.update(note)
    }

    @WorkerThread
    suspend fun delete(note : Train){
        trainDao.delete(note)
    }
    @WorkerThread
    suspend fun deleteAllTrains(){
        trainDao.deleteAllTrains()
    }

    @WorkerThread
    suspend fun getMyTrain(id : Int) : Train{
        return trainDao.getTrain(id)
    }
}

