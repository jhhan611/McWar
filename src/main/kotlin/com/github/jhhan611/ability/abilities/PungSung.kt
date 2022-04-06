package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.Ability
import com.github.jhhan611.ability.playerDamage
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.simulateProjectileDamage
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object PungSung : Ability() {
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

    var damaged = mutableListOf<Entity>() // block event loop

    @EventHandler
    fun onAttack(e: EntityDamageByEntityEvent) {
        if(damaged.remove(e.entity)) return

        val player = e.damager as? Player ?: return
        if (!player.hasAbility()) return
        e.isCancelled = true
        e.damage += (hair[player] ?: 0).toDouble()/2

        damaged.add(e.entity)
        if (e.cause == EntityDamageEvent.DamageCause.PROJECTILE) (e.entity as LivingEntity).simulateProjectileDamage(e.damage.toFloat(), player)
        else (e.entity as LivingEntity).playerDamage(e.damage.toFloat(), player)


        val target = e.entity as? Player ?: return
        if (target.isDead) {
            val currHair = hair[player] ?: return
            player.setHair(currHair + listOf(1,1,1,1,1,1,2,2,2,3).shuffled().first())
            player.playSound(player.location, org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        }
    }
}