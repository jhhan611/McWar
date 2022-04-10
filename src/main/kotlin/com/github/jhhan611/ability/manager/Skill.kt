package com.github.jhhan611.ability.manager

import com.github.jhhan611.ability.plugin
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

/**
 * Abstract class for trigger skills
 *
 * @param name Name of this trigger skill
 * @param cooldown The cooldown of this trigger skill in seconds
 */
abstract class Trigger(val name: String, private val cooldown: Int) {
    private var playerCooldown = mutableMapOf<Player, Int>()

    fun startCooldown(player: Player) {
        playerCooldown[player] = cooldown

        object : BukkitRunnable() {
            override fun run() {
                if ((playerCooldown[player] ?: return cancel()) <= 0) return cancel()
                playerCooldown[player] = playerCooldown[player]!! - 1
            }
        }.runTaskTimer(plugin!!, 20, 20)
    }

    fun getCooldown(player: Player) : Int? {
        return playerCooldown[player]
    }

    fun useSkill(player: Player) : Boolean {
        if ((playerCooldown[player] ?: return false) > 0) return false
        skill(player)
        startCooldown(player)
        return true
    }

    open fun skill(player: Player) {}
}