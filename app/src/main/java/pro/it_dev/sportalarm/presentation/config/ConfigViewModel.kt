package pro.it_dev.sportalarm.presentation.config

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pro.it_dev.sportalarm.domain.Clock
import pro.it_dev.sportalarm.settings.Setting
import pro.it_dev.sportalarm.util.Message
import pro.it_dev.sportalarm.util.Resource

class ConfigViewModel: ViewModel() {

	val popUpMsg = mutableStateOf<String?>(null)

	val laps= mutableStateOf(0)
	val min = mutableStateOf(0L)
	val sec= mutableStateOf(0L)
	val pauseMin= mutableStateOf(0L)
	val pauseSec= mutableStateOf(0L)
	private val _volume = mutableStateOf(0f)
	val volume:State<Float> get() = _volume
	val whistling = mutableStateOf(false)
	val voice = mutableStateOf(false)
	val beep = mutableStateOf(false)



	fun getClock():Resource<Boolean>{
		val clock = Setting.getClockSetting()
		laps.value = clock.laps
		min.value = clock.workTime / 60
		sec.value = clock.workTime % 60
		pauseMin.value = clock.pauseTime / 60
		pauseSec.value = clock.pauseTime % 60
		whistling.value = clock.whistling
		voice.value = clock.voice
		beep.value = clock.beep
		_volume.value = clock.volume
		return Resource.Success(true)
	}
	fun saveClock(){
		val clock = Clock().apply {
			laps = (this@ConfigViewModel.laps.value).coerceAtMost(99)
			workTime = (this@ConfigViewModel.min.value * 60 + this@ConfigViewModel.sec.value).coerceAtMost(5999)
			pauseTime = (this@ConfigViewModel.pauseMin.value * 60 + this@ConfigViewModel.pauseSec.value).coerceAtMost(5999)
			volume = this@ConfigViewModel.volume.value.coerceIn(0f,1f)
			whistling = this@ConfigViewModel.whistling.value
			voice = this@ConfigViewModel.voice.value
			beep = this@ConfigViewModel.beep.value
		}
		Setting.saveClockSetting(clock)
	}

	fun convertStringValueToInt(value:String):Int{
		val filteredValue = value.filter { it.isDigit() }
		return if (filteredValue.isEmpty()) 0
		else filteredValue.toInt()
	}
	fun convertStringValueToLong(value:String):Long{
		val filteredValue = value.filter { it.isDigit() }
		return if (filteredValue.isEmpty()) 0
		else filteredValue.toLong()
	}

	fun setVolume(volumeValue: Float){
		_volume.value = volumeValue.coerceIn(0f,1f)
		popUpMsg.value = "Volume ${(_volume.value * 100).toInt()}"
		println("Volume ${(_volume.value * 100).toInt()}")

	}
}