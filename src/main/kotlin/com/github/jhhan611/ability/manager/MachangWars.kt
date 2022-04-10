package com.github.jhhan611.ability.manager

import com.github.jhhan611.ability.Plugin
import com.github.jhhan611.ability.abilities.*
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.brighten
import com.github.jhhan611.ability.utils.toTextColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.apache.commons.lang.WordUtils
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

//TODO: rank별 확률 적용

@Suppress("SpellCheckingInspection")
object MachangWars {
    val playerAbility = mutableMapOf<Player, MutableSet<AbilityType>>()

    enum class AbilityType(val abilityName: String, val rank: Rank, val abilityObject: Ability) {
        PUNGSUNG("풍성", Rank.L, PungSung),
        MATAN("마탄의 사수", Rank.S, Matan),
        AMOGUS("아모구스", Rank.S, Amogus),
        LUCKY_GAME("운빨망겜", Rank.S, LuckyGame),
        //ROMANCE("낭만", Rank.A, Romance),
        COMBO("콤보", Rank.A, Combo),
        CIGAR("시가", Rank.C, Cigar),
        STAR("별", Rank.S, Star),
        FISHER("낚시꾼", Rank.B, Fisher),
        PAINTER("페인터", Rank.A, Painter),
        ASSASSIN("암살자", Rank.S, Assassin);

        fun getPascalName(): String {
            return WordUtils.capitalize(this.name.lowercase().replace("_", " ")).replace(" ", "")
        }
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
            playerAbility[player] = mutableSetOf(abilityType)
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

    private fun getRandomRank(): Rank {
        var totalWeight = 0.0
        for (i in Rank.values())
            totalWeight += i.chance.toDouble()
        var i = 0
        run {
            var r = Math.random() * totalWeight
            while (i < Rank.values().size - 1) {
                r -= Rank.values()[i].chance.toDouble()
                if (r <= 0.0) break
                ++i
            }
        }
        return Rank.values()[i]
    }

    fun getRandomAbility(): AbilityType {
        var rank = getRandomRank()
        while (AbilityType.values().none { it.rank == rank }) rank = getRandomRank()
        return AbilityType.values().filter { it.rank == rank }.random()
    }

    fun startGame() { // 게임 시작
        val players = plugin!!.server.onlinePlayers
        players.forEach {
            val ability = getRandomAbility()
            addAbility(it, ability)
            it.sendMessage("${ChatColor.GREEN}게임이 시작되었습니다!")

            val component = Component.text("${ChatColor.YELLOW}당신의 능력은 ")
                .append(ability.rank.getPrefixComponent().append(Component.text(" ")))
                .append(Component.text(ability.abilityName).color(ability.rank.color.toTextColor().brighten(0.3)))
                .append(Component.text(" ${ChatColor.YELLOW}입니다."))
            it.sendMessage(component)
        }
    }

    fun Player.getAbilities(): MutableSet<AbilityType> { // 플레이어의 능력들 반환, 능력이 없으면 빈 리스트
        playerAbility[this] ?: return mutableSetOf()
        return playerAbility[this]!!
    }

    class MainListener : Listener {
        private val drop = mutableSetOf<Player>()

        @EventHandler
        fun onInteraction(e: PlayerInteractEvent) {
            if (drop.remove(e.player)) return
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin!!) { drop.remove(e.player) }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
        fun onDeath(e: PlayerDeathEvent) {
            if (e.player.getAbilities().isEmpty()) return
            Bukkit.broadcast(Component.text("${ChatColor.GOLD}${e.player.name}${ChatColor.YELLOW}의 능력은 ${ChatColor.GOLD}${
                e.player.getAbilities().joinToString(", ") { it.abilityName }
            }${ChatColor.YELLOW}이었습니다."))
            e.player.getAbilities().forEach { it.abilityObject.onDelete(e.player) }
            e.player.getAbilities().clear()
        }

        @EventHandler
        fun onDisconnect(e: PlayerQuitEvent) {
            e.player.getAbilities().forEach { it.abilityObject.onDelete(e.player) }
            e.player.getAbilities().clear()
        }
    }
}

enum class Rank(val color: ChatColor, val chance: Number) {
    Sy(ChatColor.DARK_RED, 0),
    M(ChatColor.GREEN, 1),
    L(ChatColor.YELLOW, 1.1),
    S(ChatColor.LIGHT_PURPLE, 1.2),
    A(ChatColor.DARK_GREEN, 1.3),
    B(ChatColor.DARK_AQUA, 1.4),
    C(ChatColor.DARK_GRAY, 1.5);

    fun getPrefixComponent(): TextComponent {
        return Component.text("${ChatColor.GRAY}[")
            .append(Component.text(this.name).color(this.color.toTextColor()))
            .append(Component.text("${ChatColor.GRAY}]"))
    }

    fun getPrefixString(): String {
        return "${ChatColor.GRAY}[${this.color}${this.name}${ChatColor.GRAY}]"
    }
}