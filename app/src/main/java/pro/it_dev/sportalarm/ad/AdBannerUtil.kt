package pro.it_dev.sportalarm.ad

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class AdBannerUtil(){
    fun initialBanner(context: Context, adView:AdView){
        MobileAds.initialize(context) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    fun createBanner(context: Context, adUnitId:String = "ca-app-pub-3940256099942544/6300978111"):AdView{
        val adView = AdView(context)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = adUnitId
        return adView
    }
}
