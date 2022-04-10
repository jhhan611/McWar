package com.github.jhhan611.ability.utils

import net.kyori.adventure.text.Component

fun Component.newline(): Component {
    return this.append(Component.newline())
}

fun Component.space(): Component {
    return this.append(Component.space())
}

fun Component.appendText(text: String): Component {
    return this.append(Component.text(text))
}

fun String.toComponent(): Component {
    return Component.text(this)
}