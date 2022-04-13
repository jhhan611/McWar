package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.manager.Actionbar
import com.github.jhhan611.ability.manager.Trigger
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.isSword
import com.github.jhhan611.ability.utils.spawnArmorStand
import com.github.jhhan611.ability.utils.swords
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.EulerAngle
import kotlin.math.PI

object Assassin : Ability() {
    val target = mutableMapOf<Player, Player>()

    override fun onLoad() {
        addTrigger(DaggerThrow, SwitchTarget)
    }

    override fun onAdd(player: Player) {
        try {
            target[player] = Bukkit.getOnlinePlayers().filter { it != player }.random()
        } catch (e: NoSuchElementException) {
            target.remove(player)
        }

        player.inventory.addItem(ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("${ChatColor.GOLD}현재 타겟 ${ChatColor.DARK_GRAY}: ${ChatColor.WHITE}${target[player]}"))
                lore(listOf(Component.text("${ChatColor.GRAY}클릭해서 타겟 변경 (25s)")))
            }
        })
        SwitchTarget.startCooldown(player)

        object : BukkitRunnable() {
            override fun run() {
                if (!player.hasAbility()) return cancel()
                if (target[player] == null) {
                    Actionbar.prefix.remove(player)
                } else {
                    val loc = target[player]?.location ?: return
                    val x = loc.x; val y = loc.y; val z = loc.z
                    Actionbar.prefix[player] =
                        "${ChatColor.GOLD}현재 타겟 ${ChatColor.DARK_GRAY}: ${ChatColor.WHITE}${target[player]} ${ChatColor.GRAY}(${ChatColor.WHITE}$x, $y, $z${ChatColor.GRAY})"
                }
            }
        }.runTaskTimer(plugin!!, 1, 1)
    }

    object DaggerThrow : Trigger("단검 투척", 1) { //TODO CHANGE TO 40
        override fun skill(player: Player) {
            val hitPlayer = throwSword(player)
        }

        private fun throwSword(player: Player): Player? {
            val direction = player.eyeLocation.direction
            val item = player.inventory.itemInMainHand
            val armorStand =
                spawnArmorStand(player.location.add(0.0, 1.0, 0.0), invisible = true, marker = true).apply {
                    isSmall = true
                    setGravity(false)
                    isInvulnerable = true
                    rightArmPose = EulerAngle(player.eyeLocation.pitch.toDouble() * PI / 180.0, 0.0, 0.0)
                    equipment.setItemInMainHand(ItemStack(item))
                }

            player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.7f, 1f)

            var dist = 0
            var hitPlayer: Player? = null
            object : BukkitRunnable() {
                override fun run() {
                    val swordLoc = armorStand.location.clone().add(
                        armorStand.location.direction.rotateAroundY(-90 * PI / 180.0).multiply(0.2)
                    )
                    if (dist == 40) return breakSword(swordLoc)
                    if (!armorStand.location.world.getBlockAt(swordLoc).type.isAir) return breakSword(swordLoc)
                    dist++
                    armorStand.teleport(armorStand.location.add(direction))
                    armorStand.location.world.spawnParticle(Particle.SMOKE_NORMAL, armorStand.location, 1)

                    Bukkit.getOnlinePlayers().filter { it != player }.forEach {
                        if (armorStand.location.distance(it.location) < 1) {
                            hitPlayer = it
                            return breakSword(swordLoc)
                        }
                    }
                }

                fun breakSword(loc: Location) {
                    armorStand.world.spawnParticle(Particle.ITEM_CRACK, loc, 50, 0.3, 0.3, 0.3, 0.0, item)
                    armorStand.remove()
                    cancel()
                }
            }.runTaskTimer(plugin!!, 1, 1)

            return hitPlayer
        }
    }

    object SwitchTarget : Trigger("타겟 변경", 25) {
        override fun skill(player: Player) {
            try {
                target[player] = Bukkit.getOnlinePlayers().filter { it != player && it != target[player] }.random()
            } catch (e: NoSuchElementException) {
                target.remove(player)
            }

            player.inventory.itemInMainHand.apply {
                itemMeta = itemMeta.apply {
                    displayName(Component.text("${ChatColor.BOLD}${ChatColor.GOLD}현재 타겟 ${ChatColor.RESET}${ChatColor.DARK_GRAY}: ${ChatColor.WHITE}${target[player]?.name}"))
                    lore(listOf(Component.text("${ChatColor.GRAY}클릭해서 타겟 변경 (25s)")))
                }
            }
            player.sendMessage("${ChatColor.BOLD}${ChatColor.GOLD}타겟 변경 ${ChatColor.RESET}${ChatColor.DARK_GRAY}: ${ChatColor.YELLOW}타겟이 ${target[player]?.name}로 변경됐습니다.")
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f)
        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (!e.player.hasAbility()) return
        if (item == null) return
        if (item.type == Material.PAPER) {
            SwitchTarget.useSkill(e.player)
        } else if (item.type.isSword) {
            DaggerThrow.useSkill(e.player)
        }
    }
}