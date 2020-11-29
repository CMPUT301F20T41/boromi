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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test for Login UI
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginUITest {

	@Rule
	public ActivityScenarioRule<MainActivity> activityRule
			= new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void navigateToLogin() {
		onView(withId(R.id.welcome_login)).perform(click());
		onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
	}

	@Test
	public void testLogin() {
		// Enters information in the email and password fields
		onView(withId(R.id.login_email))
				.perform(typeText("loginUITest@test.com"), closeSoftKeyboard());
		onView(withId(R.id.login_password))
				.perform(typeText("testuser"), closeSoftKeyboard());

		// Checks the contents of the email and password fields
		onView(withId(R.id.login_email)).check(matches(withText("loginUITest@test.com")));
		onView(withId(R.id.login_password)).check(matches(withText("testuser")));

		onView(withId(R.id.login_login)).perform(click());


		// Validates that the loading spinner appears
		onView(withId(R.id.login_loading)).check(matches(isDisplayed()));
	}

	@Test
	public void testNavigateToWelcome() {
		// Clicks the back button
		onView(withId(R.id.button_loginBack)).perform(click());

		// Validates that the UI navigates back to the welcome page
		onView(withId(R.id.fragment_welcome)).check(matches(isDisplayed()));
	}

	@Test
	public void testNavigateToRecoverPassword() {
		// Clicks the recover password button
		onView(withId(R.id.login_recoverPassword)).perform(click());

		// Validates that the UI navigates to the recover password page
		onView(withId(R.id.fragment_recover_password)).check(matches(isDisplayed()));
	}
}
