package com.equationl.motortest.util


object PredefinedEffect {
    fun startPreEffect(index: Int) {
        when (index) {
            0 -> createHeartBeat()
            1 -> createOffBeat()
            2 -> createRock()
            3 -> createTossCoin()
        }
    }

    private fun createHeartBeat() {
        val timings = longArrayOf(150, 50, 150, 650)
        val amplitudes = intArrayOf(255, 0, 150, 0)
        VibratorHelper.instance.vibrate(timings, amplitudes, 0)
    }

    private fun createOffBeat() {
        val timings = longArrayOf(130,70,130,70,130,70,130,70,130,70,130)
        val amplitudes = intArrayOf(100,0,100,0,255,0,200,0,100,0,100)
        VibratorHelper.instance.vibrate(timings, amplitudes, 0)
    }

    private fun createRock() {
        val timings = longArrayOf(150,150,150,75,75,650)
        val amplitudes = intArrayOf(150,0,150,0,255,0)
        VibratorHelper.instance.vibrate(timings, amplitudes, 0)
    }

    private fun createTossCoin() {
        val timings = longArrayOf(10,180,10,90, 4,  90, 7, 80,2, 120,    4,50,2,40,1,40,     4,50,2,40,1,40,      4,50,2,40,1,40)
        val amplitudes = intArrayOf(255,0, 255,0, 240,0, 240,0, 240,0,    230,0,230,0,230,0,   220,0,220,0,220,0,   210,0,210,0,210,0)
        VibratorHelper.instance.vibrate(timings, amplitudes, -1)
    }
}