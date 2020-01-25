package io.github.tormundsmember.easyflashcards.ui.set_overview.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Set(
    @PrimaryKey
    val id: Int,
    val name: String

)