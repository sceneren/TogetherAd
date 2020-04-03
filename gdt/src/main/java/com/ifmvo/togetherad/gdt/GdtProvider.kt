package com.ifmvo.togetherad.gdt

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.ifmvo.togetherad.core.TogetherAd
import com.ifmvo.togetherad.core._enum.AdProviderType
import com.ifmvo.togetherad.core.listener.CommonListener
import com.ifmvo.togetherad.core.provider.BaseAdProvider
import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew Chen on 2020-04-03.
 */
class GdtProvider : BaseAdProvider() {

    private val adProviderType = AdProviderType.GDT

    override fun showSplashAd(activity: Activity, alias: String, radio: String?, container: ViewGroup, listener: CommonListener) {
        listener.onStartRequest(adProviderType)
        val splash = SplashAD(activity, skipView, TogetherAd.appIdGDT, TogetherAd.idMapGDT[adConstStr], object : SplashADListener {
            override fun onADDismissed() {
                listener.onAdDismissed()
                logd("${AdNameType.GDT.type}: ${activity.getString(R.string.dismiss)}")
                /*timer?.cancel()
                timerTask?.cancel()*/
            }

            override fun onNoAD(adError: AdError) {
                if (stop) {
                    return
                }
                cancelTimerTask()
                loge("${AdNameType.GDT.type}: ${adError.errorMsg}")
                val newConfigPreMovie = splashConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                showAdFull(activity, newConfigPreMovie, adConstStr, adsParentLayout, skipView, timeView, listener)
            }

            override fun onADPresent() {
                if (stop) {
                    return
                }
                activity.runOnUiThread {
                    skipView?.visibility = View.VISIBLE
                }
                cancelTimerTask()

                listener.onAdPrepared(AdNameType.GDT.type)
                logd("${AdNameType.GDT.type}: ${activity.getString(R.string.prepared)}")
            }

            override fun onADClicked() {
                listener.onAdClick(AdNameType.GDT.type)
                logd("${AdNameType.GDT.type}: ${activity.getString(R.string.clicked)}")
                /*timer?.cancel()
                timerTask?.cancel()*/
            }

            override fun onADTick(l: Long) {
                logd("${AdNameType.GDT.type}: 倒计时: ${l / 1000 + 1}")
                activity.runOnUiThread {
                    timeView?.text = (l / 1000 + 1).toString()
                }
            }

            override fun onADExposure() {
                logd("${AdNameType.GDT.type}: ${activity.getString(R.string.exposure)}")
            }

            override fun onADLoaded(p0: Long) {}
        }, 0)

        splash.fetchAndShowIn(adsParentLayout)
    }

    override fun getNativeAdList(activity: Activity, alias: String, radio: String?, listener: CommonListener) {

    }

}