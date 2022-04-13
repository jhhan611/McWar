package com.github.jhhan611.ability.utils

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

val swords = listOf(
    Material.WOODEN_SWORD,
    Material.STONE_SWORD,
    Material.IRON_SWORD,
    Material.GOLDEN_SWORD,
    Material.DIAMOND_SWORD,
    Material.NETHERITE_SWORD
)

val Material.isSword: Boolean
    get() {
        return swords.contains(this)
    }