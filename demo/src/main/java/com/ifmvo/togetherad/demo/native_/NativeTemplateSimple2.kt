package com.ifmvo.togetherad.demo.native_

import com.ifmvo.togetherad.baidu.NativeViewBaiduSimple2
import com.ifmvo.togetherad.core.custom.flow.BaseNativeTemplate
import com.ifmvo.togetherad.core.custom.flow.BaseNativeView
import com.ifmvo.togetherad.csj.NativeViewCsjSimple2
import com.ifmvo.togetherad.demo.AdProviderType
import com.ifmvo.togetherad.gdt.NativeViewGdtSimple2

/**
 *
 * Created by Matthew Chen on 2020/8/28.
 */
class NativeTemplateSimple2 : BaseNativeTemplate() {

    override fun getNativeView(adProviderType: String): BaseNativeView? {
        return when (adProviderType) {
            AdProviderType.GDT.type -> {
                NativeViewGdtSimple2()
            }
            AdProviderType.CSJ.type -> {
                NativeViewCsjSimple2()
            }
            AdProviderType.BAIDU.type -> {
                NativeViewBaiduSimple2()
            }
            else -> throw Exception("模板配置错误")
        }
    }
}