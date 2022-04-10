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

val airs = listOf(
    Material.AIR,
    Material.VOID_AIR,
    Material.CAVE_AIR
)

fun ItemStack.setDisplayName(s: String) {
    this.itemMeta.displayName(Component.text(s))
}

fun ItemStack.addLore(s: String) {
    this.itemMeta.lore()?.add(Component.text(s)) ?: this.itemMeta.lore(listOf(Component.text(s)))
}