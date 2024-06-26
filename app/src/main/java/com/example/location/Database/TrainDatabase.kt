package com.example.location.Database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.location.Model.Station
import com.example.location.Model.Train
import com.example.location.Util.Converter
import com.example.location.Util.StationFiller
import com.example.location.Util.TrainFiller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Train::class,Station::class], version = 1)
@TypeConverters(Converter::class)
abstract class TrainDatabase : RoomDatabase(){
    abstract fun getTrainDao() : TrainDAO
    abstract fun getStationDao() : StationDao

    companion object{
        @Volatile
        private var INSTANCE : TrainDatabase? = null
        private var converter : Converter = Converter()

        fun getDatabase(context: Context, scope: CoroutineScope): TrainDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    TrainDatabase::class.java,"app_database")
                    .addCallback(NoteDatabaseCallback(context,scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class NoteDatabaseCallback(private val context: Context,private val scope : CoroutineScope) : RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database->
                    scope.launch {
                        val trainDao = database.getTrainDao()
                        val trainMaker  = TrainFiller(context)
                        val (trains, stops) = trainMaker.getTrains()
                        for(train in trains) trainDao.insert(train)

                        val stationDao = database.getStationDao()
                        val stMaker = StationFiller(context, stops)
                        val stations : List<Station> = stMaker.getStations()
                        for(st in stations) stationDao.insert(st)

                    }

                }
            }
        }
    }
}

