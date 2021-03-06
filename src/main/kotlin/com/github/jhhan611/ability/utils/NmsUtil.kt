package com.github.jhhan611.ability.utils

import com.mojang.datafixers.util.Pair
import net.minecraft.network.protocol.game.PacketPlayOutAnimation
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.EntityDamageSource
import net.minecraft.world.entity.EnumItemSlot
import net.minecraft.world.entity.decoration.EntityArmorStand
import net.minecraft.world.item.Items
import net.minecraft.world.level.IMaterial
import net.minecraft.world.level.World
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.*


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
 * Deals the given amount of damage to this entity, from a specified player.
 * Ignores armor or status effects
 *
 * @param damage Amount of damage to deal
 * @param attacker Player which to attribute this damage from
 */
fun Entity.truePlayerDamage(damage: Float, attacker: Player) {
    val nmsEntity = (this as CraftEntity).handle
    val nmsPlayer = (attacker as CraftPlayer).handle
    val damageSource = DamageSource.playerAttack(nmsPlayer)
    damageSource.javaClass.superclass.getDeclaredMethod("setIgnoreArmor").apply {
        isAccessible = true
        invoke(damageSource)
    }
    damageSource.javaClass.superclass.getDeclaredMethod("setIgnoresInvulnerability").apply {
        isAccessible = true
        invoke(damageSource)
    }
    nmsEntity.damageEntity(damageSource, damage)
}

/**
 * Deals the given amount of damage to this entity, from a specified source.
 *
 * @param damage Amount of damage to deal
 * @param attacker Entity which to attribute this damage from
 */
fun Entity.trueProjectileDamage(damage: Float, attacker: LivingEntity) {
    val nmsEntity = (this as CraftEntity).handle
    val nmsAttacker = (attacker as CraftLivingEntity).handle
    val damageSource = EntityDamageSource("arrow", nmsAttacker).c()
    damageSource.javaClass.superclass.getDeclaredMethod("setIgnoreArmor").apply {
        isAccessible = true
        invoke(damageSource)
    }
    damageSource.javaClass.superclass.getDeclaredMethod("setIgnoresInvulnerability").apply {
        isAccessible = true
        invoke(damageSource)
    }
    nmsEntity.damageEntity(damageSource, damage)
}

/**
 * Deals the given amount of damage to this entity, from a specified entity.
 * Ignores armor or status effects
 *
 * @param damage Amount of damage to deal
 * @param attacker Entity which to attribute this damage from
 */
fun Entity.trueMobDamage(damage: Float, attacker: LivingEntity) { //TODO
    val nmsEntity = (this as CraftEntity).handle
    val nmsAttacker = (attacker as CraftLivingEntity).handle
    val damageSource = DamageSource.mobAttack(nmsAttacker)
    damageSource.javaClass.superclass.getDeclaredMethod("setIgnoreArmor").apply {
        isAccessible = true
        invoke(damageSource)
    }
    damageSource.javaClass.superclass.getDeclaredMethod("setIgnoresInvulnerability").apply {
        isAccessible = true
        invoke(damageSource)
    }
    nmsEntity.damageEntity(damageSource, damage)
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

/**
 * Spawns an Armor Stand with the following options
 *
 * @param location Location to spawn the entity
 * @param invisible Whether to make the Armor Stand invisible
 * @param marker Whether to set the Armor Stand as marker
 */
fun spawnArmorStand(location: Location, invisible: Boolean, marker: Boolean): ArmorStand {
    val w: World = (location.world as CraftWorld).handle
    val nmsEntity = EntityArmorStand(w, location.x, location.y, location.z)
    nmsEntity.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
    nmsEntity.isInvisible = invisible
    nmsEntity.isMarker = marker
    w.addEntity(nmsEntity)
    return nmsEntity.bukkitEntity as ArmorStand
}