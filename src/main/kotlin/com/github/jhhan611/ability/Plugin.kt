package com.github.jhhan611.ability

import com.github.jhhan611.ability.description.getDescription
import com.github.jhhan611.ability.manager.MachangWars
import com.github.jhhan611.ability.manager.MachangWars.getAbilities
import com.github.jhhan611.ability.manager.MachangWars.getRandomAbility
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.StringTooltip
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


var plugin: Plugin? = null

class Plugin : JavaPlugin() {
    init {
        plugin = this
    }

    override fun onEnable() {
        MachangWars.loadAbilities(this)
        this.server.pluginManager.registerEvents(MachangWars.MainListener(), this)

        CommandAPICommand("mw")
            .withPermission(CommandPermission.OP)
            .withArguments(StringArgument("command").replaceSuggestions {
                arrayOf(
                    "start"
                )
            })
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                when (args[0]) {
                    ("start") -> MachangWars.startGame()
                    else -> player.sendMessage("${ChatColor.RED}Invalid player")
                }
            })
            .register()

        val arguments: MutableList<Argument> = ArrayList()
        arguments.add(
            StringArgument("ability")
                .replaceSuggestionsT {
                    MachangWars.AbilityType.values()
                        .map {
                            StringTooltip.of(
                                it.getPascalName(),
                                it.rank.getPrefixString() + " " + it.rank.color.toString() + it.abilityName
                            )
                        }
                        .toTypedArray()
                }
        )

        val abilities = mutableMapOf<String, MachangWars.AbilityType>()
        MachangWars.AbilityType.values().forEach { abilities[it.getPascalName().lowercase()] = it }

        CommandAPICommand("ability")
            .withPermission(CommandPermission.OP)
            .withArguments(PlayerArgument("player"))
            .withArguments(StringArgument("add|list|remove").replaceSuggestions { arrayOf("add", "list", "remove") })
            .withArguments(arguments)
            .executes(CommandExecutor { sender, args ->

            })
            .register()


        CommandAPICommand("addAbility")
            .withArguments(arguments)
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val ability = args[0] as String?
                MachangWars.addAbility(
                    player,
                    abilities[ability!!.lowercase()] ?: return@PlayerCommandExecutor
                )
            })
            .register()

        CommandAPICommand("removeAbility")
            .withArguments(arguments)
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val ability = args[0] as String?
                MachangWars.removeAbility(
                    player,
                    abilities[ability!!.lowercase()] ?: return@PlayerCommandExecutor
                )
            })
            .register()

        CommandAPICommand("checkAbility")
            .withArguments(PlayerArgument("player"))
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                val target = args[0] as? Player
                if (target == null) {
                    player.sendMessage("${ChatColor.RED}Invalid player")
                    return@PlayerCommandExecutor
                }

                player.sendMessage(
                    "${ChatColor.GREEN}${target.name}'s abilities: ${
                        target.getAbilities().joinToString { it.name }
                    }"
                )
            })
            .register()

        CommandAPICommand("printDescription")
            .withArguments(arguments)
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                val ability = args[0] as String?
                player.sendMessage(abilities[ability!!.lowercase()]?.getDescription() ?: return@PlayerCommandExecutor)
            })
            .register()

        CommandAPICommand("justtestdontmindthiscommand")
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                player.sendMessage(getRandomAbility().getPascalName())
            })
            .register()
    }
}