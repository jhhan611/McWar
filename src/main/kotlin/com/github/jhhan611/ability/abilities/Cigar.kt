package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.inventory.ItemStack

object Cigar : Ability() {
    override fun onAdd(player: Player) {
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    fun onPotionEffect(e: EntityPotionEffectEvent) {
        
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if((item ?: return).type != Material.IRON_INGOT) return
    }

}