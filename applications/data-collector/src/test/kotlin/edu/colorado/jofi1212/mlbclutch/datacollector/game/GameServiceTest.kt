package edu.colorado.jofi1212.mlbclutch.datacollector.game

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.LocalDate

@SpringBootTest(
    properties = [
        "spring.cloud.gcp.sql.enabled=false"
    ]
)
@Testcontainers
class GameServiceTest {

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var gameRepository: GameRepository

    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("mlb_db")
            .withUsername("test")
            .withPassword("test")

        private val wireMockServer = WireMockServer(8089)

        @BeforeAll
        @JvmStatic
        fun startServices() {
            wireMockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun stopServices() {
            wireMockServer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("mlb.api.base-url") { "http://localhost:8089/api" }
        }
    }

    @Test
    fun `should fetch games from API and successfully save to database`() {
        // Arrange: Mock the external MLB API response payload body
        val targetDate = LocalDate.of(2026, 8, 9)
        val mockApiResponseJson = """
            {
              "dates": [
                {
                  "games": [
                  {
                      "gamePk": 822700,
                      "gameGuid": "11bf84e6-e2e4-4c79-b7eb-2190a5ce879c",
                      "link": "/api/v1.1/game/822700/feed/live",
                      "gameType": "R",
                      "season": "2026",
                      "gameDate": "2026-08-09T16:15:00Z",
                      "officialDate": "2026-08-09",
                      "status": {
                        "abstractGameState": "Live",
                        "codedGameState": "I",
                        "detailedState": "In Progress",
                        "statusCode": "I",
                        "startTimeTBD": false,
                        "abstractGameCode": "L"
                      },
                      "teams": {
                        "away": {
                          "team": {
                            "id": 113,
                            "name": "Cincinnati Reds",
                            "link": "/api/v1/teams/113"
                          },
                          "leagueRecord": {
                            "wins": 56,
                            "losses": 60,
                            "ties": 0,
                            "pct": ".483"
                          },
                          "score": 1,
                          "splitSquad": false,
                          "seriesNumber": 38
                        },
                        "home": {
                          "team": {
                            "id": 120,
                            "name": "Washington Nationals",
                            "link": "/api/v1/teams/120"
                          },
                          "leagueRecord": {
                            "wins": 58,
                            "losses": 61,
                            "ties": 0,
                            "pct": ".487"
                          },
                          "score": 2,
                          "splitSquad": false,
                          "seriesNumber": 38
                        }
                      },
                      "venue": {
                        "id": 3309,
                        "name": "Nationals Park",
                        "link": "/api/v1/venues/3309"
                      },
                      "content": {
                        "link": "/api/v1/game/822700/content"
                      },
                      "gameNumber": 1,
                      "publicFacing": true,
                      "doubleHeader": "N",
                      "gamedayType": "P",
                      "tiebreaker": "N",
                      "calendarEventID": "14-822700-2026-08-09",
                      "seasonDisplay": "2026",
                      "dayNight": "day",
                      "scheduledInnings": 9,
                      "reverseHomeAwayStatus": false,
                      "inningBreakLength": 120,
                      "gamesInSeries": 3,
                      "seriesGameNumber": 3,
                      "seriesDescription": "Regular Season",
                      "recordSource": "S",
                      "ifNecessary": "N",
                      "ifNecessaryDescription": "Normal Game"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        wireMockServer.stubFor(
            get(urlPathEqualTo("/api/v1/schedule"))
                .withQueryParam("sportId",equalTo("1"))
                .withQueryParam("date", equalTo(targetDate.toString()))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponseJson)
                        .withStatus(200)
                )
        )

        // Act: Execute the service pipeline mapping logic
        gameService.fetchAndSaveGames(targetDate)

        // Assert: Verify data made it through the entire pipeline into the actual DB
        val savedGames = gameRepository.findAll().toList()
        assertThat(savedGames).hasSize(1)

        val game = savedGames.first()
        assertThat(game.gameId).isEqualTo(822700)
        assertThat(game.homeTeamName).isEqualTo("Washington Nationals")
        assertThat(game.awayTeamName).isEqualTo("Cincinnati Reds")
    }
}