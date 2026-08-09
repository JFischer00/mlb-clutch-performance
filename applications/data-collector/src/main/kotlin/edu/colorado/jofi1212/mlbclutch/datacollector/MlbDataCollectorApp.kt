package edu.colorado.jofi1212.mlbclutch.datacollector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MlbDataCollectorApp

fun main(args: Array<String>) {
    runApplication<MlbDataCollectorApp>(*args)
}