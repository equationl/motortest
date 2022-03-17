package com.equationl.motortest.compose.ui

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.equationl.motortest.R
import com.equationl.motortest.compose.theme.DarkColors
import com.equationl.motortest.compose.theme.LightColors

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
        ContentSystemPredefined(false)  // TODO 更改状态

    }

}

@Composable
private fun ContentSystemPredefined(isCustom: Boolean = false) {
    Column {
        Text(stringResource(R.string.advanced_text_predefined), modifier = Modifier.padding(start = 8.dp))
        if (isCustom) {
            Row {
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
        else {
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
                onCheckItem = {/*TODO 使用高精度*/})
        }

        Divider()

        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_action_run_background),
                onCheckItem = {/*TODO 后台振动*/})
        }

        Divider()

        DropdownMenuItem(onClick = {
            expanded = false
        }) {
            TopBarMenuItem(
                text = stringResource(R.string.advanced_action_customize_system_default),
                onCheckItem = {/*TODO 自定义系统预设*/})
        }
    }
}

@Composable
fun TopBarMenuItem(text: String, onCheckItem: (Boolean) -> Unit) {
    var checkedState by remember { mutableStateOf(false) }
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
