package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object Romance : Ability() { // 완성

    @EventHandler
    fun onDamaged(e: EntityDamageByEntityEvent) { // 피격
        val player = e.entity as? Player ?: return
        if (!player.hasAbility()) return
        if (e.damager !is Player) {
            if (e.damager is Projectile) {
                val projectile = e.damager as Projectile
                if (projectile.shooter !is Player) return
            } else return
        }
        e.damage = 4.0
    }

    @EventHandler
    fun onDamage(e : EntityDamageByEntityEvent) { // 가격
        if (e.damager is Player) {
            val player = e.damager as Player
            if (!player.hasAbility()) return
        } else if (e.damager is Projectile) {
            val projectile = e.damager as Projectile
            if ((projectile.shooter as? Player)?.hasAbility() != true) return
        }
        e.damage = 4.0
    }
}