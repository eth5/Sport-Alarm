package pro.it_dev.sportalarm.presentation.sound

import android.content.Context
import android.media.SoundPool
import pro.it_dev.sportalarm.domain.Clock

class Sound() {
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

	fun play(file:String, volume: Float, rate:Float = 1f){
		val soundId = map[file] ?: throw NullPointerException("")
		sp.play(soundId,volume,volume,0,0,rate)
	}

	fun destroy(){
		sp.release()
	}
}