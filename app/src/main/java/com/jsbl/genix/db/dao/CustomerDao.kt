package com.jsbl.genix.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jsbl.genix.model.Customer
import com.jsbl.genix.model.DogBreed
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.model.registration.CustomerX

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg customer: CustomerX)

    @Query("SELECT * FROM CustomerX WHERE iD=:customerId")
    suspend fun getCustomer(customerId: Long): CustomerX?

    @Query("DELETE  FROM CustomerX ")
    suspend fun deleteAllCustomers()

    @Query("DELETE FROM CustomerX WHERE iD=:customerId")
    suspend fun deleteCustomer(customerId: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCustomer(vararg customer: CustomerX)



   /* @Delete
    fun deleteCarDetail(vararg postCarDetail: PostCarDetail)*/

  /*  @Update
    suspend fun update(customer: CustomerX)
*/
}