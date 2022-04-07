package com.github.jhhan611.ability.utils

import com.github.jhhan611.ability.manager.MachangWars
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent.showText
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor

fun MachangWars.AbilityType.getDescription() : Component {
    var text: String
    when(this) {
        (MachangWars.AbilityType.AMOGUS) ->
            text = """
                -----------------------------------------------------
                ${this.rank.chatColor}아모구스 (AMOGUS) ${ChatColor.GRAY}[${this.rank.chatColor}${this.rank.name}${ChatColor.GRAY}]
                
                <${ChatColor.BOLD}${ChatColor.AQUA}패시브${ChatColor.RESET}${ChatColor.GRAY}>
                - ${ChatColor.WHITE}증거인멸 : ${ChatColor.GRAY}플레이어를 죽였을 때 킬 로그가 뜨지 않고 자신을 제외한 주변 10칸 이내의 플레이어에게 5초간 실명 효과를 부여합니다.
                
                <${ChatColor.BOLD}${ChatColor.AQUA}스킬${ChatColor.RESET}${ChatColor.GRAY}>
                - ${ChatColor.WHITE}암살 (1m 30s) [철괴 우클릭] : ${ChatColor.GRAY}3.5초간 투명화 상태가 된다. 이때 플레이어를 지정할 시 대상 플레이어 뒤로 이동하여 6의 고정 피해와 출혈${ChatColor.GRAY} 상태 이상을 적용합니다.
                
                ${ChatColor.WHITE}-----------------------------------------------------
            """.trimIndent()
        (MachangWars.AbilityType.CIGAR) ->
            text = """
                
            """.trimIndent()
        (MachangWars.AbilityType.COMBO) ->
            text = """
                
            """.trimIndent()
        (MachangWars.AbilityType.LUCKY_GAME) ->
            text = """
                
            """.trimIndent()
        (MachangWars.AbilityType.MATAN) ->
            text = """
                
            """.trimIndent()
        (MachangWars.AbilityType.PUNGSUNG) ->
            text = """
                
            """.trimIndent()
        (MachangWars.AbilityType.ROMANCE) ->
            text = """
                
            """.trimIndent()
        else ->
            text = ""
    }

    val statusEffectColor = mutableMapOf(
        "공포" to TextColor.color(0x674EA7),
        "매혹" to TextColor.color(0xFF00FF),
        "출혈" to TextColor.color(0xFF0000),
        "기절" to TextColor.color(0xF1C232),
        "빙결" to TextColor.color(0x0000FF),
        "감전" to TextColor.color(0x00FFC3)
    )

    val statusEffectDescription = mutableMapOf(
        "공포" to "${ChatColor.GRAY}사용자를 기준으로 사용자의 반대 방향으로 시선이 고정됩니다. 무작위 메시지가 작성됩니다.",
        "매혹" to "${ChatColor.GRAY}사용자를 기준으로 사용자의 머리 방향으로 시선이 고정됩니다. 무작위 메시지가 작성됩니다.",
        "출혈" to "${ChatColor.GRAY}달리기가 불가능하며 매 0.7초마다 1의 고정 피해를 입습니다.",
        "기절" to "${ChatColor.GRAY}플레이어의 움직임을 봉쇄합니다.",
        "빙결" to "${ChatColor.GRAY}스택당 공격 속도와 이동 속도가 0.3%씩 느려집니다. 또한 스택이 100이 되면 스택이 초기화 되고 2.5초간 스턴 상태가 됩니다. 10초 간 빙결 스택이 오르지 않으면 0.1초에 1 스택이 줄어듭니다",
        "감전" to "${ChatColor.GRAY}3틱의 스턴을 가집니다. 10회 이상 노출 시 2초의 스턴 시간을 갖습니다."
    )

    val component = Component.text()
    for (i in statusEffectColor.keys) {
        for (ind in Regex(i).findAll(text).map { it.range.first }.toList().reversed()) {
            text = StringBuilder(text).insert(ind+i.length, "#SPLIT#").insert(ind, "#SPLIT#").toString()
        }
    }
    val textSplit = text.split("#SPLIT#")
    for (s in textSplit) {
        if (statusEffectColor[s] == null) {
            component.append(Component.text(s))
        } else {
            component.append(Component.text(s).color(statusEffectColor[s]).hoverEvent(
                showText(
                    Component.text(s).color(TextColor.color(0xFFFFFF)).append(Component.text(" : ${statusEffectDescription[s]}"))
                )))
        }
    }

    return component.asComponent()
}