package edu.colorado.jofi1212.mlbclutch.datacollector.game

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Transient
import org.springframework.data.domain.Persistable
import java.time.Instant

@Entity
data class Game(
    @Id val gameId: Long,
    val gameType: String,
    val season: Int,
    val gameDate: Instant,
    val gameState: String,
    val awayTeamId: Long,
    val awayTeamName: String,
    val awayTeamScore: Int,
    val homeTeamId: Long,
    val homeTeamName: String,
    val homeTeamScore: Int
) : Persistable<Long> {

    override fun getId(): Long = gameId

    @Transient
    private var isNewRecord: Boolean = false

    override fun isNew(): Boolean = isNewRecord

    fun markAsNew(): Game {
        this.isNewRecord = true
        return this
    }
}
