package com.github.jhhan611.ability.manager

import com.github.jhhan611.ability.plugin
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
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
    var color = TextColor.color(0xFFFFFF)

    fun startCooldown(player: Player) {
        playerCooldown[player] = cooldown

        object : BukkitRunnable() {
            override fun run() {
                playerCooldown[player] = playerCooldown[player]!! - 1
                if ((playerCooldown[player] ?: return cancel()) <= 0) return cancel()

            }
        }.runTaskTimer(plugin!!, 20, 20)
    }

    fun getCooldown(player: Player) : Int? {
        return playerCooldown[player]
    }

    fun useSkill(player: Player) : Boolean {
        if (playerCooldown[player] == null) playerCooldown[player] = 0
        if ((playerCooldown[player] ?: return false) > 0) return false
        skill(player)
        startCooldown(player)
        return true
    }

    open fun skill(player: Player) {}
}