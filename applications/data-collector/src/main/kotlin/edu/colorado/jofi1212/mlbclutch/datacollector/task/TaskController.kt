package edu.colorado.jofi1212.mlbclutch.datacollector.task

import edu.colorado.jofi1212.mlbclutch.datacollector.game.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDate

data class TaskPayload(
    val date: LocalDate?
)

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val gameService: GameService) {
    @PostMapping("/fetch-games")
    fun handleFetchGames(@RequestBody payload: TaskPayload?): Mono<ResponseEntity<String>> {
        val targetDate = payload?.date ?: LocalDate.now().minusDays(1)

        return gameService.fetchAndSaveGames(targetDate)
            .collectList()
            .map { savedGames ->
                ResponseEntity.ok("Successfully processed ${savedGames.size} games.")
            }
            .onErrorResume { error ->
                error.printStackTrace()

                Mono.just(ResponseEntity.internalServerError().body("Task failed processing games"))
            }
    }
}