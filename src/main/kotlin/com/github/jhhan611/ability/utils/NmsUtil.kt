package com.github.jhhan611.ability.utils

import com.mojang.datafixers.util.Pair
import net.minecraft.network.protocol.game.PacketPlayOutAnimation
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.EntityDamageSource
import net.minecraft.world.entity.EnumItemSlot
import net.minecraft.world.item.Items
import net.minecraft.world.level.IMaterial
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile

/**
 * Deals the given amount of damage to this entity.
 *
 * @param damage Amount of damage to deal
 */
fun Entity.genericDamage(damage: Float) {
    val nmsEntity = (this as CraftEntity).handle
    nmsEntity.damageEntity(DamageSource.n, damage)
}

/**
 * Deals the given amount of damage to this entity, from a specified entity.
 *
 * @param damage Amount of damage to deal
 * @param attacker Entity which to attribute this damage from
 */
fun Entity.mobDamage(damage: Float, attacker: LivingEntity) {
    val nmsEntity = (this as CraftEntity).handle
    val nmsLiving = (attacker as CraftLivingEntity).handle
    nmsEntity.damageEntity(DamageSource.mobAttack(nmsLiving), damage)
}

/**
 * Deals the given amount of damage to this entity, from a specified player.
 *
 * @param damage Amount of damage to deal
 * @param attacker Player which to attribute this damage from
 */
fun Entity.playerDamage(damage: Float, attacker: Player) {
    val nmsEntity = (this as CraftEntity).handle
    val nmsPlayer = (attacker as CraftPlayer).handle
    nmsEntity.damageEntity(DamageSource.playerAttack(nmsPlayer), damage)
}

/**
 * Deals the given amount of damage to this entity, from a specified projectile and source.
 *
 * @param damage Amount of damage to deal
 * @param projectile Projectile which to attribute this damage from
 * @param attacker Entity which to attribute this damage from, can be null
 */
fun Entity.projectileDamage(damage: Float, projectile: Projectile, attacker: Entity?) {
    val nmsEntity = (this as CraftEntity).handle
    val nmsProjectile = (projectile as CraftEntity).handle
    val nmsAttacker = (attacker as CraftEntity?)?.handle
    nmsEntity.damageEntity(DamageSource.projectile(nmsProjectile, nmsAttacker), damage)
}

/**
 * Deals the given amount of damage to this entity, from a specified source.
 *
 * @param damage Amount of damage to deal
 * @param attacker Entity which to attribute this damage from
 */
fun Entity.simulateProjectileDamage(damage: Float, attacker: LivingEntity) {
    val nmsEntity = (this as CraftEntity).handle
    val nmsAttacker = (attacker as CraftLivingEntity).handle
    nmsEntity.damageEntity(EntityDamageSource("arrow", nmsAttacker).c(), damage)
}

/**
 * Plays the totem animation for the specified entity and nearby players.
 */
fun Entity.animateTotem() {
    val nmsEntity = (this as CraftLivingEntity).handle
    nmsEntity.t.broadcastEntityEffect(nmsEntity, 35)
}

/**
 * Hides the entity's armor for a player.
 * Does NOT block the equipment packets that will update the entity's armor
 *
 * @param player Player to send the equipment packet
 */
fun LivingEntity.hideArmorFor(player: Player) {
    val nmsPlayer = (player as CraftPlayer).handle
    val list = mutableListOf<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>()
    for (i in 2..5)
        list.add(
            Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>(
                EnumItemSlot.values()[i],
                net.minecraft.world.item.ItemStack(Items.a as IMaterial)
            )
        )
    nmsPlayer.b.sendPacket(PacketPlayOutEntityEquipment(entityId, list))
}

/**
 * Shows the entity's armor for a player.
 *
 * @param player Player to send the equipment packet
 */
fun LivingEntity.showArmorFor(player: Player) {
    val nmsPlayer = (player as CraftPlayer).handle
    val list = mutableListOf<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>()
    for (element in EnumItemSlot.values()) {
        val item = (this as CraftLivingEntity).handle.getEquipment(element)
        if (!item.isEmpty) list.add(Pair.of(element, item.cloneItemStack()))
    }
    nmsPlayer.b.sendPacket(PacketPlayOutEntityEquipment(entityId, list))
}

/**
 * Shows the crit particle to the entity
 *
 * @param magic Particle's type
 */
fun Entity.critEffect(magic: Boolean) {
    val nmsEntity = (this as CraftEntity).handle
    nmsEntity.world.minecraftWorld.chunkProvider.broadcast(
        nmsEntity,
        PacketPlayOutAnimation(nmsEntity, if (magic) 5 else 4)
    )
}