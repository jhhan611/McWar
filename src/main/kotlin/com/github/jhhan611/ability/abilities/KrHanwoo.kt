package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.abilities.Assassin.hasAbility
import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.manager.Trigger
import com.github.jhhan611.ability.utils.swords
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull

object KrHanwoo : Ability() {
    val troy = mutableListOf<Player>()

    override fun onAdd(player: Player) {
        troy.add(player)
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    override fun onLoad() {
        addTrigger(BackDoor)
    }

    object BackDoor : Trigger("백도어", 60) {
        override fun skill(player: Player) {
            player.location.world.spawnParticle(Particle.TOTEM, player.location.add(0.0, 0.5, 0.0), 250, 7.0, 0.0, 7.0, 0.0)
            player.location.getNearbyPlayers(7.0).filter { it != player }.forEach {
                val offhand = it.inventory.itemInOffHand
                val content = it.inventory.storageContents.toMutableList()
                content.add(offhand)
                content.shuffle()
                it.inventory.storageContents = content.subList(0, content.size-2).toTypedArray()
                it.inventory.setItemInOffHand(content.last())
            }
        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (!e.player.hasAbility()) return
        if (item == null) return
        if (item.type == Material.IRON_INGOT) BackDoor.useSkill(e.player)
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if (!troy.contains(e.player)) return
        troy.remove(e.player)
        val lastDamageEvent = e.entity.lastDamageCause as? EntityDamageByEntityEvent ?: return
        if (lastDamageEvent.isCancelled) return

        val damager = lastDamageEvent.damager as? Player ?: return

        damager.location.createExplosion(4F)

        if (!damager.isDead) troy.add(damager)
    }
}