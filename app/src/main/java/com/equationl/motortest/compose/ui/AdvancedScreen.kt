package com.equationl.motortest.compose.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.equationl.motortest.R
import com.equationl.motortest.compose.theme.DarkColors
import com.equationl.motortest.compose.theme.LightColors
import com.equationl.motortest.constants.DataStoreKey
import com.equationl.motortest.settingDataStore
import com.equationl.motortest.util.PredefinedEffect
import com.equationl.motortest.util.Utils
import com.equationl.motortest.util.VibratorHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dialog_advanced_help.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

var usingCustomPredefined by mutableStateOf(false)
var usingHighAccuracy by mutableStateOf(false)
var runBackground by mutableStateOf(false)
var appPreSelectIndex by mutableStateOf(0)

var amplitude = 255
var rate = 50

//TODO checkDevice()

@Composable
fun AdvancedScreen(isDarkTheme: Boolean = isSystemInDarkTheme(), onBack: () -> Unit) {
    val context: Context = LocalContext.current

    LaunchedEffect(key1 = usingCustomPredefined, key2 = usingHighAccuracy, key3 = runBackground) {
        usingCustomPredefined = context.settingDataStore.data.map { it[DataStoreKey.usingCustomPredefined] ?: false }.first()
        usingHighAccuracy = context.settingDataStore.data.map { it[DataStoreKey.usingHighAccuracy] ?: false }.first()
        runBackground = context.settingDataStore.data.map { it[DataStoreKey.runBackground] ?: false }.first()
    }

    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors
    ) {
        Scaffold(
            topBar = {
                TopBar(onBack)
            },
            floatingActionButton = {
                FloatButton()
            }
        ) {
            Box(modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())) {
                Content()
            }
        }
    }
}

@Composable
private fun Content() {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentSystemPredefined()
        ContentAppPredefined()
        ContentFreeTest()
        ContentDiy()
    }
}

@SuppressLint("InlinedApi")
@Composable
fun ContentSystemPredefined() {
    val context = LocalContext.current
    var isUsingAble = true
    if (Build.VERSION.SDK_INT < 29) {
        isUsingAble = false
    }

    Column {
        Text(
            stringResource(if (isUsingAble) R.string.advanced_text_predefined else R.string.advanced_text_predefined_notSupport),
            modifier = Modifier.padding(start = 8.dp),
            color = if (isUsingAble) Color.Black else Color.Red
        )
        if (usingCustomPredefined) {
            var inputText by remember { mutableStateOf("") }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text(stringResource(R.string.advanced_edit_system_customize)) },
                    trailingIcon = {
                        if (inputText.isNotEmpty()) {
                            IconButton(onClick = { inputText = "" }) {
                                Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                    contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )

                Button(onClick = {
                    try {
                        VibratorHelper.instance.vibratePredefined(inputText.toInt())
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(context, R.string.advanced_toast_systemCustomize_notSupportVlue, Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(stringResource(R.string.advanced_btn_system_customize_start))
                }
            }
        }
        else {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    VibratorHelper.instance.vibratePredefined(VibrationEffect.EFFECT_CLICK)
                },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = isUsingAble) {
                    Text(stringResource(R.string.advanced_text_click))
                }
                Button(onClick = {
                    VibratorHelper.instance.vibratePredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = isUsingAble) {
                    Text(stringResource(R.string.advanced_text_doubleClick))
                }
                Button(onClick = {
                    VibratorHelper.instance.vibratePredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = isUsingAble) {
                    Text(stringResource(R.string.advanced_text_heavyClick))
                }
                Button(onClick = {
                    VibratorHelper.instance.vibratePredefined(VibrationEffect.EFFECT_TICK)
                },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = isUsingAble) {
                    Text(stringResource(R.string.advanced_text_tick))
                }
            }
        }
    }
}

@Composable
fun ContentAppPredefined() {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.advanced_text_app_predefined))
            AppPreDropMenu()
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
            Button(onClick = {
                if (appPreSelectIndex == 4) {
                    Toast.makeText(context, R.string.advanced_btn_pre_wait, Toast.LENGTH_LONG).show()
                }
                else {
                    PredefinedEffect.startPreEffect(appPreSelectIndex)
                }
            }) {
                Text(stringResource(R.string.advanced_btn_pre_start))
            }
            OutlinedButton(onClick = {
                VibratorHelper.instance.cancel()
            }, modifier = Modifier.padding(start = 8.dp)) {
                Text(stringResource(R.string.advanced_btn_pre_stop))
            }
        }
    }
}

@Composable
fun ContentFreeTest() {
    val amplitudeStopPoints = arrayOf(0, 23, 46, 69, 92, 115, 139, 162, 185, 208, 231, 225)
    val rateStopPoints = arrayOf(0, 9, 18, 27, 36, 45, 54, 63, 72, 81, 90, 100)
    var amplitudeIsOnStop by remember { mutableStateOf(false) }
    val amplitudeTextScale: Float by animateFloatAsState(
        targetValue = if (amplitudeIsOnStop) 1f else 0.8f,
        animationSpec = spring()
    )

    Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp)) {
        Text(stringResource(R.string.advanced_text_free_test))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.advanced_text_amplitud))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            var sliderPosition by remember { mutableStateOf(if (usingHighAccuracy) 10f else 100f) }
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    if (!usingHighAccuracy && sliderPosition.toInt() in amplitudeStopPoints) {
                        amplitude = if (it.toInt() != 0) it.toInt() else 1
                        VibratorHelper.instance.vibrateOneShot(1, amplitude)
                        amplitudeIsOnStop = true
                    }
                    else {
                        amplitudeIsOnStop = false
                        if (usingHighAccuracy) {
                            amplitude = if (it.toInt() != 0) it.toInt()*10 else 10
                            VibratorHelper.instance.vibrateOneShot(10, amplitude)
                        }
                    }
                },
                valueRange = if (usingHighAccuracy) 0f..25f else 0f..255f,
                steps = if (usingHighAccuracy) 0 else 10,
                modifier = Modifier.weight(9f)
            )

            Text(sliderPosition.toInt().toString(), modifier = Modifier
                .weight(1f)
                .scale(amplitudeTextScale))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.advanced_text_rate))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            var sliderPosition by remember { mutableStateOf(if (usingHighAccuracy) 5f else 50f) }
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    if (!usingHighAccuracy && sliderPosition.toInt() in rateStopPoints) {
                        rate = it.toInt()
                        VibratorHelper.instance.vibrateOneShot(1, amplitude)
                    }
                    else {
                        if (usingHighAccuracy) {
                            rate = it.toInt() * 10
                            VibratorHelper.instance.vibrateOneShot(10, amplitude)
                        }
                    }
                },
                valueRange = if (usingHighAccuracy) 0f..10f else 0f..100f,
                steps = if (usingHighAccuracy) 0 else 10,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
            Button(onClick = {
                var rate2 = 100 - rate
                rate2 = if (rate2 < 1) 1 else rate2
                val timings = longArrayOf(rate2 * 10.toLong(), rate2.toLong())
                if (rate2 == 100) {
                    timings[0] = 3_600_000
                    timings[1] = 3_600_000
                }
                val amplitudes = intArrayOf(amplitude, 0)
                VibratorHelper.instance.vibrate(timings, amplitudes, 0)
            }) {
                Text(stringResource(R.string.advanced_btn_free_start))
            }
            OutlinedButton(onClick = {
                VibratorHelper.instance.cancel()
            }, modifier = Modifier.padding(start = 8.dp)) {
                Text(stringResource(R.string.advanced_btn_free_stop))
            }
        }

    }
}

@Composable
fun ContentDiy() {
    Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp)) {
        Text(stringResource(R.string.advanced_text_diy))
        var timingsText by remember { mutableStateOf("") }
        var amplitudesText by remember { mutableStateOf("") }
        var repeatText by remember { mutableStateOf("") }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = timingsText,
                onValueChange = { timingsText = it },
                label = { Text(stringResource(R.string.advanced_edit_diy_timings)) },
                trailingIcon = {
                    if (timingsText.isNotEmpty()) {
                        IconButton(onClick = { timingsText = "" }) {
                            Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = amplitudesText,
                onValueChange = { amplitudesText = it },
                label = { Text(stringResource(R.string.advanced_edit_diy_amplitudes)) },
                trailingIcon = {
                    if (amplitudesText.isNotEmpty()) {
                        IconButton(onClick = { timingsText = "" }) {
                            Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = repeatText,
                onValueChange = { repeatText = it },
                label = { Text(stringResource(R.string.advanced_edit_diy_repeat)) },
                trailingIcon = {
                    if (repeatText.isNotEmpty()) {
                        IconButton(onClick = { repeatText = "" }) {
                            Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Button(onClick = {
                Log.i("test", "ContentDiy: 点击按钮")
                //TODO
            }) {
                Text(
                    stringResource(R.string.advanced_btn_diy_start),
                    modifier = Modifier.clickable {
                        //TODO
                        Log.i("test", "ContentDiy: 点击文字")
                    }
                )
                Icon(
                    painter = painterResource(R.drawable.ic_btn_menu_down),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            //TODO
                            Log.i("test", "ContentDiy: 点击图标")
                        }
                )
            }

            OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(start = 8.dp)) {
                Text(stringResource(R.string.advanced_btn_diy_stop))
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppPreDropMenu() {
    val options = stringArrayResource(R.array.advanced_spinner_pre)
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField( // TODO 更改样式
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { selectedOptionText = it },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEachIndexed { index, selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        appPreSelectIndex = index
                        /*TODO 更改预设模式*/
                    },
                    enabled = options.lastIndex != index
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Composable
private fun FloatButton() {
    val context = LocalContext.current
    FloatingActionButton(onClick = {
        clickHelpBtn(context)
    }) {
        Icon(painter = painterResource(android.R.drawable.ic_menu_help),
            contentDescription = stringResource(R.string.advanced_fab_description)
        )
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    TopAppBar (
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        },
        actions = {
            TopBarDropdownMenu()
        }
    )
}

@Composable
private fun TopBarDropdownMenu() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "More Menu"
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_menu_high_accuracy),
                defaultCheckState = usingHighAccuracy) {
                usingHighAccuracy = it
                CoroutineScope(Dispatchers.IO).launch {
                    context.settingDataStore.edit { setting ->
                        setting[DataStoreKey.usingHighAccuracy] = it
                    }
                }
            }
        }

        Divider()

        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_action_run_background),
                defaultCheckState = runBackground) {
                runBackground = it
                CoroutineScope(Dispatchers.IO).launch {
                    context.settingDataStore.edit { setting ->
                        setting[DataStoreKey.runBackground] = it
                    }
                }
                }
        }

        Divider()

        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_action_customize_system_default),
                defaultCheckState = usingCustomPredefined) {
                usingCustomPredefined = it
                CoroutineScope(Dispatchers.IO).launch {
                    context.settingDataStore.edit { setting ->
                        setting[DataStoreKey.usingCustomPredefined] = it
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarMenuItem(text: String, defaultCheckState: Boolean, onCheckItem: (Boolean) -> Unit) {
    var checkedState by remember { mutableStateOf(defaultCheckState) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                checkedState = !checkedState
                onCheckItem(checkedState)
            }
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = null
        )
        Text(text = text)
    }
}

private fun clickHelpBtn(context: Context) {
    val layout = View.inflate(context, R.layout.dialog_advanced_help, null)
    MaterialAlertDialogBuilder(context)
        .setTitle(R.string.advanced_fab_help_title)
        .setPositiveButton(R.string.advanced_fab_help_btn_close, null)
        .setView(layout)
        .show()
    layout.advanced_dialog_text_content.movementMethod = LinkMovementMethod.getInstance()
    layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(R.string.advanced_dialog_help_content_summary))
    layout.advanced_dialog_tab_layout.addOnTabSelectedListener(object :
        TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            when (tab.position) {
                0 -> {
                    layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(R.string.advanced_dialog_help_content_summary))
                }
                1 -> {
                    layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(R.string.advanced_dialog_help_content_usage))
                }
                2 -> {
                    layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(R.string.advanced_dialog_help_content_about))
                }
                3 -> {
                    layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(R.string.advanced_dialog_help_content_update))
                }
            }
        }

        override fun onTabReselected(tab: TabLayout.Tab) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
    })
}


@Preview
@Composable
fun MyPreview() {
    AdvancedScreen {}
}

/*
@Preview
@Composable
fun PreviewAdDark() {
    AdvancedScreen(true)
}*/
