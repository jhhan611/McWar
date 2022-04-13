package com.github.jhhan611.ability.game

import com.github.jhhan611.ability.plugin
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

var game: Game? = null
var state: GameState? = null

fun isRunning(): Boolean {
    return game != null
}

fun startGame(): Boolean { //TODO: PLAYER COUNT CHECK
    if (isRunning()) return false
    game = Game()
    Bukkit.getServer().pluginManager.registerEvents(game!!, plugin!!)
    return true
}

fun stopGame(): Boolean {
    if (!isRunning()) return false
    state = null
    if (game?.playing == true)
        game?.stop()
    game = null
    return true
}


enum class GameState{
    ABILITY_CHOOSE,
    PEACE_TIME,
    MAIN_BATTLE
}