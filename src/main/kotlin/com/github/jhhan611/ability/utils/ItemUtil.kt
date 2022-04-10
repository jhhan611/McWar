package com.github.jhhan611.ability.utils

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

val swords = listOf<Material>(
    Material.WOODEN_SWORD,
    Material.STONE_SWORD,
    Material.IRON_SWORD,
    Material.GOLDEN_SWORD,
    Material.DIAMOND_SWORD,
    Material.NETHERITE_SWORD
)

fun ItemStack.setDisplayName(s: String) {
    this.itemMeta.displayName(Component.text(s))
}

fun ItemStack.addLore(s: String) {
    this.itemMeta.lore()?.add(Component.text(s)) ?: this.itemMeta.lore(listOf(Component.text(s)))
}