package pro.it_dev.sportalarm.presentation.privacy

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.runBlocking
import pro.it_dev.sportalarm.util.fromHtml


@Composable
fun PrivacyDialog(onDismissRequest: () -> Unit) {
	Dialog(
		onDismissRequest = onDismissRequest,
		properties = DialogProperties(dismissOnClickOutside = true)
	) {
		Box(
			modifier = Modifier
				.background(MaterialTheme.colors.background, MaterialTheme.shapes.medium)
				.border(1.dp, MaterialTheme.colors.secondary, MaterialTheme.shapes.medium)
				.fillMaxWidth(1f)
				.fillMaxHeight(0.8f),
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
				modifier = Modifier.fillMaxSize(animation),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				val ctx = LocalContext.current
				val privacyText by produceState(initialValue = "") {
					value = (Dispatchers.IO) { ctx.assets.open("123.txt")
						.bufferedReader()
						.use {
							it.readText()
						}
					}
				}

				Text(
					text = privacyText.fromHtml().toString(),
					modifier = Modifier
						.weight(1f)
						.padding(8.dp)
						.verticalScroll(rememberScrollState())
				)
				TextButton(
					onClick = {
						onDismissRequest()
					}
				) {
					Text(text = "OK, BRO")
				}
			}
		}

	}

}