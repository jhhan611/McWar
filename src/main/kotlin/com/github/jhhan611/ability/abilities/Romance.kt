package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.utils.truePlayerDamage
import com.github.jhhan611.ability.utils.trueProjectileDamage
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*

object Romance : Ability() { // 보류

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
//        val entity = e.entity as? Player ?: return
//        val damager = e.damager as? Player ?: (e.damager as? Projectile ?: return).shooter as? Player ?: return
//        if (entity.hasAbility() || damager.hasAbility()) {
//            val newHealth = (entity.health - 4).coerceAtLeast(0.0)
//            if (newHealth <= 0) {
//                e.
//            } else
//                entity.health = newHealth
//        }
    }
}