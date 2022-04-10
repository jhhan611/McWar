package com.github.jhhan611.ability.manager

import com.github.jhhan611.ability.utils.appendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent.showText
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor

enum class StatusEffect(val effectName: String, val color: TextColor, val description: String) {
    HORROR("공포", TextColor.color(0x674EA7), "사용자를 기준으로 사용자의 반대 방향으로 시선이 고정됩니다. 무작위 메시지가 작성됩니다."),
    ATTRACTION("매혹", TextColor.color(0xFF00FF), "사용자를 기준으로 사용자의 머리 방향으로 시선이 고정됩니다. 무작위 메시지가 작성됩니다."),
    BLEEDING("출혈", TextColor.color(0xFF0000), "달리기가 불가능하며 매 0.7초마다 1의 고정 피해를 입습니다."),
    FAINT("기절", TextColor.color(0xF1C232), "플레이어의 움직임을 봉쇄합니다."),
    FREEZE(
        "빙결",
        TextColor.color(0x0000FF),
        "스택당 공격 속도와 이동 속도가 0.3%씩 느려집니다. 또한 스택이 100이 되면 스택이 초기화 되고 2.5초간 스턴 상태가 됩니다. 10초 간 빙결 스택이 오르지 않으면 0.1초에 1 스택이 줄어듭니다"
    ),
    ZAP("감전", TextColor.color(0x00FFC3), "3틱의 스턴을 가집니다. 10회 이상 노출 시 2초의 스턴 시간을 갖습니다.");

    fun getComponent(): Component {
        return Component.text(this.effectName).color(this.color)
            .hoverEvent(
                showText(
                    Component.text(this.effectName).color(this.color).appendText("${ChatColor.DARK_GRAY} : ")
                        .appendText("${ChatColor.GRAY}${this.description}")
                )
            )
    }

    override fun toString(): String {
        return "{${this.name}}"
    }

}