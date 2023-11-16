package de.comtec.codecamp.weathermvp.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.comtec.codecamp.weathermvp.data.model.Quote
import de.comtec.codecamp.weathermvp.data.model.WeatherData
import de.comtec.codecamp.weathermvp.data.repositories.NetworkRepository
import de.comtec.codecamp.weathermvp.data.repositories.QuotesRepository
import de.comtec.codecamp.weathermvp.data.repositories.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val quotesRepository: QuotesRepository,
    private val networkRepository: NetworkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val weatherData = weatherRepository.weatherFlow
    val loading = mutableStateOf(false)
    val quoteInfo = mutableStateOf(Quote("Content", "Author"))
    val updateTime = mutableStateOf<LocalDateTime?>(null)

    val internetAccess = networkRepository.networkStatus

    fun fetchWeatherData() {
        viewModelScope.launch {
            loading.value = true
            weatherRepository.fetchWeatherData()
            updateTime.value = LocalDateTime.now()
            loading.value = false
        }
    }

    fun fetchQuote() {
        viewModelScope.launch {
            quoteInfo.value = quotesRepository.fetchQuote()
        }
    }

}