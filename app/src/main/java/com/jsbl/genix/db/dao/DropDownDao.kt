package com.jsbl.genix.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jsbl.genix.model.DogBreed
import com.jsbl.genix.model.profileManagement.ResponseFillDropDown

/**
 * Created by Muhammad Ali on 13-May-20.
 * Email muhammad.ali9385@gmail.com
 */

@Dao
interface DropDownDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg dropDown: ResponseFillDropDown)

    @Query("SELECT * FROM ResponseFillDropDown")
    suspend fun getDropDownObj(): ResponseFillDropDown
}