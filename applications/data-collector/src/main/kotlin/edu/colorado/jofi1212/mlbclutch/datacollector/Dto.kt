package edu.colorado.jofi1212.mlbclutch.datacollector

import java.time.Instant

data class MlbResponse(val dates: List<DateContainer>)

data class DateContainer(val games: List<GameDto>)

data class GameDto(
    val gamePk: Long,
    val gameType: String,
    val season: String,
    val gameDate: String,
    val status: StatusDto,
    val teams: TeamsDto
)

data class StatusDto(val abstractGameState: String)

data class TeamsDto(val away: TeamContainer, val home: TeamContainer)

data class TeamContainer(val team: TeamInfo, val score: Int?)

data class TeamInfo(val id: Long, val name: String)


fun GameDto.toDomain() = Game(
    gameId = this.gamePk,
    gameType = this.gameType,
    season = this.season.toInt(),
    gameDate = Instant.parse(this.gameDate),
    gameState = this.status.abstractGameState,
    awayTeamId = this.teams.away.team.id,
    awayTeamName = this.teams.away.team.name,
    awayTeamScore = this.teams.away.score ?: 0,
    homeTeamId = this.teams.home.team.id,
    homeTeamName = this.teams.home.team.name,
    homeTeamScore = this.teams.home.score ?: 0
)