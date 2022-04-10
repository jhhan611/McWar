package com.github.jhhan611.ability.description

import com.github.jhhan611.ability.manager.MachangWars
import com.github.jhhan611.ability.manager.MachangWars.AbilityType.*
import com.github.jhhan611.ability.utils.appendText
import com.github.jhhan611.ability.utils.newline
import com.github.jhhan611.ability.utils.toComponent
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor


fun MachangWars.AbilityType.getDescription(): Component {
    when (this) {
        AMOGUS ->
            return DescriptionBuilder(this)
                .addPassive("증거인멸", "플레이어를 죽였을 때 킬 로그가 뜨지 않고 자신을 제외한 주변 10칸 이내의 플레이어에게 5초간 실명 효과를 부여합니다.")
                .addSkill(
                    "암살",
                    "1m 30s",
                    "철괴 우클릭",
                    "3.5초간 투명화 상태가 된다. 이때 플레이어를 지정할 시 대상 플레이어 뒤로 이동하여 6의 고정 피해와 {BLEEDING} 상태 이상을 적용합니다 ."
                ).addProvider("Vmis").build()
        CIGAR ->
            return DescriptionBuilder(this)
                .addPassive("약물 중독", "디버프 효과를 받지 않습니다.")
                .addSkill(
                    "흡연",
                    "30s",
                    "철괴 우클릭",
                    "1.5초 동안 흡연을 하며 4의 고정 피해를 입고, 주변 7칸의 플레이어들에게 멀미 I를 15초 부여합니다."
                ).addProvider("Vmis").build()
        COMBO ->
            return DescriptionBuilder(this)
                .addPassive("쪼개기", "모든 근접 공격을 적중시킨다면 7틱 후, 넉백 없는 40%의 추가 피해를 입힙니다.").addProvider("Vmis").build()
        LUCKY_GAME ->
            return DescriptionBuilder(this)
                .addPassive("인생한방", "근접 공격 시 5%의 확률로 적을 즉사시킵니다. 성공 시 상대방의 체력을 자신의 흡수 체력으로 변환시킵니다.")
                .addSkill(
                    "럭키 코인",
                    "1m",
                    "철괴 우클릭",
                    "인생한방 효과의 확률을 한 번 30%로 증가시킵니다. 이 때 실패할 경우 2분 30초 동안 이 스킬과 인생한방 효과는 작동되지 않습니다."
                ).addProvider("Vmis").build()
        MATAN ->
            return DescriptionBuilder(this)
                .addPassive("마탄", "활이 5발제 스나이퍼로 변경되며, 5개의 탄알을 모두 소비 시 2.5초의 장전 시간을 가집니다. 플레이어와 유리, 나무, 돌을 관통합니다.")
                .addPassive(
                    "최후의 한 발".toComponent(),
                    "체력이 4 이하로 내려갈 시 마탄의 탄창이 1로 고정되고 한 번 적중 시 다시 재설정됩니다. 또한 적의 체력에 따라 이하의 데미지가 적용됩니다.".toComponent()
                        .newline().appendText(" ■ 6 이하 : 처형")
                        .newline().appendText(" ■ 10 이하 : 데미지 3 추가")
                        .newline().appendText(" ■ 15 이하 : 데미지 1 추가")
                ).addProvider("Vmis").build()
        PUNGSUNG ->
            return DescriptionBuilder(this)
                .addPassive("머리카락 = 힘".toComponent(),
                    "머리카락이 1분마다 1씩 증가합니다. 머리카락 1개당 추가 데미지 0.5가 적용되며 상대를 처치할 시 이하의 확률로 머리카락을 얻습니다.".toComponent()
                    .newline().appendText(" ■ 60% : 1 / 30% : 2 / 10% : 3")
                ).addProvider("Vmis").build()
        /*ROMANCE ->
            return DescriptionBuilder(this)
                .addPassive("정정당당", "주는 데미지와 받는 데미지가 4로 고정됩니다. 이 효과는 방어구, 마법 부여를 무시합니다.")
                .addProvider("Vmis").build()*/
        FISHER ->
            return DescriptionBuilder(this)
                .addPassive("월척", "엔티티를 낚싯대로 당길 때 20%의 확률로 하늘로 날아오릅니다.")
                .addSkill(
                    "그로기",
                    "30s",
                    "철괴 우클릭",
                    "10초 동안 월척의 확률이 100%로 고정되며, 하늘로 날아오르는 효과가 극히 줄어듭니다. 또한 효과가 지속되는 동안에 같은 대상을 연속 3번 이상 맞출 시 4초 동안 {FAINT} 상태 이상을 부여합니다."
                ).addProvider("ELEC_B0").build()
        PAINTER ->
            return DescriptionBuilder(this)
                .addPassive("삼원색".toComponent(), "자신이 존재하는 컬러 존의 색깔에 따라 이하의 효과를 얻는다.".toComponent()
                    .newline().appendText(" ■ 빨간색 : 힘 I")
                    .newline().appendText(" ■ 노란색 : 재생 I")
                    .newline().appendText(" ■ 파란색 : 신속 I"))
                .addSkill("페인팅", "1m", "철괴 우클릭",
                    "자신의 위치에 빨간색, 노란색, 파란색 중 무작위의 색깔을 골라 2분간 지속되는 반경 5칸의 컬러 존을 설치합니다." )
                .addSkill("컬러 스프레이", "2m", "철괴 좌클릭",
                    "자신의 위치에 스프레이를 뿌려 20초간 지속되는 반경 8칸의 스프레이 지역을 만듭니다. 자신은 이 지역에서 투명화가 되고, 다른 플레이어들은 이 지역에서 실명과 발광에 걸립니다.")
                .build()
        ASSASSIN ->
            return DescriptionBuilder(this)
                .addPassive("타게팅".toComponent(), "게임이 시작할 때, 무작위로 1명을 대상으로 지정합니다. 대상을 죽이는데 성공하면 지속적인 힘 효과가 1 늘어납니다. 대상이 더 이상 존재하지 않을 때, 무작위의 다른 사람을 대상으로 합니다.".toComponent())
                .addPassive("위치 추적기".toComponent(), "타게팅의 대상의 좌표를 10의 자리까지 반올림하여 볼 수 있습니다.".toComponent())
                .addSkill("단검 투척", "40s", "검 우클릭",
                    "검을 10블럭 앞으로 날려 공격합니다. 이때 인챈트는 동일하게 적용되며, 검은 돌아오지 않고 자신만 주울 수 있게 떨어집니다." )
                .addSkill("타겟 리셋", "25s", "철괴 우클릭",
                    "타게팅의 대상을 무작위로 다시 정합니다.")
                .build()
        else ->
            return DescriptionBuilder(this).build()
    }
}