package edu.colorado.jofi1212.mlbclutch.datacollector

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDate

@Service
class GameService(
    private val webClient: WebClient,
    private val gameRepository: GameRepository
) {
    @Transactional
    fun fetchAndSaveGames(date: LocalDate): Flux<Game> {
        return webClient.get()
            .uri("/v1/schedule?sportId=1&date=$date")
            .retrieve()
            .bodyToMono(MlbResponse::class.java)
            .map { response ->
                response.dates.flatMap { it.games }
                    .map { it.toDomain() }
            }
            .flatMapMany { games ->
                Mono.fromCallable { gameRepository.saveAll(games) }
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMapMany { Flux.fromIterable(it) }
            }
    }
}