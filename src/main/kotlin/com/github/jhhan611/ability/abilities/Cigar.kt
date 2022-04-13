package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.abilities.Painter.hasAbility
import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.manager.Trigger
import com.github.jhhan611.ability.utils.debuff
import com.github.jhhan611.ability.utils.getNearbyEuclideanEntities
import com.github.jhhan611.ability.utils.toComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import java.util.*

object Cigar : Ability() {

    override fun onAdd(player: Player) {
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    @EventHandler
    fun onPotionEffect(e: EntityPotionEffectEvent) {
        val player = e.entity as? Player ?: return
        if (!player.hasAbility()) return
        if (debuff.contains(e.newEffect?.type ?: return)) e.isCancelled = true
    }

    object Smoke : Trigger("흡연", 30) {
        override fun skill(player: Player) {
            player.damage(4.0)
            val location = player.eyeLocation
            location.world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 200, 0.1, 0.1, 0.1, 0.1)
            var players = location.getNearbyEuclideanEntities(7.0).filter { it.type === EntityType.PLAYER && it != player}
            players = players.map { it as Player }
            players.forEach { it.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 300, 0, false, false))}//did
        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (!e.player.hasAbility()) return
        if (item == null) return
        if (item.type == Material.IRON_INGOT) {
            Smoke.useSkill(e.player)
        }
    }

}