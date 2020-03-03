package com.equationl.motortest.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * FileName: Vibrators
 * Author: equationl
 * Email: admin@likehide.com
 * Date: 2020/3/2 18:33
 * Description: 储存振动数据的表结构
 */

@Entity(tableName = "vibration_effects")
data class VibrationEffects (
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var timings: String,
    var amplitude: String,
    var repeate: Int,
    var name: String,
    @ColumnInfo(name = "create_time")
    var createTime: Long
    )