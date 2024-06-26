package com.example.location.Database


import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.location.Model.Train
import com.example.location.Model.TrainSuggestion

import kotlinx.coroutines.flow.Flow

@Dao
interface TrainDAO {
    @Insert
    suspend fun insert(train : Train)

    @Update
    suspend fun update(note : Train)

    @Delete
    suspend fun delete(note: Train)

    @Query("DELETE from train_table")
    suspend fun deleteAllTrains()

    @Query("SELECT * from train_table")
    fun getAllTrains() : Flow<List<Train>>

    @Query("SELECT id , name , src, dest, off_day, leave_time, arr_time, total_duration, total_distance from train_table ORDER BY name")
    fun getTrainNames() : Flow<List<TrainSuggestion>>

    @Query("SELECT * from train_table WHERE id = :id")
    suspend fun getTrain(id : Int):Train


}
