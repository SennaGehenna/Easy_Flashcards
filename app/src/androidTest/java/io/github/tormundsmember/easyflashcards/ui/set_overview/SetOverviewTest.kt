package io.github.tormundsmember.easyflashcards.ui.set_overview

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.MainActivity
import io.github.tormundsmember.easyflashcards.ui.set.SetViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.mockk.every
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SetOverviewTest {

    @get:Rule
    var activityActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setUp() {
        with(Dependencies.database) {
            getSets().forEach {
                deleteSet(it)
            }
        }
    }

    @After
    fun tearDown() {
        with(Dependencies.database) {
            getSets().forEach {
                deleteSet(it)
            }
        }
    }

    @Test
    fun shouldAddSet() {

        setupTutorials()

        val text = "TestSet 1"

        addSet(text)

        assertTrue(Dependencies.database.getSets().any { it.name == text })
    }

    @LargeTest
    @Test
    fun shouldAddCardToSet() {
        setupTutorials()

        val text = "TestSet 1"
        val frontText = "FrontText"
        val backText = "BackText"


        addSet(text)
        addCard(frontText, backText)

        with(Dependencies.database) {
            val firstOrNull = getSets().firstOrNull { it.name == text }
            assertTrue(firstOrNull != null && firstOrNull.name == text)
            require(firstOrNull != null)
            val cardsBySetId = getCardsBySetId(firstOrNull.id)
            assertNotEquals(0, cardsBySetId.size)
            cardsBySetId.first().let {
                assertEquals(frontText, it.frontText)
                assertEquals(backText, it.backText)
            }
        }
    }

    @LargeTest
    @Test
    fun shouldAddTwoSetsAndOneCardEach() {
        setupTutorials()

        val set1 = "TestSet 1"
        val set2 = "TestSet 2"
        val frontText1 = "FrontText 1"
        val backText1 = "BackText 1"
        val frontText2 = "FrontText 2"
        val backText2 = "BackText 2"

        addSet(set1)
        addCard(frontText1, backText1)
        clickBack()
        addSet(set2)
        addCard(frontText2, backText2)

        with(Dependencies.database) {
            val set1FromDb = getSets().firstOrNull { it.name == set1 }
            assertTrue(set1FromDb != null && set1FromDb.name == set1)
            require(set1FromDb != null)
            val cardsBySetId = getCardsBySetId(set1FromDb.id)
            assertNotEquals(0, cardsBySetId.size)
            cardsBySetId.first().let {
                assertEquals(frontText1, it.frontText)
                assertEquals(backText1, it.backText)
            }
        }
        with(Dependencies.database) {
            val set2FromDb = getSets().firstOrNull { it.name == set2 }
            assertTrue(set2FromDb != null && set2FromDb.name == set2)
            require(set2FromDb != null)
            val cardsBySetId = getCardsBySetId(set2FromDb.id)
            assertNotEquals(0, cardsBySetId.size)
            cardsBySetId.first().let {
                assertEquals(frontText2, it.frontText)
                assertEquals(backText2, it.backText)
            }
        }
    }

    @LargeTest
    @Test
    fun shouldResetProperly() {
        setupTutorials()

        with(Dependencies.database) {
            val set = Set(1, "Set 1")
            addSet(set)
            val card = Card(
                id = 1,
                frontText = "front",
                backText = "back",
                currentInterval = RehearsalInterval.STAGE_4,
                nextRecheck = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3),
                setId = set.id,
                checkCount = 13,
                positiveCheckCount = 12
            )
            addOrUpdateCard(
                card
            )

            SetViewModel().resetProgress(listOf(card))

            val newCard = getCardById(card.id)
            assertEquals(card.id, newCard.id)
            assertEquals(card.frontText, newCard.frontText)
            assertEquals(card.backText, newCard.backText)
            assertEquals(RehearsalInterval.STAGE_1, newCard.currentInterval)
            assertEquals(0, newCard.nextRecheck)
            assertEquals(card.setId, newCard.setId)
            assertEquals(card.checkCount, newCard.checkCount)
            assertEquals(card.positiveCheckCount, newCard.positiveCheckCount)
        }
    }

    companion object {

        fun addSet(setName: String) {
            clickAddSet()
            setSetText(setName)
            clickSaveSet()
        }

        fun addCard(frontText: String, backText: String) {
            clickAddCard()
            setFrontText(frontText)
            setBackText(backText)
            clickSaveCard()
        }

        fun clickAddSet() {
            onView(withId(R.id.action_add)).perform(click())
        }

        fun setSetText(text: String) {
            onView(withId(R.id.txtOriginalTerm)).perform(typeText(text))
        }

        fun clickSaveSet() {
            onView(withId(R.id.btnSaveTerm)).perform(click())
        }

        fun clickAddCard() {
            onView(withId(R.id.action_add)).perform(click())
        }

        fun setFrontText(text: String) {
            onView(withId(R.id.txtOriginalTerm)).perform(typeText(text))
        }

        fun setBackText(text: String) {
            onView(withId(R.id.txtRevealedTerm)).perform(typeText(text))
        }

        fun clickSaveCard() {
            onView(withId(R.id.btnSaveTerm)).perform(click())
        }

        fun clickBack() {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
        }

        fun ViewInteraction.isGone() = getViewAssertion(ViewMatchers.Visibility.GONE)

        fun ViewInteraction.isVisible() = getViewAssertion(ViewMatchers.Visibility.VISIBLE)

        fun ViewInteraction.isInvisible() = getViewAssertion(ViewMatchers.Visibility.INVISIBLE)

        private fun getViewAssertion(visibility: ViewMatchers.Visibility): ViewAssertion? {
            return ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(visibility))
        }


        fun setupTutorials(
            hasSeenSetOverviewTutorial: Boolean = true,
            hasSeenSetOverviewTutorialWithExistingItems: Boolean = true,
            hasSeenSetsTutorial: Boolean = true,
            hasSeenSetsTutorialWithExistingItems: Boolean = true
        ) {
            every { Dependencies.userData.hasSeenSetOverviewTutorial } returns hasSeenSetOverviewTutorial
            every { Dependencies.userData.hasSeenSetOverviewTutorialWithExistingItems } returns hasSeenSetOverviewTutorialWithExistingItems
            every { Dependencies.userData.hasSeenSetsTutorial } returns hasSeenSetsTutorial
            every { Dependencies.userData.hasSeenSetsTutorialWithExistingItems } returns hasSeenSetsTutorialWithExistingItems
        }
    }


}