package com.equationl.motortest.compose.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equationl.motortest.R
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.compose.theme.DarkColors
import com.equationl.motortest.compose.theme.ErrorTextSize
import com.equationl.motortest.compose.theme.LightColors
import com.equationl.motortest.compose.util.AdvancedUtil
import com.equationl.motortest.constants.DataStoreKey
import com.equationl.motortest.settingDataStore
import com.equationl.motortest.util.PredefinedEffect
import com.equationl.motortest.util.Utils
import com.equationl.motortest.util.VibratorHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val TAG = "AdvancedScreen"

private var usingCustomPredefined by mutableStateOf(false)
private var usingHighAccuracy by mutableStateOf(false)
private var runBackground by mutableStateOf(false)
private var appPreSelectIndex by mutableStateOf(0)

private var amplitude = 255
private var rate = 50

private lateinit var viewMode: MyViewMode

//TODO 将还在使用的 view 组件迁移到 compose （例如AlertDialog）

@Composable
fun AdvancedScreen(isDarkTheme: Boolean = isSystemInDarkTheme(), onBack: () -> Unit) {
    viewMode = viewModel()
    val context: Context = LocalContext.current

    Utils.checkDevice(context, viewMode)

    if (viewMode.isBackFromVisual) {
        val timings = viewMode.visualTimings
        val amplitude = viewMode.visualAmplitude
        AdvancedUtil.diyClickSave(context, viewMode, true, timings, amplitude)
        viewMode.isBackFromVisual = false
    }

    LaunchedEffect(key1 = usingCustomPredefined, key2 = usingHighAccuracy, key3 = runBackground) {
        usingCustomPredefined = context.settingDataStore.data.map { it[DataStoreKey.usingCustomPredefined] ?: false }.first()
        usingHighAccuracy = context.settingDataStore.data.map { it[DataStoreKey.usingHighAccuracy] ?: false }.first()
        runBackground = context.settingDataStore.data.map { it[DataStoreKey.runBackground] ?: false }.first()
        viewMode.isRunOnBackground = runBackground
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
                    onValueChange = {
                        if (it.isDigitsOnly()) {
                            inputText = it
                        } },
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
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
    val amplitudeStopPoints = arrayOf(0, 23, 46, 69, 92, 115, 139, 162, 185, 208, 231, 255)
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
            var sliderPosition by remember { mutableStateOf(100f) }
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    if (!usingHighAccuracy && sliderPosition.toInt() in amplitudeStopPoints) {
                        amplitude = if (it.toInt() != 0) it.toInt() else 1
                        VibratorHelper.instance.vibrateOneShot(10, amplitude)
                        amplitudeIsOnStop = true
                    }
                    else {
                        amplitudeIsOnStop = false
                        if (usingHighAccuracy) {
                            amplitude = if (it.toInt() != 0) it.toInt() else 1
                            VibratorHelper.instance.vibrateOneShot(10, amplitude)
                        }
                    }
                },
                valueRange = 0f..255f,
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
            var sliderPosition by remember { mutableStateOf(50f) }
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    if (!usingHighAccuracy && sliderPosition.toInt() in rateStopPoints) {
                        rate = it.toInt()
                        VibratorHelper.instance.vibrateOneShot(10, amplitude)
                    }
                    else {
                        if (usingHighAccuracy) {
                            rate = it.toInt()
                            VibratorHelper.instance.vibrateOneShot(10, amplitude)
                        }
                    }
                },
                valueRange = 0f..100f,
                steps = if (usingHighAccuracy) 0 else 10,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
            Button(onClick = {
                val rate2 = (100 - rate).coerceAtLeast(1)
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
    val context: Context = LocalContext.current
    var showMoreMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp)) {
        Text(stringResource(R.string.advanced_text_diy))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewMode.timingsText,
                onValueChange = { viewMode.timingsText = it },
                label = { Text(stringResource(R.string.advanced_edit_diy_timings)) },
                trailingIcon = {
                    if (viewMode.timingsText.isNotEmpty()) {
                        IconButton(onClick = { viewMode.timingsText = "" }) {
                            Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                            )
                        }
                    }
                },
                isError = viewMode.timingsOnError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                    .onFocusChanged {
                        if (it.hasFocus) {
                            viewMode.setTimingsError("")
                        } /*else if (timingsText.isBlank()) {
                            viewMode.setTimingsError(context.getString(R.string.advanced_edit_diy_tip_text_empty))
                        }*/
                    }
            )
        }

        if (viewMode.timingsOnError) {
            Text(viewMode.timingsErrorText, color = Color.Red, fontSize = ErrorTextSize)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewMode.amplitudesText,
                onValueChange = { viewMode.amplitudesText = it },
                label = { Text(stringResource(R.string.advanced_edit_diy_amplitudes)) },
                trailingIcon = {
                    if (viewMode.amplitudesText.isNotEmpty()) {
                        IconButton(onClick = { viewMode.amplitudesText = "" }) {
                            Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                            )
                        }
                    }
                },
                isError = viewMode.amplitudesOnError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                    .onFocusChanged {
                        if (it.hasFocus) {
                            viewMode.setAmplitudesError("")
                        } /*else if (timingsText.isBlank()) {
                            viewMode.setAmplitudesError(context.getString(R.string.advanced_edit_diy_tip_text_empty))
                        }*/
                    }
            )
        }

        if (viewMode.amplitudesOnError) {
            Text(viewMode.amplitudesErrorText, color = Color.Red, fontSize = ErrorTextSize)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewMode.repeatText,
                onValueChange = { viewMode.repeatText = it },
                label = { Text(stringResource(R.string.advanced_edit_diy_repeat)) },
                trailingIcon = {
                    if (viewMode.repeatText.isNotEmpty()) {
                        IconButton(onClick = { viewMode.repeatText = "" }) {
                            Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                            )
                        }
                    }
                },
                isError = viewMode.repeatOnError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                    .onFocusChanged {
                        if (it.hasFocus) {
                            viewMode.setRepeatError("")
                        } /*else if (timingsText.isBlank()) {
                            viewMode.setRepeatError(context.getString(R.string.advanced_edit_diy_tip_text_empty))
                        }*/
                    }
            )
        }

        if (viewMode.repeatOnError) {
            Text(viewMode.repeatErrorText, color = Color.Red, fontSize = ErrorTextSize)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Button(onClick = {
                AdvancedUtil.clickDiyStart(context, viewMode)
            }) {
                Text(
                    stringResource(R.string.advanced_btn_diy_start),
                    modifier = Modifier.clickable {
                        AdvancedUtil.clickDiyStart(context, viewMode)
                    }
                )
                Box {
                    Icon(
                        painter = painterResource(R.drawable.ic_btn_menu_down),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                showMoreMenu = true
                            }
                    )

                    if (showMoreMenu) {
                        DiyMoreDropMenu(stringArrayResource(R.array.advanced_diy_more_menu), {showMoreMenu = false}) { index, _ ->
                            AdvancedUtil.onDiyMoreSelected(context, index, viewMode)
                        }
                    }
                }
            }

            OutlinedButton(onClick = {
                VibratorHelper.instance.cancel()
            }, modifier = Modifier.padding(start = 8.dp)) {
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
        OutlinedTextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { selectedOptionText = it },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
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
        AdvancedUtil.clickHelpBtn(context)
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

        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_action_run_background),
                defaultCheckState = runBackground) {
                runBackground = it
                viewMode.isRunOnBackground = it
                CoroutineScope(Dispatchers.IO).launch {
                    context.settingDataStore.edit { setting ->
                        setting[DataStoreKey.runBackground] = it
                    }
                }
                }
        }

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
        Text(text = text, modifier = Modifier.fillMaxWidth())
        Checkbox(
            checked = checkedState,
            onCheckedChange = null
        )
    }
}

@Composable
fun DiyMoreDropMenu(
    menuItems: Array<String>,
    onDismiss: () -> Unit,
    onClickCallback: (index: Int, item: String) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
            onDismiss.invoke() },
    ) {
        menuItems.forEachIndexed { index, item ->
            DropdownMenuItem(onClick = {
                onClickCallback.invoke(index, item)
                expanded = false
                onDismiss.invoke()
            }) {
                Text(text = item)
            }
        }
    }
}
