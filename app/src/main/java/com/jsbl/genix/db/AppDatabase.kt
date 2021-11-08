package com.jsbl.genix.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.DogBreed
import com.jsbl.genix.db.dao.DogDao
import com.jsbl.genix.db.dao.DropDownDao
import com.jsbl.genix.model.profileManagement.ResponseFillDropDown
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.typeConverter.*


@Database(entities = [CustomerX::class, ResponseFillDropDown::class], version = 36)
@TypeConverters(
    CarDetailsTypeConverter::class,
    FeedbackTypeConverter::class,
    InterestTypeConverter::class,
    InterestXTypeConverter::class,
    DropDownTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun dropDownDao(): DropDownDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private var lock = Any()


        operator fun invoke(context: Context) = instance
            ?: synchronized(lock) {
                instance
                    ?: buildDatabase(context)
                        .also {
                            instance = it
                        }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "appDatabase"
            )
                .fallbackToDestructiveMigration().build()
    }
}