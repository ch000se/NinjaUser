package com.ch000se.ninjauser.patterns.structural.delegate


interface Storage {
    fun save(key: String, value: String)
    fun get(key: String): String?
}

class InMemoryStorage : Storage {
    private val data = mutableMapOf<String, String>()

    override fun save(key: String, value: String) {
        data[key] = value
    }

    override fun get(key: String): String? = data[key]
}

class UserPreferences(
    storage: Storage
) : Storage by storage {

    fun saveUserName(name: String) = save("user_name", name)
    fun getUserName(): String? = get("user_name")

    fun saveToken(token: String) = save("token", token)
    fun getToken(): String? = get("token")

}


fun main() {
    val prefs = UserPreferences(InMemoryStorage())

    prefs.saveUserName("Roma")
    prefs.saveToken("25252452")

    println("User: ${prefs.getUserName()}")
    println("Token: ${prefs.getToken()}")
}