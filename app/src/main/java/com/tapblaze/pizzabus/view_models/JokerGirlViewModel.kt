package com.tapblaze.pizzabus.view_models

import android.app.Application
import androidx.ads.identifier.AdvertisingIdClient
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.onesignal.OneSignal
import com.tapblaze.pizzabus.data.ProtoRepository
import com.tapblaze.pizzabus.other.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class JokerGirlViewModel(
    app: Application,
    private val repo: ProtoRepository
) : AndroidViewModel(app) {

    private val _flowLink: MutableStateFlow<String> = MutableStateFlow("")
    val flowLink: StateFlow<String> get() = _flowLink

    suspend fun isLaunchedEarlier(): Boolean {
        val isLaunchedEarlier = repo.jokerGirlPreferencesFlow.map { it.isLaunchedEarlier }.first()
        repo.setLaunchedEarlier()
        return isLaunchedEarlier
    }

    suspend fun currentLink(): String = repo.jokerGirlPreferencesFlow.map { it.link }.first()

    suspend fun updateLink(link: String) = repo.updateLink(link)

    fun fetchLink(appsFlyerData: MutableMap<String, Any>?) {
        OneSignal.initWithContext(getApplication())
        OneSignal.setAppId(Const.ONE_SIGNAL_APP_ID)

        viewModelScope.launch {
            //Get data
            val facebookLink = getFacebookLink()
            val googleAdvertisingId = getGoogleAdvertisingId()

            //Get url
            val link = createUrl(googleAdvertisingId, facebookLink, appsFlyerData)

            //Save url
            repo.updateLink(link)

            //Send tag
            sendTag(appsFlyerData, facebookLink)

            //Notify observers
            _flowLink.value = link
        }
    }

    private fun sendTag(
        appsFlyerData: MutableMap<String, Any>?,
        facebookLink: String
    ) {
        val campaign = appsFlyerData?.get("campaign").toString()
        when {
            campaign == "null" && facebookLink == "null" -> {
                OneSignal.sendTag(
                    "key2",
                    "organic"
                )
            }
            facebookLink != "null" && campaign == "null" -> {
                OneSignal.sendTag(
                    "key2",
                    facebookLink.replace("myapp://", "").substringBefore("/")
                )
            }
            campaign != "null" && facebookLink == "null" -> {
                OneSignal.sendTag(
                    "key2",
                    campaign.substringBefore("_")
                )
            }
        }
    }

    private fun createUrl(
        googleAdvertisingId: String,
        facebookLink: String,
        appsFlyerData: MutableMap<String, Any>?
    ): String {
        val link = Const.BASE_LINK.toUri().buildUpon().apply {
            appendQueryParameter(Const.SECURE_GET_PARAMETR, Const.SECURE_KEY)
            appendQueryParameter(Const.DEV_TMZ_KEY, TimeZone.getDefault().id)
            appendQueryParameter(Const.GADID_KEY, googleAdvertisingId)
            appendQueryParameter(Const.DEEPLINK_KEY, facebookLink)
            appendQueryParameter(
                Const.SOURCE_KEY,
                appsFlyerData?.get("media_source").toString()
            )
            appendQueryParameter(
                Const.AF_ID_KEY,
                AppsFlyerLib.getInstance().getAppsFlyerUID(getApplication())
            )
            appendQueryParameter(Const.ADSET_ID_KEY, appsFlyerData?.get("adset_id").toString())
            appendQueryParameter(
                Const.CAMPAIGN_ID_KEY,
                appsFlyerData?.get("campaign_id").toString()
            )
            appendQueryParameter(
                Const.APP_CAMPAIGN_KEY,
                appsFlyerData?.get("campaign").toString()
            )
            appendQueryParameter(Const.ADSET_KEY, appsFlyerData?.get("adset").toString())
            appendQueryParameter(Const.ADGROUP_KEY, appsFlyerData?.get("adgroup").toString())
            appendQueryParameter(
                Const.ORIG_COST_KEY,
                appsFlyerData?.get("orig_cost").toString()
            )
            appendQueryParameter(
                Const.AF_SITEID_KEY,
                appsFlyerData?.get("af_siteid").toString()
            )
        }.toString()
        return link
    }

    private suspend fun getFacebookLink(): String = suspendCoroutine { cont ->
        AppLinkData.fetchDeferredAppLinkData(getApplication()) { appLinkData ->
            cont.resume(appLinkData?.targetUri.toString())
        }
    }

    private suspend fun getGoogleAdvertisingId(): String = withContext(Dispatchers.Default) {
        AdvertisingIdClient.getAdvertisingIdInfo(getApplication()).get().id
    }
}