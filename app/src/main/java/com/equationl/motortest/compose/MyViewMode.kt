package com.equationl.motortest.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MyViewMode: ViewModel() {
    var isRunOnBackground  = false

    var isFirstBoot = true

    var isSupportAdvanced = true

    var timings = ""
    var amplitude = ""
    var repeateI = ""

    var isVibrated by mutableStateOf(false)

    var currentPage by mutableStateOf(0)

    var timingsOnError by mutableStateOf(false)
    var amplitudesOnError by mutableStateOf(false)
    var repeatOnError by mutableStateOf(false)

    var timingsErrorText by mutableStateOf("")
    var amplitudesErrorText by mutableStateOf("")
    var repeatErrorText by mutableStateOf("")

    var timingsText by mutableStateOf("")
    var amplitudesText by mutableStateOf("")
    var repeatText by mutableStateOf("")

    var openHelpDialog by mutableStateOf(false)
    var openEffectDialog by mutableStateOf(false)
    var openSaveDialog by mutableStateOf(false)
    var openImportDialog by mutableStateOf(false)

    var visualTimings = "0"
    var visualAmplitude = "0"

    var isBackFromVisual by mutableStateOf(false)

    fun clearDiyInputError() {
        amplitudesErrorText = ""
        timingsErrorText = ""
        repeatErrorText = ""

        amplitudesOnError = false
        timingsOnError = false
        repeatOnError = false
    }

    fun setTimingsError(text: String) {
        if (text.isBlank()) {
            timingsOnError = false
        }
        else {
            timingsOnError = true
            timingsErrorText = text
        }
    }

    fun setAmplitudesError(text: String) {
        if (text.isBlank()) {
            amplitudesOnError = false
        }
        else {
            amplitudesOnError = true
            amplitudesErrorText = text
        }
    }

    fun setRepeatError(text: String) {
        if (text.isBlank()) {
            repeatOnError = false
        }
        else {
            repeatOnError = true
            repeatErrorText = text
        }
    }
}