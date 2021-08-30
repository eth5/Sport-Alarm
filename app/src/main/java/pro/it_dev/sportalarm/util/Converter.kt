package pro.it_dev.sportalarm.util

fun longTimeToStringTime(value:Long, prefix:Char = ':'): String{
	return buildString {
		val min = (value / 60)
		append(if (min < 10) "0$min" else min)
		append(prefix)
		val sec = (value - min * 60)
		append( if (sec < 10) "0$sec" else sec)
	}
}