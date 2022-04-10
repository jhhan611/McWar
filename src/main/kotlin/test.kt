import net.kyori.adventure.text.Component
import net.minecraft.world.entity.EntityTypes
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

enum class SkillType(val skillName: String) {
    TEST1("test1"), TEST2("test2"), TEST3("test3")
}

data class Inflict(val user: Player, val target: Player, val skillType: SkillType) {
    val deathMessage = "${target.name} was killed by ${user.name} using ${skillType.skillName}"
}

object Skills {
    var inflict : MutableMap<Player, Inflict> = mutableMapOf()

    fun testSkill1(user: Player, target: Player) {
        target.damage(5.0)
        target.lastDamageCause = EntityDamageEvent(user, EntityDamageEvent.DamageCause.CUSTOM, 5.0)
        inflict[target] = Inflict(user, target, SkillType.TEST1)
    }

    fun testSkill2(user: Player, target: Player) {
        target.damage(10.0)
        target.lastDamageCause = EntityDamageEvent(user, EntityDamageEvent.DamageCause.CUSTOM, 10.0)
        inflict[target] = Inflict(user, target, SkillType.TEST2)
    }

    fun testSkill3(user: Player, target: Player) {
        target.damage(15.0)
        target.lastDamageCause = EntityDamageEvent(user, EntityDamageEvent.DamageCause.CUSTOM, 15.0)
        inflict[target] = Inflict(user, target, SkillType.TEST3)
    }
}

class DeathListener: Listener {
    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if (e.player.lastDamageCause == null) return
        if (e.player.lastDamageCause!!.cause != EntityDamageEvent.DamageCause.CUSTOM) return
        val inflict = Skills.inflict[e.player] ?: return
        e.deathMessage(Component.text(inflict.deathMessage))
    }
}
