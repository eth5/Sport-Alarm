package pro.it_dev.sportalarm.presentation.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

		Box(
			modifier = Modifier
				.weight(1f)
				.aspectRatio(1f),
			contentAlignment = Alignment.Center
		) {
			AssetImage("clock.png", modifier = Modifier.fillMaxWidth())
			Box(
				modifier = Modifier
					.fillMaxHeight(0.74f)
					.aspectRatio(1f)
					.offset(x = 0.dp, (-7.5f).dp)
					.align(Alignment.BottomCenter),
				contentAlignment = Alignment.Center
			) {
				Column(
					modifier = Modifier,
					verticalArrangement = Arrangement.Top,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					val currentLap by viewModel.currenLap
					Text(
						text = "Lap $currentLap of ${viewModel.laps.value}",
						color = MaterialTheme.colors.primary,
						fontSize = 20.sp,
						fontWeight = FontWeight.Bold,
						modifier = Modifier.padding(top = 8.dp)
					)

					val text = when (clockState) {
						ClockState.Off -> "Off" to Color.Black
						ClockState.InRelax -> "Relax time!" to Color.Green
						ClockState.InRun -> "Work time!" to Color.Red
						ClockState.InPause -> "Pause!" to Color.Red
					}
					Text(
						text = text.first,
						fontWeight = FontWeight.Bold,
						color = Color.Black
					)

					val timeText by remember { viewModel.timeText }
					var multiplier by remember { mutableStateOf(5f) }
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
							.drawWithContent { if (readyToDraw) drawContent() },
						textAlign = TextAlign.Center,
						style = LocalTextStyle.current.copy(
							fontSize = LocalTextStyle.current.fontSize * multiplier
						),
						onTextLayout = {
							if (it.hasVisualOverflow) multiplier *= 0.99f
							else readyToDraw = true
						}

					)
					IconButton(onClick = { viewModel.showConfig(true) }) {
						Icon(
							imageVector = Icons.Default.Alarm,
							contentDescription = "Config",
							tint = Color.Black,
							modifier = Modifier
								.size(40.dp)
							//.align(Alignment.BottomCenter)
						)
					}
				}
			}
		}
		ClockHandleBottomButtons(clockState = clockState, viewModel = viewModel)
	}

	val showConfig by remember { viewModel.showConfig }
	if (showConfig) ConfigDialog {
		viewModel.showConfig(false)
		viewModel.resetClockViewModelValues()
	}

	val soundEvent by remember {
		viewModel.soundEvent
	}
	if (soundEvent != null) {
		LaunchedEffect(key1 = soundEvent) {
			soundEvent!!.list.forEach {
				if (it.enable) sound.play(it.path, soundEvent!!.rate)
			}
		}
	}

}

@Composable
fun animationDp(start: Dp, target: Dp, animationSpec: AnimationSpec<Dp>): Dp {
	var size by remember {
		mutableStateOf(start)
	}
	val animation by animateDpAsState(
		targetValue = size,
		animationSpec = animationSpec
	)
	LaunchedEffect(key1 = target, animationSpec, block = { size = target })
	return animation
}

@Composable
fun ClockHandleBottomButtons(clockState: ClockState, viewModel: ClockViewModel) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			//.background(MaterialTheme.colors.surface)
			.height(
				animationDp(
					start = 0.dp, target = 100.dp, animationSpec = spring(
						Spring.DampingRatioHighBouncy
					)
				)
			)
			.padding(5.dp),
		verticalAlignment = Alignment.Top
	) {

		IconButton(
			onClick = { viewModel.start() },
			enabled = clockState == ClockState.Off,
			modifier = Modifier
				.weight(1f).padding(5.dp)
				.background(MaterialTheme.colors.surface, CircleShape)
		) {
			Icon(
				imageVector = Icons.Default.PlayArrow,
				contentDescription = "Play"
			)
		}
		IconButton(
			onClick = { viewModel.pause() },
			enabled = clockState != ClockState.Off,
			modifier = Modifier
				.weight(1f).padding(5.dp)
				.background(MaterialTheme.colors.surface, CircleShape)
		) {
			Icon(
				imageVector = Icons.Default.Pause,
				contentDescription = "Play"
			)
		}
		IconButton(
			onClick = { viewModel.stop() },
			enabled = clockState != ClockState.Off,
			modifier = Modifier
				.weight(1f).padding(5.dp)
				.background(MaterialTheme.colors.surface, CircleShape)
		) {
			Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
		}
	}
}

@Composable
fun AssetImage(pathToImage: String, modifier: Modifier) {
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