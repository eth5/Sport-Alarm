package pro.it_dev.sportalarm.presentation.activity

import android.graphics.BitmapFactory
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import pro.it_dev.sportalarm.ad.AdBannerUtil
import pro.it_dev.sportalarm.presentation.privacy.PrivacyDialog
import pro.it_dev.sportalarm.presentation.screens.ClockScreen
import pro.it_dev.sportalarm.presentation.sound.Sound
import pro.it_dev.sportalarm.presentation.ui.theme.SportAlarmTheme

class MainActivity : ComponentActivity() {
	private lateinit var sound: Sound
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sound = Sound()
		sound.load(
			Sound.BEEP,
			Sound.START,
			Sound.PAUSE,
			Sound.RELAX,
			Sound.WHISTLE,
			ctx = baseContext
		)


		setContent {
			SportAlarmTheme {
				Surface(
					color = MaterialTheme.colors.background,
					modifier = Modifier
						.fillMaxSize()
				) {
					Box(contentAlignment = Alignment.Center) {
						BackgroundImage(path = "bg.jpg")
						ClockScreen(sound = sound)
						AndroidView(
							factory = { ctx ->
								val adUtil = AdBannerUtil()
								val adView = adUtil.createBanner(
									ctx,
									"ca-app-pub-6127542757275882/1103625381"
								)
								adUtil.initialBanner(ctx, adView)
								adView
							},
							modifier = Modifier.align(Alignment.TopCenter)
						)
						Privacy(
							modifier = Modifier
								.align(Alignment.BottomCenter)
								.padding(bottom = 8.dp)
						)
					}
				}
			}
		}
	}

	@Composable
	fun Privacy(modifier: Modifier = Modifier) {
		var showPrivacyDialog by remember {
			mutableStateOf(false)
		}
		if (showPrivacyDialog) PrivacyDialog {
			showPrivacyDialog = false
		}
		TextButton(
			onClick = { showPrivacyDialog = true },
			modifier = modifier
		) {
			Text(text = "Privacy Policy")
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		sound.destroy()
		println("destroy")
	}

	@Composable
	fun BackgroundImage(path: String) {
		val ctx = LocalContext.current
		val imageBitmap by rememberUpdatedState(
			newValue = ctx.assets.open(path)
				.use { BitmapFactory.decodeStream(it) }.asImageBitmap()
		)
		Image(
			bitmap = imageBitmap,
			contentDescription = null,
			modifier = Modifier
				.fillMaxSize(),
			contentScale = ContentScale.FillBounds
		)
	}
}





