package com.github.jhhan611.ability.manager

import com.github.jhhan611.ability.description.Skill
import com.github.jhhan611.ability.plugin
import com.github.jhhan611.ability.utils.brighten
import com.github.jhhan611.ability.utils.toTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

abstract class Ability : Listener {
    lateinit var abilityType: MachangWars.AbilityType
    var triggers = mutableListOf<Trigger>()

    fun addTrigger(vararg trigger: Trigger) {
        trigger.forEach {
            triggers.add(it)
            it.color = this.abilityType.rank.color.toTextColor().brighten(0.3)
        }
    }

    fun Player.hasAbility() : Boolean { // 플레이어가 오브젝트에 해당하는 능력이 있는기? ex) Matan에서 이 함수를 실행시키면 플레이어가 AbilityType.MATAN을 갖고 있는지 반환함
        if((MachangWars.playerAbility[this] ?: return false).contains(abilityType)) return true
        return false
    }

    fun loadAbility(type: MachangWars.AbilityType) {
        abilityType = type
        plugin!!.server.pluginManager.registerEvents(type.abilityObject, plugin!!)
        onLoad()
    }

    open fun onLoad() {} // 플러그인 로드에 할 것들

    open fun onAdd(player: Player) {} // 플레이어가 능력을 얻었을 때 할 것들
    open fun onDelete(player: Player) {} // 플레이어가 능력을 삭제당했을 때 할 것들

    open fun onRightClick(item: ItemStack?, e: PlayerInteractEvent) { // 플레이어 우클릭 시 할 것들
        if (item == null) return
        if (item.type == Material.IRON_INGOT) trigger1(e.player)
    }

    open fun onLeftClick(item: ItemStack?, e: PlayerInteractEvent) { // 플레이어 좌클릭 시 할 것들
        if (item == null) return
        if (item.type == Material.IRON_INGOT) trigger2(e.player)
    }

    open fun trigger1(player: Player) {} // 철 우클릭 함수
    open fun trigger2(player: Player) {} // 철 좌클릭 함수
}