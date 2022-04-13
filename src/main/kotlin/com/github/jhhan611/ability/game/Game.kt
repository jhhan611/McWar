package com.github.jhhan611.ability.game

import com.github.jhhan611.ability.manager.MachangWars.addAbility
import com.github.jhhan611.ability.manager.MachangWars.getAbilities
import com.github.jhhan611.ability.manager.MachangWars.getRandomAbility
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.boss.BarStyle
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.awt.Color
import kotlin.math.ceil

class Game : Listener {
    var playing: Boolean = true
    private val players: MutableSet<Player> = mutableSetOf()

    init {
        players.addAll(Bukkit.getOnlinePlayers())
        Bukkit.broadcast(Component.text("게임이 시작되었습니다!").color(TextColor.color(0x77ff77)).decorate(TextDecoration.BOLD))
        players.forEach {
            it.getAbilities().clear()
            val ability = getRandomAbility()
            addAbility(it, ability)
            val component = Component.text("${ChatColor.YELLOW}당신의 능력은 ")
                .append(ability.rank.getPrefixComponent().space())
                .append(
                    Component.text(ability.abilityName).color(ability.rank.color.toTextColor().brighten(0.3)).space()
                )
                .append(Component.text("${ChatColor.YELLOW}입니다."))
            it.sendMessage(component)
        }

        state = GameState.ABILITY_CHOOSE

        val startColor = Color(0x77ff77);
        val endColor = Color(0xbb0000);

        val time = GameConfig.getAbilityChooseTime() * 20
        var cur = GameConfig.getAbilityChooseTime() * 20

        fun getBarComponent(): Component {
            return "${ChatColor.YELLOW}능력 선택시간 ${ChatColor.GRAY}[${ChatColor.GREEN}".toComponent().append(
                Component.text(ceil(cur / 20.0).toInt()).color(
                    getBetween(startColor, endColor, cur / time.toDouble())
                )
            ).append("${ChatColor.GRAY}]".toComponent())
        }

        val bar = BossBar.bossBar(
            getBarComponent(),
            0.0f,
            BossBar.Color.YELLOW,
            BossBar.Overlay.PROGRESS
        )
        players.forEach { it.showBossBar(bar) }
        object : BukkitRunnable() {
            override fun run() {
                if (!playing) return cancel()
                cur--
                bar.name(getBarComponent())
                bar.progress((time - cur) / time.toFloat())
                if (cur == 0) {
                    Bukkit.getOnlinePlayers().forEach { it.hideBossBar(bar) }
                    state = GameState.PEACE_TIME
                    peaceTime()
                    return cancel()
                }
            }
        }.runTaskTimer(plugin!!, 1, 1)
    }

    private fun peaceTime(){

    }

    private fun endGame(winner: Player) {
        Bukkit.broadcast(
            Component.newline().newline().newline()
                .append(Component.text("게임이 종료되었습니다!").color(TextColor.color(0xff7777)).decorate(TextDecoration.BOLD))
        )
        Bukkit.broadcast(
            Component.text("우승자: ").color(TextColor.color(0x77ffff))
                .append(Component.selector(winner.name).color(TextColor.color(0xffff55)).decorate(TextDecoration.BOLD))
        )
        Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f) }
        stop()
    }

    fun stop() {
        playing = false
        stopGame()
    }

    private fun removePlayer(player: Player) {
        players.remove(player)
        if (players.size == 1) endGame(players.first())
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entityType != EntityType.PLAYER) return
        if (state == GameState.ABILITY_CHOOSE || state == GameState.PEACE_TIME) e.damage = 0.0
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onDeath(e: PlayerDeathEvent) {
        removePlayer(e.player)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (players.contains(e.player)) {
            e.player.health = 0.0
            removePlayer(e.player)
        }
    }

}