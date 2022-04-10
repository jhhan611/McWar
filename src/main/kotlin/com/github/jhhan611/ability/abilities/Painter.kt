package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.manager.Ability
import com.github.jhhan611.ability.manager.Trigger
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.hideArmorFor
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

object Painter : Ability() { // 아마 완성
    private val colorZones = mutableListOf<ColorZone>()
    private val sprayZones = mutableListOf<SprayZone>()

    override fun onAdd(player: Player) {
        player.inventory.addItem(ItemStack(Material.IRON_INGOT))
    }

    override fun onLoad() {
        addTrigger(Painting, ColorSpray)

        object : BukkitRunnable() {
            override fun run() {
                for (colorZone in colorZones) {
                    colorZone.time--
                    val world = colorZone.location.world
                    world.spawnParticle(
                        Particle.DUST_COLOR_TRANSITION, colorZone.location, 30, 3.0, 0.0, 3.0,
                        Particle.DustTransition(colorZone.type.color, colorZone.type.color, 2f)
                    )
                    val entities = colorZone.location.getNearbyLivingEntities(5.0, 1.0) { it == colorZone.player && it.type == EntityType.PLAYER }
                    if (entities.isEmpty()) continue
                    entities.forEach { it.addPotionEffect(colorZone.type.effect) }
                } // 여기가 문제인

                for (sprayZone in sprayZones) {
                    sprayZone.time--
                    val world = sprayZone.location.world
                    world.spawnParticle(
                        Particle.DUST_COLOR_TRANSITION, sprayZone.location, 40, 5.0, 5.0, 5.0,
                        Particle.DustTransition(Color.WHITE, Color.WHITE, 5f)
                    )
                    val players = sprayZone.location.getNearbyLivingEntities(8.0, 8.0) { it.type == EntityType.PLAYER }
                    if (players.isEmpty()) continue
                    for (player in players) {
                        if (player == sprayZone.player)
                            Bukkit.getOnlinePlayers().forEach { player.hideArmorFor(it) }
                        else {
                            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 10, 0, false, false))
                            player.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 10, 0, false, false))
                        }
                    }
                }

                colorZones.removeIf { it.time < 1 }
                sprayZones.removeIf { it.time < 1 }
            }
        }.runTaskTimer(plugin!!, 0, 1)
    }

    object Painting : Trigger("페인팅", 60) {
        override fun skill(player: Player) {
            colorZones.add(ColorZone(player, player.location.add(Vector(0f, 0.3f, 0f)), ColorType.values().random(), 2400))
        }
    }

    object ColorSpray : Trigger("컬러 스프레이", 120) {
        override fun skill(player: Player) {
            sprayZones.add(SprayZone(player, player.location.add(Vector(0f, 0.3f, 0f)), 400))
        }
    }

    override fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (!e.player.hasAbility()) return
        if (item == null) return
        if (item.type == Material.IRON_INGOT) {
            Painting.useSkill(e.player)
        }
    }

    override fun onLeftClick(item: ItemStack?, e: PlayerInteractEvent) {
        if (!e.player.hasAbility()) return
        if (item == null) return
        if (item.type == Material.IRON_INGOT) {
            ColorSpray.useSkill(e.player)
        }
    }

    data class ColorZone(val player: Player, val location: Location, val type: ColorType, var time: Int)

    data class SprayZone(val player: Player, val location: Location, var time: Int)

    enum class ColorType(val color: Color, val effect: PotionEffect) {
        RED(Color.RED, PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5, 0, false, false)),
        YELLOW(Color.YELLOW, PotionEffect(PotionEffectType.REGENERATION, 5, 0, false, false)),
        BLUE(Color.BLUE, PotionEffect(PotionEffectType.SPEED, 5, 0, false, false))
    }
}