package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.manager.Trigger
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.swords
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.EulerAngle
import kotlin.math.PI

object Assassin : Ability() {
    val target = mutableMapOf<Player, Player>()

    override fun onAdd(player: Player) {
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    object DaggerThrow : Trigger("단검 투척", 40) {
        override fun skill(player: Player) {
            val hitPlayer = throwSword(player)
        }

        fun throwSword(player: Player) : Player? {
            val armorStand = player.world.spawn(player.location, ArmorStand::class.java).apply {
                isInvisible = true; setGravity(false); isVisible = false; isInvulnerable = true; isSmall = true; isMarker = true

                setRotation(90f, 0f)
                rightArmPose = EulerAngle(0.0, PI*3/2, 0.0)
                equipment.setItemInMainHand(ItemStack(player.inventory.itemInMainHand))

                teleport(player.location.add(0.0, 1.0, 0.0))
            }
            val direction = player.eyeLocation.toVector()

            player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.7f, 1f)

            var dist = 0
            var hitPlayer: Player? = null
            object : BukkitRunnable() {
                override fun run() {
                    if (dist == 40) return cancel()
                    dist++
                    armorStand.teleport(armorStand.location.add(direction.multiply(0.25)))

                    Bukkit.getOnlinePlayers().forEach {
                        if (armorStand.location.distance(it.location) < 1) {
                            hitPlayer = it
                            return cancel()
                        }
                    }
                }
            }

            return hitPlayer
        }
    }

    object SwitchTarget : Trigger("타겟 변경", 25) {
        override fun skill(player: Player) {

        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (!e.player.hasAbility()) return
        if (item == null) return
        if (item.type == Material.IRON_INGOT) {

        } else if (swords.contains(item.type)) {
            DaggerThrow.useSkill(e.player)
        }
    }
}