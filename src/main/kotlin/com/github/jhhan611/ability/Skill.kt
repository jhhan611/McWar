package com.github.jhhan611.ability

import org.bukkit.entity.Player

abstract class Trigger(val name: String, val cooldown: Int) {
    var playerCooldown = mutableMapOf<Player, Int>()

    fun getCooldown(player: Player) : Int? {
        return playerCooldown[player]
    }

    fun useSkill(player: Player) : Boolean {
        if ((playerCooldown[player] ?: return false) > 0) return false
        skill(player)
        playerCooldown[player] = cooldown
        return true
    }

    open fun skill(player: Player) {}
}