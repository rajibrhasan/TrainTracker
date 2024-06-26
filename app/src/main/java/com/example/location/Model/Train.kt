package com.example.location.Model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.Serializable

@Entity(tableName="train_table")
data class Train( @PrimaryKey val id:Int, val name:String, val src:String, val dest:String, val off_day : String,
                  val total_duration:String, val leave_time : String, val arr_time: String, val total_distance : Double, val route: List<Route>,
                  val coords : List<Coordinate>):Serializable{}