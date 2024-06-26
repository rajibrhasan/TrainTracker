package com.example.location.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "station_table")
data class Station(@PrimaryKey val name: String, val lat:Double, val lng:Double) : Serializable{
}