package com.github.agejevasv.evovox.app

object GlobalStore {
    private val repo = HashMap<String, Any?>()

    fun put(key: String, value: Any?)= repo.put(key, value)

    fun <T> get(key: String) = repo[key] as T
}