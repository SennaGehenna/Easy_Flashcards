package io.github.tormundsmember.easyflashcards.ui.set.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Set::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["setId"])]
)
data class Card(
    @PrimaryKey
    val id: Int,
    val frontText: String,
    val backText: String,
    val currentInterval: Day,
    val nextRecheck: Date,
    val setId: Int,
    val checkCount: Int, //how many times this card was rehearsed
    val positiveCheckCount: Int //how many times this card was correctly translated

)

typealias Day = Int
typealias Date = Long