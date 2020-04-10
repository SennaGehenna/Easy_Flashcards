package io.github.tormundsmember.easyflashcards.data

interface UserData {


    var hasSeenSetsTutorial: Boolean 

    var hasSeenSetsTutorialWithExistingItems: Boolean 

    var hasSeenSetOverviewTutorial: Boolean 

    var hasSeenSetOverviewTutorialWithExistingItems: Boolean 

    var useDarkMode: Boolean

    var currentDarkModeSetting: Int

    val hasOldDarkModeSetting: Boolean

    var useSpacedRepetition: Boolean 

    var allowCrashReporting: Boolean 

    var limitCards: Boolean 

    var limitCardsAmount: Int

    var doNotShowLearnedCards: Boolean 

    fun removeOldDarkModeSetting()
}