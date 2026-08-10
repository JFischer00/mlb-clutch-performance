package edu.colorado.jofi1212.mlbclutch.datacollector.game

import edu.colorado.jofi1212.mlbclutch.datacollector.game.dto.MlbResponse
import edu.colorado.jofi1212.mlbclutch.datacollector.game.dto.toDomain
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

@Service
class GameService(
    private val webClient: WebClient,
    private val gameRepository: GameRepository
) {
    @Transactional
    fun fetchAndSaveGames(date: LocalDate): List<Game> {
        val games =  webClient.get()
            .uri("/v1/schedule?sportId=1&date=$date")
            .retrieve()
            .bodyToMono(MlbResponse::class.java)
            .map { response ->
                response.dates.flatMap { it.games }
                    .map { it.toDomain() }
            }
            .block() ?: emptyList()

        return gameRepository.saveAll(games).toList()
    }
}