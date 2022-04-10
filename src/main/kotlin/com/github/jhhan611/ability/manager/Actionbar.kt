package com.github.jhhan611.ability.manager

import com.github.jhhan611.ability.manager.MachangWars.getAbilities
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.toTextColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable

object Actionbar {
    fun startActionbar() {
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach { p ->
                    val result = mutableListOf<Triple<String, Int, TextColor>>()

                    p.getAbilities().forEach {
                        for (t in it.abilityObject.triggers) {
                            val cooldown = t.getCooldown(p)
                            if (cooldown == 0 || cooldown == null) continue
                            result.add(Triple(t.name, cooldown, t.color))
                        }
                    }

                    val components = result.map { Component.text(it.first).color(it.third).append(Component.text("${ChatColor.DARK_GRAY} : ${ChatColor.WHITE}${it.second}s")) }
                    val final = Component.text()
                    components.forEachIndexed { ind, c ->
                        if (ind == components.size - 1) final.append(c)
                        else final.append(c).append(Component.text(" | ").color(ChatColor.AQUA.toTextColor()))
                    }


                    p.sendActionBar(final.build())
                }
            }
        }.runTaskTimer(plugin!!, 1, 1)
    }
}