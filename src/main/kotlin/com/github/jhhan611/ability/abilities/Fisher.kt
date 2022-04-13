package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.abilities.Assassin.hasAbility
import com.github.jhhan611.ability.abilities.LuckyGame.hasAbility
import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.manager.Trigger
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.isSword
import com.github.jhhan611.ability.utils.setDisplayName
import com.github.jhhan611.ability.utils.swords
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

object Fisher : Ability() { // 미완성
    private var isGroggy = mutableListOf<Player>()
    private var lastCaughtCount = mutableMapOf<Player, Int>()
    private var lastCaughtPlayer = mutableMapOf<Player, Player>()

    override fun onLoad() {
        addTrigger(Groggy)
    }

    override fun onAdd(player: Player) {
        var item = ItemStack(Material.FISHING_ROD)
        item.itemMeta.isUnbreakable = true
        player.inventory.addItem(item)
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        val player = e.player as? Player ?: return
        if (!player.hasAbility()) return
        val caught = e.caught as? Player ?: return

        if (Random.nextDouble() < 0.2f * (if(isGroggy.contains(player)) 5f else 1f)) {
            caught.velocity.add(Vector(0f, 4f / (if(isGroggy.contains(player)) 8f else 1f), 0f))
            if (isGroggy.contains(player)) {
                if (lastCaughtPlayer[player] == caught) {
                    lastCaughtCount[player] = lastCaughtCount.getOrDefault(player, 0) + 1
                }
                else {
                    lastCaughtPlayer[player] = caught
                    lastCaughtCount[player] = 1
                }

                if (lastCaughtCount.getOrDefault(player, 0) >= 3) {
                    lastCaughtPlayer.remove(player)
                    lastCaughtCount.remove(player)
                    //TODO: 기절 효과 3초 부여
                }
            }
        }
    }

    object Groggy : Trigger("그로기", 30) {
        override fun skill(player: Player) {
            isGroggy.add(player)
            plugin!!.server.scheduler.scheduleSyncDelayedTask(plugin!!, {
                isGroggy.remove(player)
                lastCaughtPlayer.remove(player)
                lastCaughtCount.remove(player)
            }, 200)
        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (!e.player.hasAbility()) return
        if (item == null) return
        if (item.type == Material.IRON_INGOT) {

        } else if (item.type.isSword) {
            Groggy.useSkill(e.player)
        }
    }
}