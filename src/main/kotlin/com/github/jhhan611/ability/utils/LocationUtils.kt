package com.github.jhhan611.ability.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import java.util.function.Predicate

fun Location.getNearbyEuclideanEntities(distance: Double): List<Entity> {
    return getNearbyEntities(distance, distance, distance)
        .filter { it.location.distanceSquared(this) <= distance * distance }
        .toCollection(mutableListOf())
}