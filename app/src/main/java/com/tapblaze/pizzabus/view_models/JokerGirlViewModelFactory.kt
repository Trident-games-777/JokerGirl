package com.tapblaze.pizzabus.view_models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tapblaze.pizzabus.data.ProtoRepository

@Suppress("UNCHECKED_CAST")
class JokerGirlViewModelFactory(
    private val app: Application,
    private val repo: ProtoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return JokerGirlViewModel(app, repo) as T
    }

}