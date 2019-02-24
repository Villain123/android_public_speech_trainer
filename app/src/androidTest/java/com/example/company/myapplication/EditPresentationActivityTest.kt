package com.example.company.myapplication

import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.longClick
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditPresentationActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule<StartPageActivity>(StartPageActivity::class.java)

    @Before
    fun enableDebugMode() {
        setTrainingPresentationMod(true) // включение тестовой презентации
    }

    @After
    fun disableDebugMode() {
        setTrainingPresentationMod(false) // выключение тестовой презентации
    }

    @Test
    fun datePickerExist() {
        onView(withId(R.id.addBtn)).perform(click())
        onView(withId(R.id.datePicker)).check(matches(isDisplayed()))
    }

    @Test
    fun setDateForPresentation() {
        // Изменение даты при добавлении
        onView(withId(R.id.addBtn)).perform(click())
        onView(withId(R.id.datePicker)).perform(PickerActions.setDate(2035, 5, 12))
        onView(withId(R.id.addPresentation)).perform(click())
        onView(withText("2035-5-12")).check(matches(isDisplayed()))

        // Изменение даты при редактировании
        onView(withText("2035-5-12")).perform(longClick())
        onView(withText("Редактировать")).perform(click())
        onView(withId(R.id.datePicker)).perform(PickerActions.setDate(2036, 5, 12))
        onView(withId(R.id.addPresentation)).perform(click())
        onView(withText("2036-5-12")).check(matches(isDisplayed()))
    }

    private fun setTrainingPresentationMod(mode: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(getTargetContext())
        val spe = sp.edit()
        val testPresentationMode = activityTestRule.activity.getString(R.string.deb_pres)

        spe.putBoolean(testPresentationMode, mode)
        spe.apply()
    }
}