package com.github.jhhan611.ability.description

import com.github.jhhan611.ability.manager.MachangWars
import com.github.jhhan611.ability.manager.StatusEffect
import com.github.jhhan611.ability.utils.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextColor
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor
import java.util.regex.Pattern

private val descriptionSeparator = StringUtils.repeat("-", 40)
private val infoColor = ChatColor.GRAY
private val extraColor = ChatColor.DARK_GRAY

class DescriptionBuilder(val ability: MachangWars.AbilityType) {
    private val passives = mutableListOf<Passive>()
    private val skills = mutableListOf<Skill>()
    private val conditions = mutableListOf<String>()
    private val providers = mutableListOf<String>()

    fun addPassive(name: Component, description: Component): DescriptionBuilder {
        passives.add(Passive(name, description.colorInfo().checkStatusEffect()))
        return this
    }

    fun addPassive(name: String, description: String): DescriptionBuilder {
        passives.add(Passive(name.toComponent(), description.toComponent().colorInfo().checkStatusEffect()))
        return this
    }

    fun addSkill(name: Component, cooldown: Component, usage: Component, description: Component): DescriptionBuilder {
        skills.add(Skill(name, cooldown, usage, description.colorInfo().checkStatusEffect()))
        return this
    }

    fun addSkill(name: String, cooldown: String, usage: String, description: String): DescriptionBuilder {
        skills.add(
            Skill(
                name.toComponent(),
                cooldown.toComponent(),
                usage.toComponent(),
                description.toComponent().colorInfo().checkStatusEffect()
            )
        )
        return this
    }

    fun addCondition(condition: String): DescriptionBuilder {
        conditions.add(condition)
        return this
    }

    fun addProvider(provider: String): DescriptionBuilder {
        providers.add(provider)
        return this
    }

    private fun Component.colorInfo(): Component {
        return this.colorIfAbsent(TextColor.color(infoColor.asBungee().color.rgb))
    }

    private fun Component.checkStatusEffect(): Component {
        var component = this
        StatusEffect.values().forEach {
            component = component.replaceText(
                TextReplacementConfig.builder()
                    .matchLiteral(it.toString()).replacement(it.getComponent()).build()
            )
        }
        return component
    }

    fun build(): Component {
        var component = Component.text(descriptionSeparator).newline()
            .append(ability.rank.getPrefixComponent()).space()
            .append(Component.text(ability.abilityName).color(ability.rank.color.toTextColor().brighten(0.3))).space()
            .appendText("${ChatColor.GRAY}(${ability.getPascalName()})").newline()
        if (conditions.isNotEmpty()) {
            component =
                component.appendText("${ChatColor.GRAY}<${ChatColor.AQUA}조건${ChatColor.GRAY}>").newline()
            for (condition in conditions)
                component = component.appendText("${ChatColor.DARK_GRAY} - ").append(condition.toComponent()).newline()
        }
        if (passives.isNotEmpty()) {
            component =
                component.appendText("${ChatColor.GRAY}<${ChatColor.AQUA}패시브${ChatColor.GRAY}>").newline()
            for (passive in passives)
                component = component.appendText("${ChatColor.DARK_GRAY} - ")
                    .append(passive.name)
                    .appendText("${ChatColor.DARK_GRAY} : ")
                    .append(passive.description)
                    .newline()
        }
        if (skills.isNotEmpty()) {
            component =
                component.appendText("${ChatColor.GRAY}<${ChatColor.AQUA}스킬${ChatColor.GRAY}>").newline()
            for (skill in skills)
                component = component.appendText("${ChatColor.DARK_GRAY} - ")
                    .append(skill.name)
                    .appendText("${ChatColor.DARK_GRAY} (")
                    .append(skill.cooldown)
                    .appendText("${ChatColor.DARK_GRAY})")
                    .appendText("${ChatColor.DARK_GRAY} [")
                    .append(skill.usage)
                    .appendText("${ChatColor.DARK_GRAY}] : ")
                    .append(skill.description)
                    .newline()
        }
        if (providers.isNotEmpty()) {
            component =
                component.appendText("${ChatColor.GRAY}<${ChatColor.AQUA}아이디어 제공자${ChatColor.GRAY}>").newline()
            for (provider in providers)
                component = component.appendText("${ChatColor.DARK_GRAY} - ").append(provider.toComponent()).newline()
        }
        return component.appendText(descriptionSeparator)
    }

}

data class Passive(val name: Component, val description: Component)

data class Skill(val name: Component, val cooldown: Component, val usage: Component, val description: Component)