package com.github.jhhan611.ability.utils

import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import java.awt.Color
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

fun getBetween(from: Color, to: Color, at: Double): TextColor {
    return TextColor.color(
        (from.red * at + to.red * (1.0 - at)).toInt(),
        (from.green * at + to.green * (1.0 - at)).toInt(),
        (from.blue * at + to.blue * (1.0 - at)).toInt()
    )
}