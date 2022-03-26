package com.equationl.motortest.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MyViewMode: ViewModel() {
    var currentPage by mutableStateOf(0)
}