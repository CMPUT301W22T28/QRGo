package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Testing the main activity navigation, that it moves to new fragments when you click on the navication
 *
 * @author Walter Ostrander
 *
 * March 14, 2022
 */
public class MainActivityTest {
    private final String testUsername = "bigvalthoss";

    /**
     * The activity rule, passing in a username since we are not running the login activity
     */
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);
            intent.putExtra("Username", testUsername);
            return intent;
        }
    };

    /**
     * Starting the activity
     */
    @Before
    public void setUp() {
        mainActivityActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    /**
     * testing that the first fragment to show up is the profile
     */
    @Test
    public void testProfileShowsUp() {
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()));
    }

    /**
     * Testing that the navigation bar works correctly
     */
    @Test
    public void testChangeFragments() {
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()));

        // assert it moves to leaderboard fragment
        onView(withId(R.id.navigation_leaderboard)).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));

        // assert it moves to camera fragment
        onView(withId(R.id.navigation_camera)).perform(ViewActions.click());
        onView(withId(R.id.camera_fragment)).check(matches(isDisplayed()));

        // assert it moves to search
        onView(withId(R.id.navigation_search)).perform(ViewActions.click());
        onView(withId(R.id.search_fragment)).check(matches(isDisplayed()));

        // assert it moves to map fragment
        onView(withId(R.id.navigation_map)).perform(ViewActions.click());
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()));
    }
}
