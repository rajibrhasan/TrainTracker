package com.example.location.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.location.Model.Station
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Insert
    suspend fun insert(st : Station)

    @Update
    suspend fun update(st : Station)

    @Delete
    suspend fun delete(st : Station)

    @Query("DELETE from station_table")
    suspend fun deleteAllStations()

    @Query("SELECT * from station_table ORDER BY name")
    fun getAllStations() : Flow<List<Station>>


    @Query("SELECT * from station_table WHERE name = :stName")
    fun getStation(stName : String): Station
}

