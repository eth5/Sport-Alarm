package pro.it_dev.sportalarm.settings

import android.content.Context
import android.content.SharedPreferences
import pro.it_dev.sportalarm.domain.Clock

object Setting {
	private const val INIT_KEY = "initialed"
	private const val LAPS = "laps"
	private const val MIN = "min"
	private const val SEC = "sec"
	private const val PAUSE_MIN = "pause_min"
	private const val PAUSE_SEC = "pause_sec"
	private const val WHISTLING = "whistling"
	private const val VOICE = "voice"

	private lateinit var config:SharedPreferences

	fun initial(ctx: Context){
		config = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
	}

	fun getClockSetting():Clock{
		return Clock().apply {
			laps = config.getInt(LAPS, 1)
			min = config.getInt(MIN, 1)
			sec = config.getInt(SEC, 0)
			pauseMin = config.getInt(PAUSE_MIN, 0)
			pauseSec = config.getInt(PAUSE_SEC, 20)
			whistling = config.getBoolean(WHISTLING, true)
			voice = config.getBoolean(VOICE, true)
		}
	}
	fun saveClockSetting(clock: Clock){
		val editor = config.edit()
		editor.putInt(LAPS, clock.laps)
		editor.putInt(MIN, clock.min)
		editor.putInt(SEC, clock.sec)
		editor.putInt(PAUSE_MIN, clock.pauseMin)
		editor.putInt(PAUSE_SEC, clock.pauseSec)
		editor.putBoolean(WHISTLING, clock.whistling)
		editor.putBoolean(VOICE, clock.voice)
		editor.apply()
	}

}