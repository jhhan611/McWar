package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.MachangWars
import com.github.jhhan611.ability.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object Romance : MachangWars.Ability() {

    @EventHandler
    fun onDamaged(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        if (!player.hasAbility()) return
        e.damage = 4.0
    }

    @EventHandler
    fun onDamage(e : EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        if (!player.hasAbility()) return
        e.damage = 4.0
    }
}