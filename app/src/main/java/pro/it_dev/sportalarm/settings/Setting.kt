package pro.it_dev.sportalarm.settings

import android.content.Context
import android.content.SharedPreferences
import pro.it_dev.sportalarm.domain.Clock

object Setting {
	private const val LAPS = "laps"
	private const val WORK_TIME = "pause_time"
	private const val PAUSE_TIME = "work_time"
	private const val BEEP = "beep"
	private const val WHISTLING = "whistling"
	private const val VOICE = "voice"

	private lateinit var config:SharedPreferences

	fun initial(ctx: Context){
		config = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
	}

	fun getClockSetting():Clock{
		return Clock().apply {
			laps = config.getInt(LAPS, 1)
			whistling = config.getBoolean(WHISTLING, true)
			voice = config.getBoolean(VOICE, true)
			beep = config.getBoolean(BEEP, true)

			workTime = config.getLong(WORK_TIME, 60)
			pauseTime = config.getLong(PAUSE_TIME, 20)
		}
	}
	fun saveClockSetting(clock: Clock){
		val editor = config.edit()
		editor.putInt(LAPS, clock.laps)
		editor.putBoolean(WHISTLING, clock.whistling)
		editor.putBoolean(VOICE, clock.voice)
		editor.putBoolean(BEEP, clock.beep)

		editor.putLong(WORK_TIME,clock.workTime)
		editor.putLong(PAUSE_TIME,clock.pauseTime)
		editor.apply()
	}

}