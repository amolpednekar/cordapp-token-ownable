package com.template

import net.corda.core.serialization.SerializationWhitelist

class Plugin : SerializationWhitelist {
    override val whitelist: List<Class<*>> get() = listOf(
    )
}