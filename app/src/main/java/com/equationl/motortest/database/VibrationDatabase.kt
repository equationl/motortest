package com.equationl.motortest.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * FileName: VibrationDatabase
 * Author: equationl
 * Email: admin@likehide.com
 * Date: 2020/3/2 21:01
 * Description: Database of vibrationEffects
 */

@Database(entities = [(VibrationEffects::class)], version = 1)
abstract class VibrationDatabase : RoomDatabase() {
    abstract fun getVibratorEffectsDao() : VibrationEffectsDao

}