package pro.it_dev.sportalarm.presentation.config

import androidx.lifecycle.ViewModel
import pro.it_dev.sportalarm.domain.Clock
import pro.it_dev.sportalarm.settings.Setting
import pro.it_dev.sportalarm.util.Resource

class ConfigViewModel: ViewModel() {

	suspend fun getClock():Resource<Clock>{
		val clock = Setting.getClockSetting()
		return Resource.Success(clock)
	}
	fun saveClock(clock: Clock){
		Setting.saveClockSetting(clock)
	}

	fun convertStringValueToInt(value:String):Int{
		val filteredValue = value.filter { it.isDigit() }
		return if (filteredValue.isEmpty()) 0
		else filteredValue.toInt()
	}


}