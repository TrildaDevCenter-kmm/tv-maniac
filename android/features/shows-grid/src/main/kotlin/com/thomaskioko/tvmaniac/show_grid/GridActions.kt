package com.thomaskioko.tvmaniac.show_grid

sealed interface GridActions

data class ReloadShows(val category: Long)  : GridActions
data class LoadShows(val category: Long) : GridActions