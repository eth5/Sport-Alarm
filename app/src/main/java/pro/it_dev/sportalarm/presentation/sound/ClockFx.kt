package pro.it_dev.sportalarm.presentation.sound

import pro.it_dev.sportalarm.domain.Clock

sealed class ClockFx(val path:String, var enable:Boolean){
	object Beep: ClockFx("raw/beep.ogg", true)
	object Start: ClockFx("raw/start.mp3", true)
	object Relax: ClockFx("raw/relax.mp3", true)
	object Pause: ClockFx("raw/pause.ogg", true)
	object Whistle: ClockFx("raw/whistle.ogg", true)
	companion object{
		fun setClockFxEnableState(clock: Clock){
			Whistle.enable = clock.whistling
			Beep.enable = clock.beep

			Start.enable = clock.voice
			Relax.enable = clock.voice
			Pause.enable = clock.voice
		}
	}
}