package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.plugin
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.random.Random

object LuckyGame : Ability() { // 보류
    private var isLuckyCoins = mutableSetOf<Player>()
    private var isSkillUnAbles = mutableSetOf<Player>()
    private var LuckyCoinCoolTime = mutableMapOf<Player, Long>()

    override fun onAdd(player: Player) {
        player.inventory.apply {
            player.inventory.apply {
                addItem(ItemStack(Material.IRON_INGOT))
            }
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) { // 인생한방
        val player = e.damager as? Player ?: return
        if (!player.hasAbility()) return
        val victim = e.entity as? Player ?: return

        if (isSkillUnAbles.contains(player)) return

        if (Random.nextDouble() < 0.05f * (if (isLuckyCoins.contains(player)) 6f else 1f)) {
            victim.health = 0.0
            victim.world.spawnParticle(
                Particle.BLOCK_CRACK,
                victim.eyeLocation,
                10,
                Material.GOLD_BLOCK.createBlockData()
            )
            isLuckyCoins.remove(player)
            return
        }

        if (isLuckyCoins.contains(player)) {
            isSkillUnAbles.add(player)
            player.world.playSound(player.eyeLocation, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
            player.world.spawnParticle(Particle.EXPLOSION_LARGE, player.eyeLocation, 1)
            plugin!!.server.scheduler.scheduleSyncDelayedTask(plugin!!, {
                isSkillUnAbles.remove(player)
            }, 3000)
            isLuckyCoins.remove(player)
        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if ((item ?: return).type != Material.IRON_INGOT) return

        if (System.currentTimeMillis() - LuckyCoinCoolTime.getOrDefault(e.player, 0L) < 2000) return

        isLuckyCoins.add(e.player)
        LuckyCoinCoolTime[e.player] = System.currentTimeMillis()

        object : BukkitRunnable() {
            override fun run() {
                if (!isLuckyCoins.contains(e.player)) return cancel()

                val dustTransition = Particle.DustTransition(Color.YELLOW, Color.YELLOW, 1f)
                e.player.world.spawnParticle(
                    Particle.DUST_COLOR_TRANSITION,
                    e.player.eyeLocation.add(Vector(0, 1, 0)),
                    3,
                    0.2,
                    0.2,
                    0.2,
                    dustTransition
                )
            }
        }.runTaskTimer(plugin!!, 0, 1)
    }
}