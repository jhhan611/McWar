package com.github.jhhan611.ability.utils

import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import kotlin.math.roundToInt

fun TextColor.brighten(fraction: Double): TextColor {
    val red = 255.0.coerceAtMost(this.red() + 255 * fraction).roundToInt()
    val green = 255.0.coerceAtMost(this.green() + 255 * fraction).roundToInt()
    val blue = 255.0.coerceAtMost(this.blue() + 255 * fraction).roundToInt()
    return TextColor.color(red, green, blue)
}

fun ChatColor.toTextColor(): TextColor {
    return TextColor.color(this.asBungee().color.rgb)
}