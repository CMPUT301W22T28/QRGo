package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testing everything appears correctly on the profile
 *
 * @author Walter Ostrander
 *
 * May 14, 2022
 *
 */
@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTesting {

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
     * Initializing the test by starting the application
     */
    @Before
    public void init() {
        mainActivityActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction();

        // assert it moves to profile fragment
        onView(withId(R.id.navigation_profile)).perform(ViewActions.click());
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()));
    }

    /**
     * test that the username shows up when you navigate to the profile fragment.
     */
    @Test
    public void usernameShowsUpTest() {


        // check proper username
        onView(withText(testUsername)).check(matches(isDisplayed()));
    }

    @Test
    public void addQrCodeTest() {

    }
}
