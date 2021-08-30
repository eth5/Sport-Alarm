package pro.it_dev.sportalarm.presentation.config

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
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
import pro.it_dev.sportalarm.R
import pro.it_dev.sportalarm.domain.Clock
import pro.it_dev.sportalarm.presentation.screens.animationDp
import pro.it_dev.sportalarm.util.Resource


@Composable
fun ConfigDialog(configViewModel: ConfigViewModel = viewModel(), onDismissRequest:()->Unit) {
	Dialog(
		onDismissRequest = onDismissRequest,
		properties = DialogProperties(dismissOnClickOutside = true)
	){
		Box(modifier = Modifier
			.fillMaxHeight(0.8f)
			.fillMaxWidth(1f),
			contentAlignment = Center
		){
			var size by remember {
				mutableStateOf(0f)
			}
			val animation by animateFloatAsState(
				targetValue = size,
				animationSpec = spring(
					dampingRatio = Spring.DampingRatioHighBouncy
				)
			)
			LaunchedEffect(key1 = "", block = {size=1f})
			Column(modifier = Modifier
				.fillMaxSize(animation)
				.background(color = MaterialTheme.colors.background, RoundedCornerShape(10.dp))
				.shadow(0.dp, RoundedCornerShape(10.dp))
			) {
				Text(
					text = "Config",
					color = MaterialTheme.colors.primary,
					fontWeight = FontWeight.Bold,
					fontSize = 20.sp,
					maxLines = 1,
					modifier = Modifier
						.padding(start = 5.dp)
						.align(CenterHorizontally)
				)
				val clockState by produceState<Resource<Clock>>(initialValue = Resource.Loading()){
					value = configViewModel.getClock()
				}
				Box(
					modifier = Modifier
						.weight(1f)
						.fillMaxWidth(1f)
						.padding(start = 16.dp, end = 16.dp),
					contentAlignment = Alignment.Center

				){
					when(clockState){
						is Resource.Loading -> CircularProgressIndicator()
						is Resource.Success -> ConfigSetting(clockState.data!!, configViewModel)
						is Resource.Error -> Text(text = clockState.message ?: "Unknown error", color = Color.Red)
					}
				}

				Row (modifier = Modifier
					.fillMaxWidth()
					.padding(10.dp)
				){
					TextButton(
						onClick = {
							configViewModel.saveClock(clockState.data!!)
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

		}

	}
}

@Composable
fun ConfigSetting(clock: Clock, configViewModel: ConfigViewModel) {
	Column(
		modifier = Modifier
			.verticalScroll(rememberScrollState())
			.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		EditField(
			value = clock.laps,
			label = { Text(text = "Laps") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			onChangeSetter = {
				clock.laps = configViewModel.convertStringValueToInt(it).coerceIn(1,1000)
				clock.laps
			}
		)
		Text(text = LocalContext.current.getString(R.string.work_time))
		Row (verticalAlignment = Alignment.CenterVertically){
			EditField(
				value = clock.min,
				label = { Text(text = "Min") },
				modifier = Modifier.weight(1f),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				onChangeSetter = {
					clock.min = configViewModel.convertStringValueToInt(it).coerceIn(0,59)
					clock.min
				}
			)
			Text(text = ":")
			EditField(
				value = clock.sec,
				label = { Text(text = "Sec") },
				modifier = Modifier.weight(1f),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				onChangeSetter = {
					clock.sec = configViewModel.convertStringValueToInt(it).coerceIn(0,59)
					clock.sec
				}
			)
		}
		Text(text = LocalContext.current.getString(R.string.pause_time))
		Row (verticalAlignment = Alignment.CenterVertically){
			EditField(
				value = clock.pauseMin,
				label = { Text(text = "Min") },
				modifier = Modifier.weight(1f),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				onChangeSetter = {
					clock.pauseMin = configViewModel.convertStringValueToInt(it).coerceIn(0,59)
					clock.pauseMin
				}
			)
			Text(text = ":")
			EditField(
				value = clock.pauseSec,
				label = { Text(text = "Sec") },
				modifier = Modifier.weight(1f),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				onChangeSetter = {
					clock.pauseSec = configViewModel.convertStringValueToInt(it).coerceIn(0,59)
					clock.pauseSec
				}
			)
		}
		LabeledCheckBox(
			value = clock.whistling,
			label = "Whistling",
			modifier = Modifier
				.align(Start)
				.padding(10.dp),
			onChange = { clock.whistling = it; it })
		LabeledCheckBox(
			value = clock.voice,
			label = "Voice",
			modifier = Modifier
				.align(Start)
				.padding(10.dp),
			onChange = { clock.voice = it; it })

	}
}

@Composable
fun LabeledCheckBox(value:Boolean, label:String, modifier: Modifier = Modifier, onChange:(Boolean)->Boolean) {
	Row(modifier = modifier) {
		var isChecked by remember { mutableStateOf(value) }
		Checkbox(checked = isChecked, onCheckedChange = { isChecked = onChange(it) })
		Text(text = label)
	}
}

@Composable
fun <T>EditField(
	value:T,
	adapter:(T)->String = { it.toString() },
	label: @Composable (() -> Unit)? = null,
	modifier: Modifier = Modifier,
	fontSize: TextUnit = 18.sp,
	maxLines:Int = 1,
	keyboardOptions: KeyboardOptions = KeyboardOptions (),
	onChangeSetter: (String)->T
){
	var stateValue by remember { mutableStateOf(value) }
	var size by remember { mutableStateOf(0.8f) }
	var borderSize by remember { mutableStateOf(1.dp) }

	TextField(
		value = adapter(stateValue),
		label = label,
		onValueChange = { stateValue = onChangeSetter(it) },
		modifier = modifier
			.padding(horizontal = 2.dp, vertical = 2.dp)
			//.fillMaxWidth(size)
			.border(borderSize, MaterialTheme.colors.secondary, CircleShape)
			.shadow(2.dp, CircleShape)
			.background(color = Color.White, CircleShape)
			.onFocusChanged {
				if (it.isFocused) {
					size = 0.9f
					borderSize = 3.dp
				} else {
					size = 0.8f
					borderSize = 1.dp
				}
			}
		,
		maxLines = maxLines,
		textStyle = TextStyle(
			fontSize = fontSize
		),
		keyboardOptions = keyboardOptions
	)
}