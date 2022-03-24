package com.equationl.motortest.compose.ui

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.equationl.motortest.R
import com.equationl.motortest.compose.theme.DarkColors
import com.equationl.motortest.compose.theme.LightColors

var usingCustomPredefined by mutableStateOf(false)  // TODO 读取初始值
var usingHighAccuracy by mutableStateOf(false)      // TODO 读取初始值

@Composable
fun AdvancedScreen(isDarkTheme: Boolean = isSystemInDarkTheme()) {
    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors
    ) {
        Scaffold(
            topBar = {
                TopBar()
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
    //TODO 主页内容
    Column(modifier = Modifier.fillMaxSize()) {
        ContentSystemPredefined()
        ContentAppPredefined()
        ContentFreeTest()
        ContentDiy()
    }

}

@Composable
fun ContentSystemPredefined() {
    Column {
        Text(stringResource(R.string.advanced_text_predefined), modifier = Modifier.padding(start = 8.dp))
        if (usingCustomPredefined) {
            var text by remember { mutableStateOf("") }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.advanced_edit_system_customize)) },
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            IconButton(onClick = { text = "" }) {
                                Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                    contentDescription = stringResource(R.string.advanced_edit_systemCustomize_clr_description)
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )

                Button(onClick = {
                    /*TODO*/
                }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(stringResource(R.string.advanced_btn_system_customize_start))
                }
            }
        }
        else {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(stringResource(R.string.advanced_text_click))
                }
                Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(stringResource(R.string.advanced_text_doubleClick))
                }
                Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(stringResource(R.string.advanced_text_heavyClick))
                }
                Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(stringResource(R.string.advanced_text_tick))
                }
            }
        }
    }
}

@Composable
fun ContentAppPredefined() {
    Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.advanced_text_app_predefined))
            AppPreDropMenu()
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
            Button(onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.advanced_btn_pre_start))
            }
            OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(start = 8.dp)) {
                Text(stringResource(R.string.advanced_btn_pre_stop))
            }
        }
    }
}

@Composable
fun ContentFreeTest() {
    val amplitudeStopPoints = arrayOf(0, 23, 46, 69, 92, 115, 139, 162, 185, 208, 231, 225)
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
                        //TODO 拖动滑块， 振动
                        amplitudeIsOnStop = true
                    }
                    else {
                        amplitudeIsOnStop = false
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
                    if (!usingHighAccuracy && sliderPosition.toInt() in amplitudeStopPoints) {
                        //TODO 拖动滑块， 振动
                    }
                },
                valueRange = if (usingHighAccuracy) 0f..10f else 0f..100f,
                steps = if (usingHighAccuracy) 0 else 10,
                modifier = Modifier.weight(9f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
            Button(onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.advanced_btn_free_start))
            }
            OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(start = 8.dp)) {
                Text(stringResource(R.string.advanced_btn_free_stop))
            }
        }

    }
}

@Composable
fun ContentDiy() {
    // TODO diy

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
            }) {
                Text(
                    stringResource(R.string.advanced_btn_diy_start),
                    modifier = Modifier.clickable {
                        Log.i("test", "ContentDiy: 点击文字")
                    }
                )
                Icon(
                    painter = painterResource(R.drawable.ic_btn_menu_down),
                    contentDescription = "",
                    modifier = Modifier.padding(start = 8.dp).clickable {
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
    FloatingActionButton(onClick = { /*TODO 点击帮助*/ }) {
        Icon(painter = painterResource(android.R.drawable.ic_menu_help),
            contentDescription = stringResource(R.string.advanced_fab_description)
        )
    }
}

@Composable
private fun TopBar() {
    TopAppBar (
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = { /*TODO 返回*/ }) {
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
                onCheckItem =
                {
                    // TODO 使用高精度， 保存数值
                    usingHighAccuracy = it
                },
                usingHighAccuracy)
        }

        Divider()

        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_action_run_background),
                onCheckItem = {
                    // TODO 后台振动， 保存数值
                },
            false
            )  // TODO 读取数值
        }

        Divider()

        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_action_customize_system_default),
                onCheckItem = {
                    // TODO 自定义系统预设， 保存数值
                    usingCustomPredefined = it
                },
                usingCustomPredefined)
        }
    }
}

@Composable
fun TopBarMenuItem(text: String, onCheckItem: (Boolean) -> Unit, defaultCheckState: Boolean) {
    var checkedState by remember { mutableStateOf(defaultCheckState) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = {
                checkedState = it
                onCheckItem(it)
            }
        )
        Text(text = text)
    }
}


@Preview
@Composable
fun MyPreview() {
    AdvancedScreen()
}

/*
@Preview
@Composable
fun PreviewAdDark() {
    AdvancedScreen(true)
}*/
