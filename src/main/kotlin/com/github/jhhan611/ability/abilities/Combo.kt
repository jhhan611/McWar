package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.critEffect
import com.github.jhhan611.ability.utils.genericDamage
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable

object Combo : Ability() {

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        if (!player.hasAbility()) return

        object: BukkitRunnable() {
            override fun run(){
                val victim = e.entity as? LivingEntity ?: return cancel()
                victim.noDamageTicks = 0
                victim.genericDamage((e.damage * 0.4).toFloat())
                victim.world.playSound(victim.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f)
                victim.critEffect(true)
            }
        }.runTaskLater(plugin!!, 7)
    }
}