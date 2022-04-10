package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.plugin
import net.minecraft.world.entity.projectile.EntityFireball
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.metadata.MetadataValueAdapter
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*

object Star : Ability() {
    private val r = Random()

    override fun onAdd(player: Player) {
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.entity !is Player) return
        if (!(e.entity as Player).hasAbility()) return
        if (e.damager !is Fireball) return
        if (e.damager.getMetadata("StarOwner")[0].asString() != e.entity.uniqueId.toString()) return
        e.isCancelled = true
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (item == null) return
        if (item.type != Material.IRON_INGOT) return
        if (!e.player.isSneaking) return
        var tick = 10
        var left = 20
        object : BukkitRunnable() {
            override fun run() {
                if (!e.player.isOnline) return cancel()
                if (!e.player.isSneaking) return cancel()
                if (tick == 1) {
                    val loc = e.player.location.add((r.nextInt(4) - 2).toDouble(), 50.0, (r.nextInt(4) - 2).toDouble())
                    val fireball = loc.world.spawnEntity(loc, EntityType.FIREBALL) as Fireball
                    fireball.yield = 0.0F
                    fireball.setIsIncendiary(false)
                    fireball.direction = Vector(0, -5, 0)
                    fireball.velocity = Vector(0, -5, 0)
                    fireball.isGlowing = true
                    fireball.setMetadata("StarOwner", FixedMetadataValue(plugin!!, e.player.uniqueId.toString()))
                    left--;
                    if (left == 0) return cancel()
                    tick = 10
                } else
                    tick--
            }

        }.runTaskTimer(plugin!!, 0, 1)
    }
}