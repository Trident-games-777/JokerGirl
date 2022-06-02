package com.tapblaze.pizzabus.other

import com.appsflyer.AppsFlyerConversionListener

class AppsFlyerConversionListenerWrapper(
    private val block: (MutableMap<String, Any>?) -> Unit
) : AppsFlyerConversionListener {
    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) { block(p0) }
    override fun onConversionDataFail(p0: String?) {}
    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
    override fun onAttributionFailure(p0: String?) {}
}
