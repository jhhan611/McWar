package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.Ability
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object Cigar : Ability() {
    override fun onAdd(player: Player) {
        player.inventory.apply {
            player.inventory.apply {
                addItem(ItemStack(Material.IRON_INGOT))
            }
        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if((item ?: return).type != Material.IRON_INGOT) return

    }

}