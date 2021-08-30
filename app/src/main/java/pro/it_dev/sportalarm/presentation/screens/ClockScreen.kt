package pro.it_dev.sportalarm.presentation.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pro.it_dev.sportalarm.R
import pro.it_dev.sportalarm.presentation.config.ConfigDialog
import pro.it_dev.sportalarm.presentation.sound.Sound

@Composable
fun ClockScreen(sound: Sound, viewModel: ClockViewModel = viewModel()) {

	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		val clockState by remember { viewModel.clockState }

		Box (
			modifier = Modifier
				.weight(1f)
				.aspectRatio(1f),
			contentAlignment = Alignment.Center
		){
			AssetImage("clock.png", modifier = Modifier.fillMaxWidth())
			Box (
				modifier = Modifier
					.fillMaxHeight(0.7f)
					.aspectRatio(1f)
					.offset(x = 0.dp, (-7.5f).dp)
					.align(Alignment.BottomCenter),
				contentAlignment = Alignment.TopCenter
			){
//				Box(modifier = Modifier
//					.fillMaxSize(0.9f)
//					//.border(1.dp, Color.Red, CircleShape)
//					.background(Color.Red, CircleShape)
//					.align(Alignment.Center)
//					.offset(x = 200.dp, y = 160.dp)
//
//				) {
//
//				}
				Column(
					verticalArrangement = Arrangement.Top,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					val currentLap by viewModel.currenLap
					Text(
						text = "Lap $currentLap of ${viewModel.laps.value}",
						color = MaterialTheme.colors.primary,
						fontSize = 20.sp,
						modifier = Modifier.padding(top = 32.dp)
						)
					Spacer(modifier = Modifier.height(2.dp))

					val text = when(clockState){
						ClockState.Off -> "Off" to Color.Black
						ClockState.InRelax -> "Relax time!" to Color.Green
						ClockState.InRun -> "Work time!" to Color.Red
						ClockState.InPause -> "Pause!" to Color.Red
					}
					Text(
						text = text.first,
						fontWeight = FontWeight.Bold
						)

					val timeText by remember { viewModel.timeText }

					var multiplier by remember { mutableStateOf(1f) }
					var readyToDraw by remember { mutableStateOf(false) }
					Text(
						text = timeText,
						color = text.second,
						maxLines = 1,
						overflow = TextOverflow.Visible,
						modifier = Modifier
							.border(1.dp, Color.Black, RoundedCornerShape(10.dp))
							.fillMaxWidth(0.7f)
							.fillMaxHeight(0.4f)
							.padding(start = 12.dp, end = 12.dp)
							//.drawWithContent { if (readyToDraw) drawContent() }
						,
						textAlign = TextAlign.Center,
						style = LocalTextStyle.current.copy(
							fontSize = LocalTextStyle.current.fontSize * multiplier
						),
						onTextLayout = {
							if ( !it.hasVisualOverflow ) multiplier *= 1.1f
						}

					)
				}
			}

			Box(
				modifier = Modifier
					.fillMaxHeight(0.2f)
					.align(Alignment.BottomCenter)
					.padding(bottom = 16.dp),
				contentAlignment = Alignment.TopCenter
			) {
				TextButton(
					onClick = { viewModel.showConfig(true) }
					) {
					Text(text = LocalContext.current.getString(R.string.config).toUpperCase(Locale(Locale.current.toLanguageTag())))
				}
			}

		}
		ClockHandleBottomButtons(clockState = clockState, viewModel = viewModel)
	}

	val showConfig by remember { viewModel.showConfig }
	if (showConfig) ConfigDialog() {
		viewModel.showConfig(false)
		viewModel.resetClockViewModelValues()
	}

	val soundEvent by remember {
		viewModel.soundEvent
	}
	if (soundEvent != null){
		LaunchedEffect(key1 = soundEvent){
			soundEvent!!.list.forEach {
				sound.play(it, soundEvent!!.rate)
			}
		}
	}

}

@Composable
fun animationDp(start:Dp, target:Dp,animationSpec: AnimationSpec<Dp>): Dp {
	var size by remember {
		mutableStateOf(start)
	}
	val animation by animateDpAsState(
		targetValue = size,
		animationSpec = animationSpec
	)
	LaunchedEffect(key1 = target, animationSpec, block = {size = target})
	return animation
}

@Composable
fun ClockHandleBottomButtons(clockState: ClockState, viewModel: ClockViewModel) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(
				animationDp(start = 0.dp, target = 100.dp, animationSpec = spring(
					Spring.DampingRatioHighBouncy
				))
			)
			.padding(5.dp),
		verticalAlignment = Alignment.Top
	) {
		Button(
			modifier = Modifier
				.weight(1f)
				.padding(5.dp),
			onClick = {
				when(clockState){
					ClockState.Off -> viewModel.start()
					ClockState.InRun, ClockState.InRelax, ClockState.InPause -> viewModel.stop()
				}
			}
		) {
			Text(
				text = (when (clockState) {
					ClockState.Off -> LocalContext.current.getString(R.string.start)
					ClockState.InRelax, ClockState.InRun, ClockState.InPause -> LocalContext.current.getString(R.string.stop)
				}).toUpperCase(Locale(Locale.current.toLanguageTag()))
			)
		}
		Button(
			modifier = Modifier
				.weight(1f)
				.padding(5.dp),
			enabled = clockState == ClockState.InRun || clockState == ClockState.InRelax || clockState == ClockState.InPause,
			onClick = { viewModel.pause() }
		) {
			Text(text = LocalContext.current.getString(R.string.pause).toUpperCase(Locale(Locale.current.toLanguageTag())))
		}
	}
}

@Composable
fun AssetImage(pathToImage:String, modifier: Modifier) {
	val ctx = LocalContext.current
	val imageBitmap by rememberUpdatedState(
		newValue = ctx.assets.open(pathToImage)
			.use { BitmapFactory.decodeStream(it) }.asImageBitmap()
	)
	Image(
		bitmap = imageBitmap,
		contentDescription = null,
		modifier = modifier
			.aspectRatio(1f),
		contentScale = ContentScale.Fit
	)
}