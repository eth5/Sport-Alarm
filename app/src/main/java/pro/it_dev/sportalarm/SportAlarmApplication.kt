package pro.it_dev.sportalarm

import android.app.Application
import pro.it_dev.sportalarm.settings.Setting

class SportAlarmApplication:Application() {
	override fun onCreate() {
		super.onCreate()
		Setting.initial(baseContext)
	}
}