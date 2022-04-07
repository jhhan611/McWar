import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object Plugin : JavaPlugin() {}

class SkillItem(private val skillType: SkillType, material: Material, private val cooldown: Int) : ItemStack(material) {
    private val currentCooldownKey = NamespacedKey(Plugin, "currentCooldown")
    private val cooldownKey = NamespacedKey(Plugin, "cooldown")

    init {
        this.apply {
            itemMeta = itemMeta.apply {
                persistentDataContainer.set(currentCooldownKey, PersistentDataType.INTEGER, 0)
                persistentDataContainer.set(cooldownKey, PersistentDataType.INTEGER, 0)
                displayName(Component.text(skillType.skillName))
            }
        }

        val plugin = Bukkit.getPluginManager().getPlugin("plugin name")

        val item = this

        object : BukkitRunnable() {
            override fun run() {
                try {
                    var currentCooldown =
                        item.itemMeta.persistentDataContainer.get(currentCooldownKey, PersistentDataType.INTEGER)!!
                    if (currentCooldown > 0) {
                        currentCooldown--
                    }
                    item.itemMeta.persistentDataContainer.set(
                        currentCooldownKey,
                        PersistentDataType.INTEGER,
                        currentCooldown
                    )
                } catch (ex: Exception) {
                    cancel()
                }
            }
        }.runTaskTimer(plugin!!, 0, 20)
    }
}

class SkillUseListener : Listener {
    @EventHandler
    fun onDeath(e: PlayerInteractEvent) {
        val cooldownKey = NamespacedKey(Plugin, "cooldown")
        val currentCooldownKey = NamespacedKey(Plugin, "currentCooldown")
        if (e.item == null) return

        val container = e.item!!.itemMeta.persistentDataContainer

        if (!container.has(currentCooldownKey, PersistentDataType.INTEGER)) return

        val cooldown = container.get(currentCooldownKey, PersistentDataType.INTEGER)!!

        if (cooldown == 0) {
            // use skill
            container.set(
                currentCooldownKey,
                PersistentDataType.INTEGER,
                container.get(cooldownKey, PersistentDataType.INTEGER)!!
            )
        } else if (cooldown > 0) {
            e.player.sendMessage("talmo cant use skill z")
        }
    }
}