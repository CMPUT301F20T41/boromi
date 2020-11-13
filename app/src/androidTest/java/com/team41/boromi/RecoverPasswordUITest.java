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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecoverPasswordUITest {

	@Rule
	public ActivityScenarioRule<MainActivity> activityRule
			= new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void navigateToRecoverPassword() {
		// Clicks the go to login button
		onView(withId(R.id.welcome_login)).perform(click());

		// Validates that the UI navigates to the login page
		onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));

		// Clicks the recover password button
		onView(withId(R.id.login_recoverPassword)).perform(click());

		// Validates that the UI navigates to the recover password page
		onView(withId(R.id.fragment_recover_password)).check(matches(isDisplayed()));
	}

	@Test
	public void testRecoverPassword() {
		// Enters information into the login field and checks that it was enter
		onView(withId(R.id.recover_email))
				.perform(
						typeText("recoverPasswordUITest@test.com"),
						closeSoftKeyboard()
				);
		onView(withId(R.id.recover_email))
				.check(matches(withText("recoverPasswordUITest@test.com")));

		// Clicks the recover password button
		onView(withId(R.id.recover_recover)).perform(click());

		// TODO: Validates that the loading spinner appears
		onView(withId(R.id.recover_loader)).check(matches(isDisplayed()));
	}

	@Test
	public void testNavigateToLogin() {
		// Clicks the back button
		onView(withId(R.id.button_recoverBack)).perform(click());

		// Validates that the UI navigates back to the welcome page
		onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
	}

}
