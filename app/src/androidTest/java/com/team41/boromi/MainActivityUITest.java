package com.team41.boromi;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test for Auth pages UI
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {

	@Rule
	public ActivityScenarioRule<MainActivity> activityRule
			= new ActivityScenarioRule<>(MainActivity.class);

	@Test
	public void testGoToLogin() {
		// Type text and then press the button.
		onView(withId(R.id.welcome_login)).perform(click());
		onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
	}

	@Test
	public void testGoToCreateAccount() {
		// Type text and then press the button.
		onView(withId(R.id.welcome_signup)).perform(click());
		onView(withId(R.id.fragment_signup)).check(matches(isDisplayed()));
	}
}
