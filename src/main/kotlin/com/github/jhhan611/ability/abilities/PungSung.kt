package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.MachangWars
import com.github.jhhan611.ability.plugin
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object PungSung : MachangWars.Ability() {
    private var hair = mutableMapOf<Player, Int>()
    private var bossbar = mutableMapOf<Player, org.bukkit.boss.BossBar>()

    override fun onDelete(player: Player) {
        bossbar[player]?.removePlayer(player)
    }

    override fun onAdd(player: Player) {
        bossbar[player] = Bukkit.createBossBar("머리카락", BarColor.WHITE, BarStyle.SOLID)
        bossbar[player]!!.addPlayer(player)
        player.setHair(0)

        var taskID = 0
        var time = 1200
        taskID = plugin!!.server.scheduler.scheduleSyncRepeatingTask(plugin!!, {
            if (!player.hasAbility()) {
                plugin!!.server.scheduler.cancelTask(taskID)
                return@scheduleSyncRepeatingTask
            }

            time--
            if (time <= 0) {
                val currHair = hair[player] ?: return@scheduleSyncRepeatingTask
                player.setHair(currHair+1)
                player.playSound(player.location, org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                time = 1200

            }
            bossbar[player]?.progress = (1200-time).toDouble()/1200

        }, 1, 1)
    }

    private fun Player.setHair(to: Int) {
        val bar = (bossbar[player] ?: return)
        hair[this] = to
        bar.setTitle("머리카락: $to")
    }

    @EventHandler
    fun onAttack(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        if (!player.hasAbility()) return
        e.damage += (hair[player] ?: 0).toDouble()/2

        val target = e.entity as? Player ?: return
        if (target.health - e.damage <= 0) {
            val currHair = hair[player] ?: return
            player.setHair(currHair + listOf(1,1,1,1,1,1,2,2,2,3).shuffled().first())
            player.playSound(player.location, org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        }
    }
}