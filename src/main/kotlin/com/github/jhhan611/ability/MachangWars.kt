package com.github.jhhan611.ability

import com.github.jhhan611.ability.abilities.*
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

//TODO: rank별 확률 적용

object MachangWars {
    val playerAbility = mutableMapOf<Player, MutableList<AbilityType>>()

    enum class AbilityType(val abilityName: String, val rank : Char, val abilityObject : Ability) {
        MATAN("마탄의 사수", 'S', Matan),
        AMOGUS("아모구스", 'S', Amogus),
        ROMANCE("낭만", 'A', Romance),
        COMBO("콤보", 'A', Combo),
        LUCKYGAME("운빨망겜", 'S', LuckyGame),
        PUNGSUNG("풍성", 'L', PungSung)
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

    fun Player.getAbilities() : MutableList<AbilityType> { // 플레이어의 능력들 반환, 능력이 없으면 빈 리스트
        playerAbility[this] ?: return mutableListOf()
        return playerAbility[this]!!
    }

    open class Ability : Listener {
        lateinit var abilityType: AbilityType

        fun Player.hasAbility() : Boolean { // 플레이어가 오브젝트에 해당하는 능력이 있는기? ex) Matan에서 이 함수를 실행시키면 플레이어가 AbilityType.MATAN을 갖고 있는지 반환함
            if((playerAbility[this] ?: return false).contains(abilityType)) return true
            return false
       }

        fun loadAbility(type: AbilityType) {
            abilityType = type
            plugin!!.server.pluginManager.registerEvents(type.abilityObject, plugin!!)
            onLoad()
        }

        open fun onLoad() {} // 플러그인 로드에 할 것들

        open fun onAdd(player: Player) {} // 플레이어가 능력을 얻었을 때 할 것들
        open fun onDelete(player: Player) {} // 플레이어가 능력을 삭제당했을 때 할 것들

        open fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) { // 플레이어 우클릭 시 할 것들
            if (item == null) return
            if (item.type == Material.IRON_INGOT) trigger1(e.player)
        }

        open fun onLeftClick(item: ItemStack?, e: PlayerInteractEvent) { // 플레이어 좌클릭 시 할 것들
            if (item == null) return
            if (item.type == Material.IRON_INGOT) trigger2(e.player)
        }

        open fun trigger1(player: Player) {} // 철 우클릭 함수
        open fun trigger2(player: Player) {} // 철 좌클릭 함수
    }

    class MainListener : Listener {
        @EventHandler
        fun onInteraction(e: PlayerInteractEvent) {
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
        fun onDeath(e: PlayerDeathEvent) {
            Bukkit.getServer().onlinePlayers.forEach { p ->
                p.sendMessage("${ChatColor.GOLD}${e.player.name}${ChatColor.YELLOW}의 능력은 ${ChatColor.GOLD}${e.player.getAbilities().joinToString { it.name }}${ChatColor.YELLOW}이었습니다.")
            }
        }

        @EventHandler
        fun onDisconnect(e: PlayerQuitEvent) {
            e.player.getAbilities().forEach {
                playerAbility[e.player]?.remove(it)
                it.abilityObject.onDelete(e.player)
            }
        }
    }

    fun Player.meleeDamage(damage: Double) {
        var epf = (this.inventory.helmet?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.chestplate?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.leggings?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.boots?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0)
        if (epf > 20) epf = 20
        this.damage(damage*(1-epf.toDouble()/25))
    }

    fun Player.projectileDamage(damage: Double) {
        var epf = (this.inventory.helmet?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.chestplate?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.leggings?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.boots?.enchantments?.get(Enchantment.PROTECTION_ENVIRONMENTAL) ?: 0) +
                (this.inventory.helmet?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0)*2 +
                (this.inventory.chestplate?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0)*2 +
                (this.inventory.leggings?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0)*2 +
                (this.inventory.boots?.enchantments?.get(Enchantment.PROTECTION_PROJECTILE) ?: 0)*2
        if (epf > 20) epf = 20
        this.damage(damage*(1-epf.toDouble()/25))
    }
}