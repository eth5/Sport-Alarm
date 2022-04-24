package pro.it_dev.sportalarm.presentation.config

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pro.it_dev.sportalarm.R
import pro.it_dev.sportalarm.util.Resource

//todo Add soundPlayer by di for test play
@Composable
fun ConfigDialog(viewModel: ConfigViewModel = viewModel(), onDismissRequest: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth(1f)
                .border(1.dp, MaterialTheme.colors.secondary, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Center
            ) {
                var size by remember {
                    mutableStateOf(0f)
                }
                val animation by animateFloatAsState(
                    targetValue = size,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy
                    )
                )
                LaunchedEffect(Unit, block = { size = 1f })
                Column(
                    modifier = Modifier
                        .fillMaxSize(animation)
                        .background(
                            color = MaterialTheme.colors.background,
                            RoundedCornerShape(10.dp)
                        )
                        .shadow(0.dp, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = LocalContext.current.getString(R.string.config),
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .align(CenterHorizontally)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(1f)
                            .padding(start = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        ShowSettingContent(viewModel = viewModel)
                    }
                    ShowFooter(viewModel = viewModel, onDismissRequest = onDismissRequest)
                }

            }
        }

        SnackbarSimpleMessage(viewModel.popUpMsg, scaffoldState, scope, SnackbarDuration.Short)
    }

}


@Composable
private fun ShowSettingContent(viewModel: ConfigViewModel, ) {
    val clockState by produceState<Resource<Boolean>>(initialValue = Resource.Loading()) {
        value = viewModel.getClock()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center

    ) {
        when (clockState) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> ConfigSetting(viewModel)
            is Resource.Error -> Text(
                text = clockState.message ?: "Unknown error",
                color = Color.Red
            )
        }
    }
}

@Composable fun ShowFooter(viewModel: ConfigViewModel, onDismissRequest: () -> Unit){
    val clockState by produceState<Resource<Boolean>>(initialValue = Resource.Loading()) {
        value = viewModel.getClock()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        TextButton(
            onClick = {
                viewModel.saveClock()
                onDismissRequest()
            },
            enabled = clockState is Resource.Success,
            modifier = Modifier
                .weight(1f)
        ) {
            Text(text = LocalContext.current.getString(R.string.save))
        }
        TextButton(
            onClick = { onDismissRequest() },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = LocalContext.current.getString(R.string.cancel))
        }
    }
}

@Composable
fun SnackbarSimpleMessage(
    textState: MutableState<String?>,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    duration: SnackbarDuration
) {
    if (textState.value == null) return
    val text = textState.value!!
    LaunchedEffect(key1 = textState.value) {
        textState.value = null
        scope.launch {
            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            scaffoldState.snackbarHostState.showSnackbar(text, duration = duration)

        }
    }
}

@Composable
fun ConfigSetting(viewModel: ConfigViewModel) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val ctx = LocalContext.current
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp, vertical = 10.dp)
                .border(1.dp, MaterialTheme.colors.secondary, CircleShape)
                .shadow(2.dp, CircleShape)
                .background(color = MaterialTheme.colors.background, CircleShape),
            contentAlignment = Center
        ) {

            val volume by remember { viewModel.volume }
            Slider(
                value = volume,
                valueRange = 0f..1f,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                onValueChange = {
                    viewModel.setVolume(it)
                }

            )
        }
        EditField(
            stateValue = viewModel.laps,
            label = { Text(text = ctx.getString(R.string.laps)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onChangeSetter = {
                if (it.length < 9) viewModel.laps.value = viewModel.convertStringValueToInt(it)
            }
        )
        Text(text = LocalContext.current.getString(R.string.work_time))
        Row(verticalAlignment = Alignment.CenterVertically) {
            EditField(
                stateValue = viewModel.min,
                label = { Text(text = ctx.getString(R.string.min)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onChangeSetter = {
                    if (it.length < 9) viewModel.min.value = viewModel.convertStringValueToLong(it)
                }
            )
            Text(text = ":")
            EditField(
                stateValue = viewModel.sec,
                label = { Text(text = ctx.getString(R.string.sec)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onChangeSetter = {
                    if (it.length < 9) viewModel.sec.value = viewModel.convertStringValueToLong(it)
                }
            )
        }
        Text(text = LocalContext.current.getString(R.string.pause_time))
        Row(verticalAlignment = Alignment.CenterVertically) {
            EditField(
                stateValue = viewModel.pauseMin,
                label = { Text(text = ctx.getString(R.string.min)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onChangeSetter = {
                    if (it.length < 9) viewModel.pauseMin.value =
                        viewModel.convertStringValueToLong(it)
                }
            )
            Text(text = ":")
            EditField(
                stateValue = viewModel.pauseSec,
                label = { Text(text = ctx.getString(R.string.sec)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onChangeSetter = {
                    if (it.length < 9) viewModel.pauseSec.value =
                        viewModel.convertStringValueToLong(it)
                }
            )
        }

        LabeledCheckBox(
            value = viewModel.whistling,
            label = ctx.getString(R.string.whistling),
            modifier = Modifier
                .align(Start)
                .padding(10.dp),
            onChange = { viewModel.whistling.value = it })
        LabeledCheckBox(
            value = viewModel.voice,
            label = ctx.getString(R.string.voice),
            modifier = Modifier
                .align(Start)
                .padding(10.dp),
            onChange = { viewModel.voice.value = it })
        LabeledCheckBox(
            value = viewModel.beep,
            label = ctx.getString(R.string.beep),
            modifier = Modifier
                .align(Start)
                .padding(10.dp),
            onChange = { viewModel.beep.value = it })

    }
}

@Composable
fun LabeledCheckBox(
    value: State<Boolean>,
    label: String,
    modifier: Modifier = Modifier,
    onChange: (Boolean) -> Unit
) {
    Row(modifier = modifier) {
        val isChecked by remember { value }
        Checkbox(checked = isChecked, onCheckedChange = { onChange(it) })
        Text(text = label)
    }
}

@Composable
fun <T> EditField(
    stateValue: State<T>,
    modifier: Modifier = Modifier,
    adapter: (T) -> String = { it.toString() },
    label: @Composable (() -> Unit)? = null,
    fontSize: TextUnit = 18.sp,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onChangeSetter: (String) -> Unit
) {
    val stateValue by remember { stateValue }
    var borderSize by remember { mutableStateOf(1.dp) }

    TextField(
        value = adapter(stateValue),
        label = label,
        onValueChange = { onChangeSetter(it) },
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .border(borderSize, MaterialTheme.colors.secondary, CircleShape)
            .shadow(2.dp, CircleShape)
            .background(color = MaterialTheme.colors.background, CircleShape)
            .onFocusChanged {
                borderSize = if (it.isFocused) 3.dp else 1.dp
            },
        maxLines = maxLines,
        textStyle = TextStyle(
            fontSize = fontSize
        ),
        keyboardOptions = keyboardOptions
    )
}