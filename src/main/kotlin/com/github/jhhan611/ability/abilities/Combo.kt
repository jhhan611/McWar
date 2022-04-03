package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.MachangWars
import com.github.jhhan611.ability.plugin
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object Combo : MachangWars.Ability() {

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        if (!player.hasAbility()) return

        var taskID = 0
        taskID = plugin!!.server.scheduler.scheduleSyncDelayedTask(plugin!!, {
            val victim = e.entity as? LivingEntity
            if (victim == null) {
                plugin!!.server.scheduler.cancelTask(taskID)
                return@scheduleSyncDelayedTask
            }
            victim.damage(e.damage * 0.4f)
            victim.world.playSound(victim.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f)
            victim.world.spawnParticle(Particle.CRIT, victim.eyeLocation, 3, 0.1, 0.1, 0.1, 1)
        }, 3)
    }
}