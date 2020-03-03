package com.equationl.motortest.database

import android.content.Context
import androidx.room.Room


/**
 * FileName: DatabaseHelper
 * Author: equationl
 * Email: admin@likehide.com
 * Date: 2020/3/2 21:43
 * Description: 用于创建单例模式
 */
class DatabaseHelper constructor(context: Context) {
    val dataBase = Room.databaseBuilder(context, VibrationDatabase::class.java, "effects")
        .allowMainThreadQueries()
        .build()

    companion object {
        @Volatile
        var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            if (INSTANCE == null) {
                synchronized(DatabaseHelper::class) {
                    if (INSTANCE == null) {
                        INSTANCE = DatabaseHelper(context.applicationContext)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    fun getAll(): List<VibrationEffects> {
        return dataBase.getVibratorEffectsDao().getAll()
    }

    fun queryById(id: Int): VibrationEffects {
        return dataBase.getVibratorEffectsDao().queryById(id)
    }

    fun insert(item: VibrationEffects):Long {
        return dataBase.getVibratorEffectsDao().insert(item)
    }

    fun update(item: VibrationEffects):Int {
        return dataBase.getVibratorEffectsDao().update(item)
    }

    fun delete(item: VibrationEffects):Int {
        return dataBase.getVibratorEffectsDao().delete(item)
    }
}