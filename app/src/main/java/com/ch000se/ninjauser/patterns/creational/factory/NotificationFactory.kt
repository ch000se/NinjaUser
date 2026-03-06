package com.ch000se.ninjauser.patterns.creational.factory

sealed class Notification {
    abstract fun send(message: String)

    class Email(private val emailAddress: String = "default@mail.com") : Notification() {
        override fun send(message: String) {
            println("Sending EMAIL to $emailAddress: $message")
        }
    }

    class Push(private val deviceToken: String = "token123") : Notification() {
        override fun send(message: String) {
            println("Sending PUSH to device $deviceToken: $message")
        }
    }

    class Sms(private val phoneNumber: String = "+380991234567") : Notification() {
        override fun send(message: String) {
            println("Sending SMS to $phoneNumber: $message")
        }
    }

    companion object Factory {
        fun create(type: NotificationType): Notification = when (type) {
            NotificationType.EMAIL -> Email()
            NotificationType.PUSH -> Push()
            NotificationType.SMS -> Sms()
        }
    }
}

enum class NotificationType {
    EMAIL, PUSH, SMS
}

fun main() {

    val notification1 = Notification.create(NotificationType.EMAIL)
    notification1.send("Hello factory!")

    val notification2 = Notification.Sms("+380997654321")
    notification2.send("Direct SMS message")


    fun handleNotification(notification: Notification) {
        when (notification) {
            is Notification.Email -> println("It's an email")
            is Notification.Push -> println("It's a push")
            is Notification.Sms -> println("It's an SMS")
        }
    }

    handleNotification(notification1)
}