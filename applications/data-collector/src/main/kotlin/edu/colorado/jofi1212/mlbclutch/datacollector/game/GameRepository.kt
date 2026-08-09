package edu.colorado.jofi1212.mlbclutch.datacollector.game

import org.springframework.data.repository.CrudRepository

interface GameRepository: CrudRepository<Game, Long> {
}