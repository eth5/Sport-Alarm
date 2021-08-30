package pro.it_dev.sportalarm.presentation.sound

import android.content.Context
import android.media.SoundPool

class Sound() {
	companion object{
		const val BEEP = "raw/beep.ogg"
		const val START = "raw/start.mp3"
		const val RELAX = "raw/relax.mp3"
		const val PAUSE = "raw/pause.ogg"
		const val WHISTLE = "raw/whistle.ogg"
	}
	private val sp = SoundPool
		.Builder()
		.setMaxStreams(3)
		.build()
	private val map = mutableMapOf<String,Int>()

	fun load(vararg sounds:String, ctx: Context){
		sounds.forEach {
			map[it] = sp.load(ctx.assets.openFd(it),0)
		}
	}

	fun play(file:String, rate:Float = 1f){
		val soundId = map[file] ?: throw NullPointerException("")
		sp.play(soundId,1f,1f,0,0,rate)
	}

	fun destroy(){
		sp.release()
	}
}