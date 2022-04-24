package pro.it_dev.sportalarm.presentation.screens

import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
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
import pro.it_dev.sportalarm.presentation.sound.SoundEvent

@Composable
fun ClockScreen(sound: Sound, viewModel: ClockViewModel = viewModel()) {

	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {


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
					UpdateViewData(viewModel = viewModel)
					ShowIconButton(viewModel = viewModel)
				}
			}
		}

		ClockHandleBottomButtons(viewModel = viewModel)
	}


	ShowConfigDialog(viewModel = viewModel)
	PlaySoundEvent(viewModel.soundEvent, sound = sound)
}

@Composable
fun ShowIconButton(viewModel: ClockViewModel){
	IconButton(onClick = { viewModel.showConfig(true) }) {
		Icon(
			imageVector = Icons.Default.Alarm,
			contentDescription = "Config",
			tint = Color.Gray,
			modifier = Modifier
				.size(40.dp)
		)
	}
}

@Composable
fun ShowConfigDialog(viewModel: ClockViewModel){
	val showConfig by viewModel.showConfig
	if (showConfig) ConfigDialog {
		viewModel.showConfig(false)
		viewModel.resetClockViewModelValues()
	}
}

@Composable
fun PlaySoundEvent(soundEventState: State<SoundEvent?>, sound: Sound) {
	val soundEvent by soundEventState
	if (soundEvent != null) {
		LaunchedEffect(key1 = soundEvent) {
			val volume = soundEvent!!.volume
			soundEvent!!.list.forEach {
				if (it.enable) sound.play(it.path,volume, soundEvent!!.rate)
			}
		}
	}
}


@Composable
fun UpdateViewData(viewModel: ClockViewModel){
	ShowLaps(viewModel = viewModel)
	ShowState(viewModel = viewModel)
	ShowTime(viewModel = viewModel)
}

private val sb = StringBuilder();
@Composable
private fun ShowLaps(viewModel: ClockViewModel){
	val laps by viewModel.laps;
	val currentLap by viewModel.currentLap
	sb.clear()

	Text(
		text = sb.append("Lap ").append(currentLap).append(" of ").append(laps).toString(),
		color = MaterialTheme.colors.primary,
		fontSize = 20.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.padding(top = 8.dp)
	)
}

@Composable
private fun ShowTime(viewModel: ClockViewModel){
	val clockState by viewModel.clockState
	val timeText by viewModel.timeText
	var multiplier by remember { mutableStateOf(5f) }
	var readyToDraw by remember { mutableStateOf(false) }
	Text(
		text = timeText,
		color = clockState.color,
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
}

@Composable
private fun ShowState(viewModel: ClockViewModel){
	val clockState by viewModel.clockState
	val text = when (clockState) {
		ClockState.Off -> "Off"
		ClockState.InRelax -> "Relax time!"
		ClockState.InRun -> "Work time!"
		ClockState.InPause -> "Pause!"
	}

	Text(
		text = text,
		fontWeight = FontWeight.Bold,
		color = Color.Black
	)
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
fun ClockHandleBottomButtons(viewModel: ClockViewModel) {
	val clockStateValue by viewModel.clockState
	Row(
		modifier = Modifier
			.fillMaxWidth()
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
		IconBottomButton(
			imageVector = Icons.Default.PlayArrow,
			contentDescription = "Play",
			enabled = clockStateValue == ClockState.Off,
			modifier = Modifier.weight(1f),
			onClick = { viewModel.start() }
		)
		IconBottomButton(
			imageVector = Icons.Default.Pause,
			contentDescription = "Pause",
			enabled = clockStateValue != ClockState.Off,
			modifier = Modifier.weight(1f),
			onClick = { viewModel.pause() }
		)
		IconBottomButton(
			imageVector = Icons.Default.Stop,
			contentDescription = "Stop",
			enabled = clockStateValue != ClockState.Off,
			modifier = Modifier.weight(1f),
			onClick = { viewModel.stop() }
		)
	}
}

@Composable
fun IconBottomButton(imageVector: ImageVector,contentDescription:String, enabled: Boolean, modifier: Modifier, onClick:()->Unit) {
	IconButton(
		onClick = onClick,
		enabled = enabled,
		modifier = modifier
			.padding(5.dp)
			.background(MaterialTheme.colors.background, CircleShape)
			.border(
				1.dp,
				MaterialTheme.colors.secondary.copy(alpha = LocalContentAlpha.current * if (enabled) 1f else 0.2f),
				CircleShape
			)
	) {
		Icon(
			imageVector = imageVector,
			contentDescription = contentDescription
		)
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