package com.github.jhhan611.ability

import com.github.jhhan611.ability.abilities.*
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

//TODO: rank별 확률 적용

object MachangWars {
    val playerAbility = mutableMapOf<Player, MutableList<AbilityType>>()

    enum class AbilityType(val abilityName: String, val rank: Char, val abilityObject: Ability) {
        PUNGSUNG("풍성", 'L', PungSung),
        MATAN("마탄의 사수", 'S', Matan),
        AMOGUS("아모구스", 'S', Amogus),
        LUCKYGAME("운빨망겜", 'S', LuckyGame),
        ROMANCE("낭만", 'A', Romance),
        COMBO("콤보", 'A', Combo),
        CIGAR("시가", 'C', Cigar)
    }

    fun loadAbilities(plugin: Plugin) { // 모든 능력들의 메소드 loadAbility() 발동
        val abilities = AbilityType.values()
        plugin.logger.info("Loading ${abilities.size} abilities")

        abilities.forEach {
            it.abilityObject.loadAbility(it)
        }
    }

    fun addAbility(player: Player, abilityType: AbilityType) { // 플레이어에게 능력 부여
        if (playerAbility[player]?.contains(abilityType) == true) {
            player.sendMessage("${ChatColor.RED}player already has ability ${abilityType.name}")
            return
        }

        playerAbility[player] ?: run {
            playerAbility[player] = mutableListOf(abilityType)
            abilityType.abilityObject.onAdd(player)
            return
        }
        playerAbility[player]!!.add(abilityType)
        abilityType.abilityObject.onAdd(player)
    }

    fun removeAbility(player: Player, abilityType: AbilityType) { // 플레이어에게 능력 가져감
        if (playerAbility[player]?.contains(abilityType) != true) {
            player.sendMessage("${ChatColor.RED}player doesn't have ability ${abilityType.name}")
            return
        }

        playerAbility[player]!!.remove(abilityType)
        abilityType.abilityObject.onDelete(player)
    }

    fun startGame() { // 게임 시작
        val players = Bukkit.getPluginManager().getPlugin("MachangWars")!!.server.onlinePlayers
        players.forEach {
            val ability = AbilityType.values().toList().shuffled().first()
            addAbility(it, ability)
            it.sendMessage("${ChatColor.GREEN}게임이 시작되었습니다!")
            it.sendMessage("${ChatColor.YELLOW}당신의 능력은 ${ChatColor.GOLD}${ability.abilityName}${ChatColor.YELLOW}입니다.")
        }
    }

    fun Player.getAbilities(): MutableList<AbilityType> { // 플레이어의 능력들 반환, 능력이 없으면 빈 리스트
        playerAbility[this] ?: return mutableListOf()
        return playerAbility[this]!!
    }

    class MainListener : Listener {
        private val drop = mutableSetOf<Player>()

        @EventHandler
        fun onInteraction(e: PlayerInteractEvent) {
            if (drop.remove(e.player)) {
                e.player.sendMessage("blocked")
                return
            }

            val abilities = playerAbility[e.player] ?: return

            if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR)
                abilities.forEach {
                    it.abilityObject.onRightClick(e.item, e)
                }

            if (e.action == Action.LEFT_CLICK_BLOCK || e.action == Action.LEFT_CLICK_AIR)
                abilities.forEach {
                    it.abilityObject.onLeftClick(e.item, e)
                }
        }

        @EventHandler
        fun onThrow(e: PlayerDropItemEvent) {
            drop.add(e.player)
            plugin?.let {
                Bukkit.getScheduler().scheduleSyncDelayedTask(it) {
                    drop.remove(e.player)
                }
            }
        }

        @EventHandler
        fun onDeath(e: PlayerDeathEvent) {
            Bukkit.getServer().onlinePlayers.forEach { p ->
                p.sendMessage(
                    "${ChatColor.GOLD}${e.player.name}${ChatColor.YELLOW}의 능력은 ${ChatColor.GOLD}${
                        e.player.getAbilities().joinToString { it.name }
                    }${ChatColor.YELLOW}이었습니다."
                )
            }
        }

        @EventHandler
        fun onDisconnect(e: PlayerQuitEvent) {
            e.player.getAbilities().forEach { it.abilityObject.onDelete(e.player) }
            playerAbility[e.player]?.clear() //@Jhun wil this work?  preventing ConcurrentException
        }
    }

    fun Player.meleeDamage(damage: Double, damager: Player? = null) {
        var epf = (this.inventory.helmet?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.chestplate?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.leggings?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.boots?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0)
        if (epf > 20) epf = 20
        this.damage(damage * (1 - epf.toDouble() / 25), damager)
    }

    fun Player.projectileDamage(damage: Double, damager: Player? = null) {
        var epf = (this.inventory.helmet?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.chestplate?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.leggings?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.boots?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.helmet?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0) * 2 +
                (this.inventory.chestplate?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0) * 2 +
                (this.inventory.leggings?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0) * 2 +
                (this.inventory.boots?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0) * 2
        if (epf > 20) epf = 20
        this.damage(damage * (1 - epf.toDouble() / 25), damager)
    }
}