package de.comtec.codecamp.weathermvp.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import de.comtec.codecamp.weathermvp.R
import de.comtec.codecamp.weathermvp.data.model.Quote
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import java.time.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {

    val weather = viewModel.weatherData.collectAsState(initial = null).value
    val loading = viewModel.loading.value
    val hasNetwork = viewModel.internetAccess.collectAsState().value
    val quoteInfo = viewModel.quoteInfo.value
    val lastUpdateTime = viewModel.updateTime.value

    val pullRefreshState = rememberPullRefreshState(refreshing = loading, onRefresh = {
        if (hasNetwork) {
            viewModel.fetchWeatherData()
            viewModel.fetchQuote()
        }
    })

    LaunchedEffect(key1 = hasNetwork, block = {
        if (hasNetwork) {
            viewModel.fetchWeatherData()
            viewModel.fetchQuote()
        }
    })

    Box {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .pullRefresh(pullRefreshState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!hasNetwork) {
                item {
                    NoNetwork()
                }
            } else {
                item {
                    TemperatureInfo(weather?.temperature)
                }
                item {
                    PrecipitationInfo(weather?.precipitationProbability)
                }
                item {
                    QuoteInfo(quoteInfo)
                }
            }
            item {
                TimeInfo(time = lastUpdateTime)
            }
        }
        PullRefreshIndicator(
            refreshing = loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview
@Composable
fun NoNetwork() {
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Error icon
                Icon(painterResource(id = R.drawable.ic_error), contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "No internet connection", fontSize = 16.sp)
            }
        }
    }
}

@Preview
@Composable
fun TemperatureInfo(temperature: Double? = null) {
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${temperature ?: "-"}°C", fontSize = 28.sp)
            Text(text = "Temperature", fontSize = 12.sp)
        }
    }
}

@Preview
@Composable
fun PrecipitationInfo(precipitation: Double? = null) {
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${precipitation ?: "-"}%", fontSize = 28.sp)
            Text(text = "Precipitation", fontSize = 12.sp)
        }
    }
}

@Preview
@Composable
fun QuoteInfo(quoteInfo: Quote = Quote("Content", "Author")) {
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = quoteInfo.content ?: "-", fontSize = 16.sp)
            Text(text = quoteInfo.author ?: "-", fontSize = 12.sp)
        }
    }
}

@Composable
fun TimeInfo(time: LocalDateTime? = null) {
    Card{
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            if (time != null) {
                Text(text = "Last update: ${time.hour ?: "-"}:${time.minute ?: "-"}", fontSize = 12.sp)
            } else {
                Text(text = "Last update: never", fontSize = 12.sp)
            }
        }
    }
}

