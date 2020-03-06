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
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.MainActivity
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SetOverviewTest {

    @get:Rule
    var activityActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setup() {
        with(Dependencies.database) {
            getSets().forEach {
                deleteSet(it)
            }
        }
    }

    @Test
    fun shouldAddSet() {

        val text = "TestSet 1"

        addSet(text)

        assertTrue(Dependencies.database.getSets().any { it.name == text })
    }

    @Test
    fun shouldAddCardToSet() {
        val text = "TestSet 1"
        val frontText = "FrontText"
        val backText = "BackText"

        addSet(text)
        addCard(frontText, backText)

        with(Dependencies.database) {
            val firstOrNull = getSets().firstOrNull { it.name == text }
            assertTrue(firstOrNull != null && firstOrNull.name == text)
            require(firstOrNull != null)
            getCardsBySetId(firstOrNull.id).first().let {
                assertEquals(frontText, it.frontText)
                assertEquals(backText, it.backText)
            }
        }
    }

    @Test
    fun shouldAddTwoSetsAndOneCardEach() {
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
            getCardsBySetId(set1FromDb.id).first().let {
                assertEquals(frontText1, it.frontText)
                assertEquals(backText1, it.backText)
            }
        }
        with(Dependencies.database) {
            val set2FromDb = getSets().firstOrNull { it.name == set2 }
            assertTrue(set2FromDb != null && set2FromDb.name == set2)
            require(set2FromDb != null)
            getCardsBySetId(set2FromDb.id).first().let {
                assertEquals(frontText2, it.frontText)
                assertEquals(backText2, it.backText)
            }
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
    }
}