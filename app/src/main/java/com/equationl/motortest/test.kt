package com.equationl.motortest


/**
 * FileName: test
 * Author: equat
 * Date: 2020/3/1 23:30
 * Description: test
 */

fun main() {
    val timings = longArrayOf    (2, 3, 5, 5, 5, 5, 5, 5, 5, 5)
    val amplitudes = intArrayOf    (0, 1, 1, 1, 0, 1, 0, 1, 1, 0)
    val pattern = mutableListOf<Long>()

    var isCloseMotor = false
    var duration = 0L
    for (i in amplitudes.indices) {
        if ((amplitudes[i] > 0) == isCloseMotor) {
            duration += timings[i]
        }
        else {
            pattern.add(duration)
            isCloseMotor = amplitudes[i] > 0
            duration = timings[i]
        }
    }
    pattern.add(duration)

    println(pattern)
}