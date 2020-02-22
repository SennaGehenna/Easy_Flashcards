package io.github.tormundsmember.easyflashcards.ui.base_ui.exceptions

class MissingRequiredKeysException(message: String, val missingKeys: List<String>) : Throwable(message) {
}