package com.ifmvo.togetherad.csj.provider

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.bytedance.sdk.openadsdk.*
import com.ifmvo.togetherad.core.listener.BannerListener
import com.ifmvo.togetherad.core.provider.BaseAdProvider
import com.ifmvo.togetherad.core.utils.ScreenUtil
import com.ifmvo.togetherad.core.utils.px2dp
import com.ifmvo.togetherad.csj.CsjProvider
import com.ifmvo.togetherad.csj.TogetherAdCsj

/**
 *
 * Created by Matthew Chen on 2020/11/25.
 */
abstract class CsjProviderBanner : BaseAdProvider() {

    private var mTTNativeExpressAd: TTNativeExpressAd? = null

    override fun showBannerAd(activity: Activity, adProviderType: String, alias: String, container: ViewGroup, listener: BannerListener) {

        destroyBannerAd()

        callbackBannerStartRequest(adProviderType, listener)

        val screenWidth = ScreenUtil.getDisplayMetricsWidth(activity)
        val wDp = if (CsjProvider.Banner.expressViewAcceptedSizeWidth == -1f) px2dp(activity, screenWidth) else CsjProvider.Banner.expressViewAcceptedSizeWidth
        val hDp = if (CsjProvider.Banner.expressViewAcceptedSizeHeight == -1f) wDp / 8 else CsjProvider.Banner.expressViewAcceptedSizeHeight

        val adSlot = AdSlot.Builder()
                .setCodeId(TogetherAdCsj.idMapCsj[alias]) //广告位id
                .setSupportDeepLink(CsjProvider.Banner.supportDeepLink)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(wDp, hDp) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(1, 1)//这个参数设置即可，不影响模板广告的size
                .build()
        TTAdSdk.getAdManager().createAdNative(activity).loadBannerExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onNativeExpressAdLoad(adList: MutableList<TTNativeExpressAd>?) {
                if (adList.isNullOrEmpty()) {
                    callbackBannerFailed(adProviderType, listener, "请求成功，但是返回的list为空")
                    return
                }
                callbackBannerLoaded(adProviderType, listener)

                mTTNativeExpressAd = adList[0]
                mTTNativeExpressAd?.setSlideIntervalTime(CsjProvider.Banner.slideIntervalTime)
                mTTNativeExpressAd?.setExpressInteractionListener(object : TTNativeExpressAd.ExpressAdInteractionListener {
                    override fun onAdClicked(p0: View?, p1: Int) {
                        callbackBannerClicked(adProviderType, listener)
                    }

                    override fun onAdShow(view: View?, p1: Int) {
                        callbackBannerExpose(adProviderType, listener)
                    }

                    override fun onRenderSuccess(view: View?, p1: Float, p2: Float) {
                        container.addView(view)
                    }

                    override fun onRenderFail(view: View?, errorMsg: String?, errorCode: Int) {
                        callbackBannerFailed(adProviderType, listener, "错误码：$errorCode, 错误信息：$errorMsg")
                    }
                })
                mTTNativeExpressAd?.setDislikeCallback(activity, object : TTAdDislike.DislikeInteractionCallback {
                    override fun onSelected(position: Int, value: String) {
                        //用户选择不喜欢原因后，移除广告展示
                        container.removeAllViews()
                        callbackBannerClosed(adProviderType, listener)
                    }

                    override fun onCancel() {}
                    override fun onRefuse() {}
                })
                mTTNativeExpressAd?.render()
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                callbackBannerFailed(adProviderType, listener, "错误码：$errorCode, 错误信息：$errorMsg")
            }
        })
    }

    override fun destroyBannerAd() {
        mTTNativeExpressAd?.destroy()
        mTTNativeExpressAd = null
    }

}