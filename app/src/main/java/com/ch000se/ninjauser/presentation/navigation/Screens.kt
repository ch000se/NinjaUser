package com.ch000se.ninjauser.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screens {
    @Serializable
    data object Home : Screens

    @Serializable
    data class Detail(val userId: String) : Screens
}