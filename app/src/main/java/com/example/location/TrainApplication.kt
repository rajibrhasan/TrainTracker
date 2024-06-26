package com.example.location


import android.app.Application
import com.example.location.Database.TrainDatabase
import com.example.location.Repository.StationRepository
import com.example.location.Repository.TrainRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TrainApplication : Application(){
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy{ TrainDatabase.getDatabase(this,applicationScope)}
    val repository by lazy { TrainRepository(database.getTrainDao()) }
    val stRepository by lazy { StationRepository(database.getStationDao()) }
}