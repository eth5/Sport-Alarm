package pro.it_dev.sportalarm.domain

class Clock (
	var laps:Int = 0,
	var min:Int = 0,
	var sec:Int = 0,
	var ms:Int = 0,
	var pauseMin:Int = 0,
	var pauseSec:Int = 0,
	var whistling:Boolean = true,
	var voice:Boolean = true
)