package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object Amogus : Ability() {

    override fun onAdd(player: Player) {
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if ((item ?: return).type != Material.IRON_INGOT) return
        //투명화 상태에서는 갑옷, 무기, 상태 입자가 보이지 않습니다. 상태 입자는 보여도 되는데 이펙트 입자는 안보이게
    }
}