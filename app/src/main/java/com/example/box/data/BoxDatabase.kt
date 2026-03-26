package com.example.box.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.box.data.entities.Item
import com.example.box.data.entities.Origin
import com.example.box.data.entities.PlaceItem

@Database(entities = [(
    Item::class),
    (PlaceItem::class),
    (Origin::class)],
    version =1,
    exportSchema = false)
abstract class BoxDatabase: RoomDatabase() {

    abstract fun boxDao(): BoxDao

    companion object {
        private var INSTANCE: BoxDatabase? = null
        fun getInstance(context: Context): BoxDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context = context.applicationContext,
                        klass = BoxDatabase::class.java,
                        name = "box_database"
                    ).fallbackToDestructiveMigration(false)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}