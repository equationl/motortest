package com.equationl.motortest.database

import androidx.room.*


/**
 * FileName: VibratorsDao
 * Author: equationl
 * Email: admin@likehide.com
 * Date: 2020/3/2 20:32
 * Description: Dao interface of VibrationEffect
 */

@Dao
interface VibrationEffectsDao {
    @Query("select * from vibration_effects")
    fun getAll(): List<VibrationEffects>

    @Query("select * from vibration_effects where id LIKE :id LIMIT 1")
    fun queryById(id: Int): VibrationEffects

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: VibrationEffects):Long

    @Update
    fun update(item: VibrationEffects):Int

    @Delete
    fun delete(item: VibrationEffects):Int
}