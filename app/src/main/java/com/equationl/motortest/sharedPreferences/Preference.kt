package com.equationl.motortest.sharedPreferences

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by ${李伟} on 2017/8/29 0029.
 * ————————————————
 *版权声明：本文为CSDN博主「Android李伟」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
 *原文链接：https://blog.csdn.net/liwei123liwei123/article/details/77677998
 */
class Preference<T>(val context: Context, val string:String, val default : T) :
    ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(string, default)
    }

    val prefs by lazy{context.getSharedPreferences("Realnen",Context.MODE_PRIVATE)}

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(string, value)
    }

    private fun<A> putPreference(name:String,value:A)= with(prefs.edit()){
        when(value){//if语句 现在在kotlin中是表达式
            is Long -> putLong(name,value)
            is String -> putString(name,value)
            is Int -> putInt(name,value)
            is Boolean -> putBoolean(name,value)
            is Float -> putFloat(name,value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }
    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            //is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can not be saved")
        }
        res as U
    }
}