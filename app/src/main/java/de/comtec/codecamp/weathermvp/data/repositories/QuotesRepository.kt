package de.comtec.codecamp.weathermvp.data.repositories

import de.comtec.codecamp.weathermvp.data.model.Quote
import de.comtec.codecamp.weathermvp.data.network.QuotesService

class QuotesRepository(private val api: QuotesService) {

    suspend fun fetchQuote() : Quote {
        return api.getRandomQuote().body() ?: Quote("No quote", "No author")
    }

}