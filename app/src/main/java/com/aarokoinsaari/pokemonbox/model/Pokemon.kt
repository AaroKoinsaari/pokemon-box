/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Pokemon(
    val id: Int,
    val name: String,
    val image: ImageVector? = null,
    val types: List<String>? = null,
    val description: String? = null
)
