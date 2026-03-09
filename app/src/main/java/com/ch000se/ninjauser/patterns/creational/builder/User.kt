package com.ch000se.ninjauser.patterns.creational.builder

data class User(
    val name: String,
    val email: String,
    val age: Int,
    val phone: String,
    val address: String
) {
    class Builder {
        private var name: String = "Kolya"
        private var email: String = "kolya@gmail.com"
        private var age: Int = 25
        private var phone: String = "+380991234567"
        private var address: String = "Kyiv, Ukraine"

        fun name(name: String) = apply { this.name = name }
        fun email(email: String) = apply { this.email = email }
        fun age(age: Int) = apply { this.age = age }
        fun phone(phone: String) = apply { this.phone = phone }
        fun address(address: String) = apply { this.address = address }

        fun build(): User {
            return User(name, email, age, phone, address)
        }
    }
}

fun main() {
    val user = User.Builder()
        .name("Vasya")
        .email("vasya@gmail.com")
        .age(30)
        .phone("+380997654321")
        .address("Lviv, Ukraine")
        .build()

    println(user)
}