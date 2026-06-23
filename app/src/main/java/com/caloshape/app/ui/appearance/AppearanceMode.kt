package com.caloshape.app.ui.appearance

enum class AppearanceMode {
    LIGHT,
    DARK;

    companion object {
        fun fromStored(value: String?): AppearanceMode {
            return when (value) {
                DARK.name -> DARK
                else -> LIGHT
            }
        }
    }
}
