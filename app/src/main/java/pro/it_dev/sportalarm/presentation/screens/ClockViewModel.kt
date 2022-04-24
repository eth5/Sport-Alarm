package pro.it_dev.sportalarm.presentation.screens

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import pro.it_dev.sportalarm.domain.Clock
import pro.it_dev.sportalarm.presentation.sound.ClockFx
import pro.it_dev.sportalarm.presentation.sound.SoundEvent
import pro.it_dev.sportalarm.settings.Setting
import pro.it_dev.sportalarm.util.longTimeToStringTime


enum class ClockState(val color:Color) {
    Off(Color.Black),
    InRelax(Color.Green),
    InRun(Color.Red),
    InPause(Color.Red);
}

class ClockViewModel : ViewModel() {
    private lateinit var clock: Clock
    private var pause: Boolean = false

    private val _laps = mutableStateOf(0)
    val laps: State<Int> get() = _laps

    private val currentLapMutable = mutableStateOf(0)
    val currentLap: State<Int> get() = currentLapMutable

    private val timeTextMutable = mutableStateOf("00:00")
    val timeText: State<String> get() = timeTextMutable

    val soundEvent = mutableStateOf<SoundEvent?>(null)

    val ms = mutableStateOf(0)

    val clockState = mutableStateOf(ClockState.Off)

    private val _showConfig = mutableStateOf(false)
    val showConfig: State<Boolean> = _showConfig

    fun showConfig(status: Boolean) {
        if (status) stop()
        _showConfig.value = status
    }

    init {
        resetClockViewModelValues()
    }

    fun resetClockViewModelValues() {
        clock = Setting.getClockSetting().also {
            _laps.value = it.laps
        }
        ClockFx.updateSoundEnabledState(clock)
    }

    private var job: Job? = null

    fun start() {
        if (clockState.value != ClockState.Off) return
        clockState.value = ClockState.InRun


        viewModelScope.launch {
            job?.cancelAndJoin()
            resetClockViewModelValues()

            Log.d("ViewModel","Set dudka")
            soundEvent.value = SoundEvent(listOf(ClockFx.Whistle), volume = clock.volume)

            delay(1000)

            val job = viewModelScope.launch(Dispatchers.Default) {
                currentLapMutable.value = 0
                pause = false

                while (currentLap.value < laps.value) {
                    currentLapMutable.value++

                    clockState.value = ClockState.InRun
                    soundEvent.value = SoundEvent(
                        listOf(ClockFx.Start),
                        volume = clock.volume //todo refactor this shit
                    )
                    loop(clock.workTime, timeTextMutable)

                    clockState.value = ClockState.InRelax
                    soundEvent.value = SoundEvent(listOf(ClockFx.Relax), volume = clock.volume)
                    loop(clock.pauseTime, timeTextMutable)
                }
                resetClockViewModelValues()
            }
            job.invokeOnCompletion {
                currentLapMutable.value = 0
                timeTextMutable.value = "00:00"
                clockState.value = ClockState.Off
                pause = false
            }
            this@ClockViewModel.job = job
        }
    }

    private suspend fun loop(timeValue: Long, state: MutableState<String>) {
        var time = timeValue
        state.value = longTimeToStringTime(time)
        while (time-- >= 0) {
            var ms = 10
            while (--ms >= 0) {
                this.ms.value = ms
                delay(100)
                while (pause) {
                    delay(100)
                }
            }
            state.value = longTimeToStringTime(time)
            if (time in 0..3) soundEvent.value =
                SoundEvent(listOf(ClockFx.Beep), volume = clock.volume)
        }
    }

    fun stop() {
        job?.cancel()
        resetClockViewModelValues()
    }

    private lateinit var beforePauseSate: ClockState
    fun pause() {
        if (!pause) beforePauseSate = clockState.value
        pause = !pause
        clockState.value = if (pause) ClockState.InPause else beforePauseSate
    }

}