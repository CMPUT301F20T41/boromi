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
 * Test for Signup UI
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpUITest {

	@Rule
	public ActivityScenarioRule<MainActivity> activityRule
			= new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void navigateToSignUp() {
		onView(withId(R.id.welcome_signup)).perform(click());
		onView(withId(R.id.fragment_signup)).check(matches(isDisplayed()));
	}

	@Test
	public void testSignUp() {
		// Enters information in the email, username, and password fields
		onView(withId(R.id.signup_email))
				.perform(typeText("signUpUITest@test.com"), closeSoftKeyboard());
		onView(withId(R.id.signup_username))
				.perform(typeText("signUpUITest"), closeSoftKeyboard());
		onView(withId(R.id.signup_password))
				.perform(typeText("testuser"), closeSoftKeyboard());

		// Checks the contents of the email, username and password fields
		onView(withId(R.id.signup_email)).check(matches(withText("signUpUITest@test.com")));
		onView(withId(R.id.signup_username)).check(matches(withText("signUpUITest")));
		onView(withId(R.id.signup_password)).check(matches(withText("testuser")));

		// Clicks on the signup button
		onView(withId(R.id.signup_signup)).perform(click());

		// Validates that the loading spinner appears
		onView(withId(R.id.signup_loading)).check(matches(isDisplayed()));
	}

	@Test
	public void testNavigateToWelcome() {
		// Clicks the back button
		onView(withId(R.id.button_signupBack)).perform(click());

		// Validates that the UI navigates back to the welcome page
		onView(withId(R.id.fragment_welcome)).check(matches(isDisplayed()));
	}

}
