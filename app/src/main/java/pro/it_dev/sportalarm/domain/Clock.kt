package pro.it_dev.sportalarm.domain

class Clock (
	var laps:Int = 0,
	var workTime:Long = 0,
	var pauseTime:Long = 0,

	var whistling:Boolean = true,
	var voice:Boolean = true,
	var beep:Boolean = true
)