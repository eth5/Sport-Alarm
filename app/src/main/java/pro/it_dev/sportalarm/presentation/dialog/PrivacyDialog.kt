package pro.it_dev.sportalarm.presentation.dialog

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import pro.it_dev.sportalarm.util.fromHtml


@Composable
fun MyDialog(onDismissRequest: () -> Unit) {
	Dialog(
		onDismissRequest = onDismissRequest,
		properties = DialogProperties(dismissOnClickOutside = true)
	) {
		Box(
			modifier = Modifier
				.background(MaterialTheme.colors.background, RoundedCornerShape(10.dp))
				.shadow(0.dp, RoundedCornerShape(10.dp))
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
			LaunchedEffect(key1 = "", block = { size = 1f })

			Column(
				modifier = Modifier.fillMaxSize(animation),
				horizontalAlignment = Alignment.CenterHorizontally
			) {

				var privacyText by remember {
					mutableStateOf("")
				}
				val ctx = LocalContext.current
				LaunchedEffect(key1 = ctx){
					privacyText = ctx.assets.open("123.txt")
						.bufferedReader()
						.use {
							it.readText()
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