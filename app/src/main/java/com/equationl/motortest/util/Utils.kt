package com.equationl.motortest.util

import android.os.Build
import android.text.Html
import android.text.Spanned


/**
 * FileName: Utils
 * Author: equation
 * Email: admin@likehide.com
 * Date: 2020/3/1 23:53
 * Description: 一些工具方法集合
 */
object Utils {
    var DIY_MODE_RUN = 1
    var DIY_MODE_SAVE = 2
    var DIY_MODE_OPEN = 3
    var DIY_MODE_SHARE = 4
    var DIY_MODE_IMPORT = 5

    fun text2html(text: String, flag: Int=16): Spanned {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            Html.fromHtml(text)
        } else {
            Html.fromHtml(text, flag)
        }
    }
}