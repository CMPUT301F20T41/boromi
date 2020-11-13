package com.team41.boromi;

import android.util.Log;
import android.util.Pair;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.callbacks.AuthCallback;
import com.team41.boromi.controllers.AuthenticationController;
import com.team41.boromi.dbs.UserDB;
import com.team41.boromi.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BookActivityUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() throws InterruptedException {
        onView(withId(R.id.welcome_login)).perform(click());
        onView(withId(R.id.login_email))
                .perform(typeText("rcravichan3@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password))
                .perform(typeText("supertest"), closeSoftKeyboard());

        // Checks the contents of the email and password fields
        onView(withId(R.id.login_email)).check(matches(withText("rcravichan3@gmail.com")));
        onView(withId(R.id.login_password)).check(matches(withText("supertest")));

        onView(withId(R.id.login_login)).perform(click());


        // Validates that the loading spinner appears
        onView(withId(R.id.login_loading)).check(matches(isDisplayed()));

        Thread.sleep(5000);

    }

//    @Test
//    public void testOwnerTabsSwitch() {

//        onView(withId(R.id.tab_owned_books)).perform(click());
//
//        // Click Owner Available
//        onView(withId(R.id.tabs_sub_owned_available)).perform(click());
//        onView(withId(R.layout.available)).check(matches(isDisplayed()));
//
//
//        // Click Owner Requests
//        onView(withId(R.id.tabs_sub_owned_requests)).perform(click());
//        onView(withId(R.layout.reqom)).check(matches(isDisplayed()));
//
//        // Click Owner Accepted
//        onView(withId(R.id.tabs_sub_owned_accepted)).perform(click());
//        onView(withId(R.layout.accepted)).check(matches(isDisplayed()));
//
//        // Click Owner Lent
//        onView(withId(R.id.tabs_sub_owned_lent)).perform(click());
//        onView(withId(R.layout.lent)).check(matches(isDisplayed()));
//    }

//    @Test
//    public void testBorrowedTabsSwitch() {
//        onView(withId(R.id.tab_borrowed_books)).perform(click());
//
//        // Click Borrower Borrowed Tab
//        onView(withId(R.id.tabs_sub_borrowed_borrowed)).perform(click());
//        onView(withId(R.layout.borrowing)).check(matches(isDisplayed()));
//
//        // Click Borrrower Requested Tab
//        onView(withId(R.id.tabs_sub_borrowed_requested)).perform(click());
//        onView(withId(R.layout.reqbm)).check(matches(isDisplayed()));
//
//
//        // Click Borrrower Accepted Tab
//        onView(withId(R.id.tabs_sub_borrowed_accepted)).perform(click());
//        onView(withId(R.layout.accepted_request)).check(matches(isDisplayed()));

//    }


//    @Test
//    public void testBookActivityTabsSwitch() {
//        // Check to see if tabs click works
//        onView(withId(R.id.tab_borrowed_books)).perform(click());
//        onView(withId(R.id.borrowed_fragment)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.tab_search)).perform(click());
//        onView(withId(R.id.search_fragment)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.tab_location)).perform(click());
//        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.tab_settings)).perform(click());
//        onView(withId(R.id.settings_fragment)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.tab_owned_books)).perform(click());
//        onView(withId(R.id.owned_fragment)).check(matches(isDisplayed()));
//    }


    @Test
    public void testAddFragmentActivity(){
        // Checks to see if the add fragment works
        onView(withId(R.id.toolbar_add)).perform(click());
        onView(withId(R.id.add_book_fragment)).check(matches(isDisplayed()));
    }

//    @Test
//    public void testEditFragmentActivity() throws InterruptedException {
        // Checks to see the edit fragment works
//        onView(withId(R.id.toolbar_add)).perform(click());
//        onView(withId(R.id.add_book_fragment)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.add_book_author)).perform(typeText("Book Author"), closeSoftKeyboard());
//        onView(withId(R.id.add_book_title)).perform(typeText("Book Title"), closeSoftKeyboard());
//        onView(withId(R.id.add_book_isbn)).perform(typeText("1234567891"), closeSoftKeyboard());
//
//        onView(withId(R.id.add_book_add_button)).perform(click());
//
//        onView(withId(R.id.right_button)).perform(click());
//        Thread.sleep(1000);
//        onView(withId(R.id.edit_book)).perform(click());
//
//        onView(withId(R.id.edit_book_fragment)).check((matches(isDisplayed())));
//
//        onView(withId(R.id.tabs_sub_owned_available)).perform(click());
//        onView(withId(R.id.available_fragment)).check(matches(isDisplayed()));

//    }
}
