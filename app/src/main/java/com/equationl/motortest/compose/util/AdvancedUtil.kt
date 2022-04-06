package com.equationl.motortest.compose.util

import android.content.Context
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import com.equationl.motortest.R
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.database.DatabaseHelper
import com.equationl.motortest.database.VibrationEffects
import com.equationl.motortest.util.VibratorHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Composable
private fun NormalDialog(
    title: String,
    button: String,
    onDismissRequest: () -> Unit,
    onClickButton: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.subtitle1
            )

            content.invoke()

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClickButton,
                    border = BorderStroke(0.dp, Color.White)
                ) {
                    Text(button)
                }
            }
        }
    }
}

@Composable
fun HelpDialog(viewMode: MyViewMode) {
    NormalDialog(
        title = stringResource(R.string.advanced_fab_help_title),
        button = stringResource(R.string.advanced_fab_help_btn_close),
        onDismissRequest = {
            viewMode.openHelpDialog = false
        },
        onClickButton = {
            viewMode.openHelpDialog = false
        }) {
        HelpDialogContent()
    }
}

@Composable
private fun HelpDialogContent() {
    val showText = listOf(
        stringResource(R.string.advanced_dialog_help_content_summary),
        stringResource(R.string.advanced_dialog_help_content_usage),
        stringResource(R.string.advanced_dialog_help_content_about),
        stringResource(R.string.advanced_dialog_help_content_update)
    )
    val defaultShowText = stringResource(R.string.advanced_dialog_help_content_summary)

    var currentShowText by remember { mutableStateOf(defaultShowText) }

    var currentTabIndex by remember { mutableStateOf(0) }

    Column {
        Column(modifier = Modifier
            .heightIn(0.dp, 300.dp)
            .verticalScroll(rememberScrollState())) {
            HtmlText(currentShowText)
        }

        TabRow(
            selectedTabIndex = currentTabIndex,
            backgroundColor = Color.Transparent,
            divider = {},
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Tab(selected = currentTabIndex == 0, onClick = {
                currentTabIndex = 0
                currentShowText = showText[currentTabIndex]
            }) {
                Text(stringResource(R.string.advanced_dialog_tab_summary))
            }

            Tab(selected = currentTabIndex == 1, onClick = {
                currentTabIndex = 1
                currentShowText = showText[currentTabIndex]
            }) {
                Text(stringResource(R.string.advanced_dialog_tab_usage))
            }

            Tab(selected = currentTabIndex == 2, onClick = {
                currentTabIndex = 2
                currentShowText = showText[currentTabIndex]
            }) {
                Text(stringResource(R.string.advanced_dialog_tab_about))
            }

            Tab(selected = currentTabIndex == 3, onClick = {
                currentTabIndex = 3
                currentShowText = showText[currentTabIndex]
            }) {
                Text(stringResource(R.string.advanced_dialog_tab_update))
            }

        }
    }
}

@Composable
private fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = {
            it.text = HtmlCompat.fromHtml(html, FROM_HTML_MODE_COMPACT)
            it.movementMethod = LinkMovementMethod.getInstance()
            it.setTextColor(android.graphics.Color.parseColor("#000000"))
        }
    )
}

@Composable
fun EffectDialog(context: Context, viewMode: MyViewMode) {
    val databaseHelper = DatabaseHelper.getInstance(context)

    val saveList = remember { mutableStateListOf<VibrationEffects>() }

    NormalDialog(
        title = stringResource(R.string.advanced_diy_open_choise_title),
        button = stringResource(R.string.advanced_diy_open_choise_btn_close),
        onDismissRequest = {
            viewMode.openEffectDialog = false
        },
        onClickButton = {
            viewMode.openEffectDialog = false
        }) {
        Column(modifier = Modifier
            .height(300.dp)) {
            if (saveList.isEmpty()) {
                Row(modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.advanced_saveList_empty))
                }
            }

            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                for (data in saveList) {
                    item(key = data.id) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 点击 ITEM
                                viewMode.timingsText = data.timings
                                viewMode.amplitudesText = data.amplitude
                                viewMode.repeatText = data.repeate.toString()
                                viewMode.openEffectDialog = false
                            }) {
                            Column {
                                Text(data.name)
                                Text(SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.CHINA).format(data.createTime))
                            }
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_delete),
                                contentDescription = "delete",
                                modifier = Modifier.clickable {
                                    // 删除
                                    databaseHelper.delete(data)
                                    saveList.remove(data)
                            })
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = saveList) {
        saveList.clear()
        saveList.addAll(databaseHelper.getAll())
    }
}

@Composable
fun SaveDialog(viewMode: MyViewMode, scaffoldState: ScaffoldState) {
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current
    NormalDialog(
        title = stringResource(R.string.advanced_diy_save_fileName_title),
        button = stringResource(R.string.advanced_diy_save_fileName_btn_sure),
        onDismissRequest = {
            viewMode.openSaveDialog = false
        },
        onClickButton = {
            if (inputText.isBlank()) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.advanced_diy_save_fileName_isEmpty))
                }
            }
            else {
                val databaseHelper = DatabaseHelper.getInstance(context)
                databaseHelper.insert(VibrationEffects(null, viewMode.timings, viewMode.amplitude, viewMode.repeateI.toInt(), inputText, System.currentTimeMillis()))
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.advanced_diy_save_success))
                }
                viewMode.timings = ""
                viewMode.amplitude = ""
                viewMode.repeateI = ""
                viewMode.openSaveDialog = false
            }
        }) {
        OutlinedTextField(
            value = inputText,
            onValueChange = {inputText = it},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun ImportDialog(viewMode: MyViewMode, scaffoldState: ScaffoldState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }

    NormalDialog(
        title = stringResource(R.string.advanced_diy_import_dialog_title),
        button = stringResource(R.string.advanced_diy_import_dialog_btn_sure),
        onDismissRequest = {
            viewMode.openImportDialog = false
                           },
        onClickButton = {
            if (inputText.isBlank()) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.advanced_diy_import_text_isEmpty))
                }
            }
            else {
                try {
                    val text = String(Base64.decode(inputText, Base64.DEFAULT))
                    val array = JSONObject(text)
                    val timings = array.getString("timings")
                    val amplitude = array.getString("amplitude")
                    val repeate = array.getString("repeate")
                    viewMode.timingsText = timings
                    viewMode.amplitudesText = amplitude
                    viewMode.repeatText = repeate
                    viewMode.openImportDialog = false
                }
                catch (e: Exception) {
                    Log.e("el", "import fail:", e)
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.advanced_diy_import_fail))
                    }
                }
            }
        }) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp)
        )
    }
}

object AdvancedUtil {
    private const val TAG = "Advanced"

    fun clickDiyStart(context: Context, viewMode: MyViewMode) {
        viewMode.clearDiyInputError()

        val data = checkDiyData(context, viewMode) ?: return
        val timing = data[0]
        val amplitude = data[1]
        val repeateI = data[2]

        val timingsL = timing.split(",").map { it.toLong() }.toLongArray()
        val amplitudeI = amplitude.split(",").map { it.toInt() }.toIntArray()

        try {
            VibratorHelper.instance.vibrate(timingsL, amplitudeI, Integer.parseInt(repeateI))
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "error in try create vibrationEffect", e)
            viewMode.repeatOnError = true
            viewMode.repeatErrorText = context.getString(R.string.advanced_diy_start_fail, e.message)
            return
        }
    }

    private fun checkDiyData(context: Context, viewMode: MyViewMode): List<String>? {
        val timings = viewMode.timingsText
        val amplitudes = viewMode.amplitudesText
        val repeat = viewMode.repeatText

        if (timings.isBlank()) {
            viewMode.timingsOnError = true
            viewMode.timingsErrorText = context.getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }

        if (amplitudes.isBlank()) {
            viewMode.amplitudesOnError = true
            viewMode.amplitudesErrorText = context.getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }
        if (repeat.isBlank()) {
            viewMode.repeatOnError = true
            viewMode.repeatErrorText = context.getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }

        val timing = timings
            .replace(" ".toRegex(), "")
            .replace("\n".toRegex(), "")
        val timingsT = timing.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val amplitude = amplitudes
            .replace(" ".toRegex(), "")
            .replace("\n".toRegex(), "")
        val amplitudeT = amplitude.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val repeateI: Int
        try {
            repeateI = repeat.toInt()
        } catch (e: NumberFormatException) {
            viewMode.repeatOnError = true
            viewMode.repeatErrorText = context.getString(R.string.advanced_edit_diy_tip_number_error)
            return null
        }

        for (dataT in timingsT) {
            val data: Long
            try {
                data =dataT.toLong()
            } catch (e: NumberFormatException) {
                viewMode.timingsOnError = true
                viewMode.timingsErrorText = context.getString(R.string.advanced_edit_diy_tip_number_error)
                return null
            }
            if (data < 0) {
                viewMode.timingsOnError = true
                viewMode.timingsErrorText = context.getString(R.string.advanced_diy_check_timings_less_zero)
                return null
            }
        }

        for (dataT in amplitudeT) {
            val data: Int
            try {
                data =dataT.toInt()
            } catch (e: NumberFormatException) {
                viewMode.setAmplitudesError(context.getString(R.string.advanced_edit_diy_tip_number_error))
                return null
            }
            if (data < 0 || data > 255) {
                viewMode.setAmplitudesError(context.getString(R.string.advanced_diy_check_amplitude_number_error))
                return null
            }
        }

        if (amplitudeT.size != timingsT.size) {
            return if (amplitudeT.size > timingsT.size) {
                viewMode.setAmplitudesError(context.getString(R.string.advanced_diy_check_amp_bigThan_tim))
                null
            } else {
                viewMode.setTimingsError(context.getString(R.string.advanced_diy_check_tim_bigThan_amp))
                null
            }
        }

        if (repeateI >= amplitudeT.size) {
            viewMode.setRepeatError(context.getString(R.string.advanced_diy_check_reapeat_outOfIndex))
            return null
        }
        if (repeateI < -1) {
            viewMode.setRepeatError(context.getString(R.string.advanced_diy_check_repeat_wrong_index))
            return null
        }

        return listOf(timing, amplitude, repeateI.toString())
    }

    fun diyClickSave(context: Context, viewMode: MyViewMode, isFromVisualization: Boolean = false) {
        val timings: String
        val amplitude: String
        val repeateI: String

        if (isFromVisualization) {
            timings = viewMode.visualTimings
            amplitude = viewMode.visualAmplitude
            repeateI = "-1"

            viewMode.timingsText = timings
            viewMode.amplitudesText = amplitude
            viewMode.repeatText = repeateI
        }
        else {
            val data = checkDiyData(context, viewMode) ?: return

            timings = data[0]
            amplitude = data[1]
            repeateI = data[2]
        }

        viewMode.timings = timings
        viewMode.amplitude = amplitude
        viewMode.repeateI = repeateI
        viewMode.openSaveDialog = true
    }

    private fun diyClickShare(context: Context, viewMode: MyViewMode) {
        val data = checkDiyData(context, viewMode) ?: return

        val timings = data[0]
        val amplitude = data[1]
        val repeateI = data[2]

        var text = "{\"timings\": \"$timings\",\"amplitude\": \"$amplitude\",\"repeate\": \"$repeateI\"}"
        text = Base64.encodeToString(text.toByteArray(), Base64.DEFAULT)

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"
        context.startActivity(Intent.createChooser(intent, context.getText(R.string.app_name)))
    }

    fun onDiyMoreSelected(
        context: Context,
        index: Int,
        viewMode: MyViewMode
    ) {
        when (index) {
            0 -> {
                viewMode.visualAmplitude = "0"
                viewMode.visualTimings = "0"
                viewMode.currentPage = 2
            }
            1 -> {
                diyClickSave(context, viewMode)
            }
            2 -> {
                viewMode.openEffectDialog = true
                //diyClickOpen(context, viewMode)
            }
            3 -> {
                viewMode.openImportDialog = true
                //diyClickImport(context, viewMode)
            }
            4 -> {
                diyClickShare(context, viewMode)
            }
        }
    }
}