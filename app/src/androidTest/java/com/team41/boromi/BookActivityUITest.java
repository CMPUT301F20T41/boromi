package com.team41.boromi;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static dagger.internal.Preconditions.checkNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BookActivityUITest {

  /**
   * Function to find the matcher with given position
   * @param position
   * @param itemMatcher
   * @return
   */
  public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
    checkNotNull(itemMatcher);
    return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
      @Override
      public void describeTo(Description description) {
      }

      @Override
      protected boolean matchesSafely(final RecyclerView view) {
        RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
        if (viewHolder == null) {
          // returns false if no item is in the position
          return false;
        }
        return itemMatcher.matches(viewHolder.itemView);
      }
    };
  }

    /**
     * returns the matcher
     * @param parentMatcher
     * @param index
     * @return
     */
  public static Matcher<View> getAt(final Matcher<View> parentMatcher, final int index) {
    return new NthMatcher<View>(parentMatcher, index);
  }

  /**
   * returns matcher with given index, from finding from parent matcher
   * @param <T>
   */
  private static class NthMatcher<T> extends TypeSafeMatcher<T> {
    private Matcher<T> mParent;
    private int matcherNum;
    private int matcherCount = 0;

    NthMatcher(final Matcher<T> parentMatcher, int num) {
      mParent = parentMatcher;
      matcherNum = num;
    }

    @Override
    protected boolean matchesSafely(T item) {
      if (mParent.matches(item)) {
          return matcherCount++ == matcherNum;
      }
      return false;
    }

    @Override
    public void describeTo(Description description) {
    }
  }

  /**
   * Function that would let select tabs given position to click on from the tab layout
   * @param position
   * @return
   */
  @NonNull
  private static ViewAction selectTab(final int position) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
      }

      @Override
      public String getDescription() {
        return "with tab at index" + String.valueOf(position);
      }

      @Override
      public void perform(UiController uiController, View view) {
        if (view instanceof TabLayout) {
          TabLayout tabLayout = (TabLayout) view;
          TabLayout.Tab tab = tabLayout.getTabAt(position);

          if (tab != null) {
            tab.select();
          }
        }
      }
    };
  }

  /**
   * set up activity rule
   */
  @Rule
  public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

  /**
   * set up to login in boromi after every ui test
   * @throws InterruptedException
   */
  @Before
  public void setup() throws InterruptedException {
    onView(withId(R.id.welcome_login)).perform(click());
    onView(withId(R.id.login_email)).perform(typeText("rcravichan3@gmail.com"), closeSoftKeyboard());
    onView(withId(R.id.login_password)).perform(typeText("supertest"), closeSoftKeyboard());

    // Checks the contents of the email and password fields
    onView(withId(R.id.login_email)).check(matches(withText("rcravichan3@gmail.com")));
    onView(withId(R.id.login_password)).check(matches(withText("supertest")));

    onView(withId(R.id.login_login)).perform(click());

    // Validates that the loading spinner appears
    onView(withId(R.id.login_loading)).check(matches(isDisplayed()));

    Thread.sleep(3000);
  }

  /**
   * Static Tear Down function that would delete book that were added for test. Not used for every UI test
   * @throws InterruptedException
   */
  public static void tearDown() throws InterruptedException {
    onView(getAt(withId(R.id.generic_list), 0)).perform(RecyclerViewActions.actionOnItemAtPosition(0,
      new ViewAction() {
        @Override
        public Matcher<View> getConstraints() {
          return null;
        }

        @Override
        public String getDescription() {
          return "Click on delete button to delete book";
        }

        @Override
        public void perform(UiController uiController, View view) {
          try {
            Thread.sleep(2000);
            View button = view.findViewById(R.id.right_button);
            button.performClick();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      })
    );
    Thread.sleep(3000);
    onData(anything()).atPosition(1).perform(click());
  }

  /**
   * Tests tabs in owner section
   */
  @Test
  public void testOwnerTabsSwitch() {
    onView(allOf(withText("Requests"), isDescendantOfA(withId(R.id.owned_fragment)))).perform(click()).check(matches(isDisplayed()));
    onView(allOf(withText("Accepted"), isDescendantOfA(withId(R.id.owned_fragment)))).perform(click()).check(matches(isDisplayed()));
    onView(allOf(withText("Lent"), isDescendantOfA(withId(R.id.owned_fragment)))).perform(click()).check(matches(isDisplayed()));
    onView(allOf(withText("Available"), isDescendantOfA(withId(R.id.owned_fragment)))).perform(click()).check(matches(isDisplayed()));
  }

  /**
   * Tests Add Book Functionality
   * @throws InterruptedException
   */
  @Test
  public void testAddBook() throws InterruptedException {
    onView(withId(R.id.toolbar_add)).perform(click());
    onView(withId(R.id.add_book_fragment)).check(matches(isDisplayed()));
    onView(withId(R.id.add_book_author)).perform(typeText("Ravi"), closeSoftKeyboard());

    onView(withId(R.id.add_book_title)).perform(typeText("Test Intent"), closeSoftKeyboard());

    onView(withId(R.id.add_book_isbn)).perform(typeText("1234567891"), closeSoftKeyboard());

    onView(withId(R.id.add_book_add_button)).perform(click());

    Thread.sleep(5000);
    onView(getAt(withId(R.id.generic_list),0)).check(matches(atPosition(0, hasDescendant(withText("Test Intent")))));
    tearDown();
  }

  /**
   * Tests Edit Book Functionality
   * @throws InterruptedException
   */
  @Test
  public void testEditBook() throws InterruptedException {
    onView(withId(R.id.toolbar_add)).perform(click());
    onView(withId(R.id.add_book_fragment)).check(matches(isDisplayed()));
    onView(withId(R.id.add_book_author)).perform(typeText("Ravi"), closeSoftKeyboard());

    onView(withId(R.id.add_book_title)).perform(typeText("Test Intent"), closeSoftKeyboard());

    onView(withId(R.id.add_book_isbn)).perform(typeText("1234567891"), closeSoftKeyboard());

    onView(withId(R.id.add_book_add_button)).perform(click());

    Thread.sleep(3000);
    onView(getAt(withId(R.id.generic_list), 0)).perform(RecyclerViewActions.actionOnItemAtPosition(0,
      new ViewAction() {
        @Override
        public Matcher<View> getConstraints() {
          return null;
        }

        @Override
        public String getDescription() {
          return "Click on specific button";
        }

        @Override
        public void perform(UiController uiController, View view) {
          try {
            Thread.sleep(3000);
            View button = view.findViewById(R.id.right_button);
            button.performClick();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      })
    );
    Thread.sleep(3000);
    onData(anything()).atPosition(0).perform(click());
    onView(withId(R.id.edit_book_fragment)).check(matches(isDisplayed()));
    onView(withId(R.id.edit_book_title)).perform(typeText(" Edit"), closeSoftKeyboard());
    onView(withId(R.id.edit_book_add_button)).perform(click());
    Thread.sleep(3000);

    onView(getAt(withId(R.id.generic_list),0)).check(matches(atPosition(0, hasDescendant(withText("Test Intent Edit")))));
    tearDown();
  }

  /**
   * Tests Bottom Tabs Activity
   * @throws InterruptedException
   */
  @Test
  public void testBottomTabsActivity() throws InterruptedException {
    onView(withId(R.id.tabs_main)).perform(selectTab(1)).check(matches(isDisplayed()));
    Thread.sleep(1000);
    onView(withId(R.id.tabs_main)).perform(selectTab(2)).check(matches(isDisplayed()));
    Thread.sleep(1000);
    onView(withId(R.id.tabs_main)).perform(selectTab(3)).check(matches(isDisplayed()));
    Thread.sleep(1000);
    onView(withId(R.id.tabs_main)).perform(selectTab(4)).check(matches(isDisplayed()));
    Thread.sleep(1000);
    onView(withId(R.id.tabs_main)).perform(selectTab(0)).check(matches(isDisplayed()));
  }

  /**
   * Tests Tabs in Borrower Sections
   * @throws InterruptedException
   */
  @Test
  public void testBorrowedTabsActivity() throws InterruptedException {
    onView(withId(R.id.tabs_main)).perform(selectTab(1)).check(matches(isDisplayed()));
    Thread.sleep(1000);
    onView(allOf(withText("Requested"), isDescendantOfA(withId(R.id.borrowed_fragment)))).perform(click()).check(matches(isDisplayed()));
    onView(allOf(withText("Accepted"), isDescendantOfA(withId(R.id.borrowed_fragment)))).perform(click()).check(matches(isDisplayed()));
    onView(allOf(withText("Borrowed"), isDescendantOfA(withId(R.id.borrowed_fragment)))).perform(click()).check(matches(isDisplayed()));
  }
}