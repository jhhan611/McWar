package com.github.jhhan611.ability

import com.github.jhhan611.ability.MachangWars.getAbilities
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.ArrayList


var plugin : Plugin? = null

class Plugin : JavaPlugin() {
    init {
        plugin = this
    }

    override fun onEnable() {
        MachangWars.loadAbilities(this)
        this.server.pluginManager.registerEvents(MachangWars.MainListener(), this)


        val commands: MutableList<Argument> = ArrayList<Argument>()
        commands.add(StringArgument("command").replaceSuggestions {
            arrayOf(
                "start"
            )
        })

        CommandAPICommand("mw")
            .withPermission(CommandPermission.OP)
            .withArguments(commands)
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                when (args[0]) {
                    ("start") -> MachangWars.startGame()
                    else -> player.sendMessage("${ChatColor.RED}Invalid player")
                }
            })
            .register()

        val arguments: MutableList<Argument> = ArrayList<Argument>()
        arguments.add(StringArgument("ability").replaceSuggestions {
            MachangWars.AbilityType.values().map { it.name }.toTypedArray()
        })
        val abilities = mutableMapOf<String, MachangWars.AbilityType>()
        MachangWars.AbilityType.values().forEach { abilities[it.name] = it }

        CommandAPICommand("addAbility")
            .withArguments(arguments)
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                val ability = args[0] as String?
                MachangWars.addAbility(player, abilities[ability ?: return@PlayerCommandExecutor] ?: return@PlayerCommandExecutor)
            })
            .register()

        CommandAPICommand("removeAbility")
            .withArguments(arguments)
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                val ability = args[0] as String?
                MachangWars.removeAbility(player, abilities[ability ?: return@PlayerCommandExecutor] ?: return@PlayerCommandExecutor)
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

                player.sendMessage("${ChatColor.GREEN}${target.name}'s abilities: ${player.getAbilities().joinToString { it.name }}")
            })
            .register()

//        kommand {
//            register("startGame") {
//                requires { playerOrNull != null && player.isOp }
//                executes {
//                    MachangWars.startGame()
//                }
//            }
//
//            register("addAbility") {
//                requires { playerOrNull != null && player.isOp }
//                then("player" to player()) {
//                    val abilities = MachangWars.AbilityType.values().toList()
//                    abilities.forEach { a ->
//                        then(a.name) {
//                            executes {
//                                val player: Player by it
//                                MachangWars.addAbility(player, a)
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}