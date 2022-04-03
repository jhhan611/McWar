package com.github.jhhan611.ability.abilities

import com.github.jhhan611.ability.MachangWars
import com.github.jhhan611.ability.MachangWars.projectileDamage
import com.github.jhhan611.ability.plugin
import net.kyori.adventure.sound.Sound
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

object Matan : MachangWars.Ability() {
    private var ammo = mutableMapOf<Player, Int>()
    private var reload = mutableMapOf<Player, Int>()
    private var reloadSchedulerID = mutableMapOf<Player, Int>()
    private var bossbar = mutableMapOf<Player, org.bukkit.boss.BossBar>()

    override fun onDelete(player: Player) {
        bossbar[player]?.removePlayer(player)
    }

    override fun onAdd(player: Player) {
        plugin!!.logger.info("Added ${abilityType.abilityName} to ${player.name}")

        player.inventory.apply{
            addItem(ItemStack(Material.BOW))
            addItem(ItemStack(Material.ARROW))
        }

        bossbar[player] = Bukkit.createBossBar("탄창", BarColor.WHITE, BarStyle.SOLID)
        bossbar[player]!!.addPlayer(player)
        player.setAmmo(5, player.health)
        reload[player] = 0
        reloadSchedulerID[player] = 0
    }

    override fun onLeftClick(item: ItemStack?, e: PlayerInteractEvent) {
        if((item ?: return).type != Material.BOW) return
        if(reload[e.player] ?: return > 0) return

        e.player.reloadAmmo()
    }

    private fun Player.setAmmo(to : Int, health : Double) {
        val bar = (bossbar[player] ?: return)
        ammo[this] = to
        bar.progress = 0.2*to
        bar.setTitle("탄창: ${to}/5")
        bar.color = BarColor.WHITE
        if (health <= 4) {
            this.playSound(this.location, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f)
            bar.setTitle("탄창: ${to}/5 - ${ChatColor.RED}최후의 한발")
            bar.color = BarColor.RED
        }
    }

    private fun Player.reloadAmmo() {
        if (this.health <= 4) return

        reload[this] = 50
        this.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 50, 100))
        val reloadBar = Bukkit.createBossBar("장전중..", BarColor.BLUE, BarStyle.SOLID)
        reloadBar.addPlayer(this)
        reloadSchedulerID[this] = plugin!!.server.scheduler.scheduleSyncRepeatingTask(plugin!!, {
            var currReload = reload[this] ?: return@scheduleSyncRepeatingTask
            reloadBar.progress = (50-currReload)*0.02
            currReload--
            reload[this] = currReload
            if (currReload <= 0) {
                if (this.health <= 4) this.setAmmo(1, this.health)
                else this.setAmmo(5, this.health)
                reloadBar.removePlayer(this)
                plugin!!.server.scheduler.cancelTask(reloadSchedulerID[this]!!)
                return@scheduleSyncRepeatingTask
            }
        }, 1, 1)
    }

    @EventHandler
    fun onArrowShoot(e: EntityShootBowEvent) {
        val player = (e.entity as? Player) ?: return
        if (!player.hasAbility()) return

        e.isCancelled = true

        if (reload[player] ?: return > 0) return
        if (ammo[player] == 0) {
            player.reloadAmmo()
            return
        }

        if (e.force != 1f) return

        player.setAmmo((ammo[player] ?: return) - 1, player.health)

        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction

        val raytrace = player.world.rayTrace(eyeLocation, direction, 50.0, FluidCollisionMode.NEVER, true, 0.0) { entity: Entity -> entity as? Player != player }

        val pos1 = player.eyeLocation.toVector()
        val pos2 = raytrace?.hitPosition ?: player.eyeLocation.add(player.eyeLocation.direction.multiply(50)).toVector()

        val distance: Double = pos1.distance(pos2)
        val vector: Vector = pos2.clone().subtract(pos1).normalize()
        var length = 0.0
        while (length < distance) {
            player.world.spawnParticle(Particle.SMOKE_NORMAL, pos1.toLocation(player.world), 3, 0.0, 0.0, 0.0, 0.0, null)
            player.world.spawnParticle(Particle.CRIT, pos1.toLocation(player.world), 3, 0.0, 0.0, 0.0, 0.0, null)
            length += 1
            pos1.add(vector)
        }
        player.playSound(player.location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f)

        if (raytrace?.hitEntity != null) {
            if (raytrace.hitEntity?.type == EntityType.PLAYER) {
                val target = raytrace.hitEntity as Player
                if (player.health <= 4) {
                    if (target.health <= 6) target.damage(1234.0, player)
                    else if (6 < target.health && target.health <= 10) target.projectileDamage((6 + (e.bow?.enchantments?.get(Enchantment.ARROW_DAMAGE) ?: 0).toDouble() / 2))
                    else if (10 < target.health && target.health <= 15) target.projectileDamage((4 + (e.bow?.enchantments?.get(Enchantment.ARROW_DAMAGE) ?: 0).toDouble() / 2))
                    else target.projectileDamage((3 + (e.bow?.enchantments?.get(Enchantment.ARROW_DAMAGE) ?: 0).toDouble() / 2))
                    player.setAmmo(1, player.health)
                } else {
                    target.projectileDamage((3 + (e.bow?.enchantments?.get(Enchantment.ARROW_DAMAGE) ?: 0).toDouble() / 2))
                }
                player.playSound(player.location, org.bukkit.Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f)
            }
        }
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        val player = (e.entity as? Player) ?: return
        if (!player.hasAbility()) return

        if (player.health-e.damage <= 4) player.setAmmo(1, player.health-e.damage)
    }

    @EventHandler
    fun onHeal(e: EntityRegainHealthEvent) {
        val player = (e.entity as? Player) ?: return
        if (!player.hasAbility()) return

        if (player.health+e.amount > 4) player.setAmmo(ammo[player] ?: return, player.health+e.amount)
    }
}