package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
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
 * Testing everything appears correctly on the leaderboard
 *
 * @author Sankalp Saini
 *
 * March 14, 2022
 *
 */
@RunWith(AndroidJUnit4.class)
public class LeaderboardFragmentUITest {
    private final String myUsername = "sankalpsankalp";

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<MainActivity>(MainActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);
            intent.putExtra("Username", myUsername);
            return intent;
        }
    };

    @Before
    public void init() {
        testRule.getActivity().getSupportFragmentManager().beginTransaction();

        // assert it moves to profile fragment
        onView(withId(R.id.navigation_leaderboard)).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void correctUsername() {
        // check proper username
        onView(withId(R.id.personal_player_card_username)).check(matches(withText(myUsername)));
        onView(withId(R.id.personal_player_card_score)).check(matches(withText("Ranking: 1")));
    }

    @Test
    public void checkButtonsClickable() {
        // checks to make sure all of the buttons can be pressed
        onView(withText("COUNT")).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
        onView(withText("SUM")).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
        onView(withText("HIGHEST")).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
        //onView(withParent(withText(myUsername))).check(matches(isDisplayed()));
        //onView(withText("7")).check(matches(isDisplayed()));
        //onView(withText("COUNT")).check(matches(isClickable()));
    }

    @Test
    public void checkCount() {
        onView(withText("COUNT")).perform(ViewActions.click());
        onView(withId(R.id.personal_player_card_username)).check(matches(withText(myUsername)));
        onView(withId(R.id.personal_player_card_score)).check(matches(isDisplayed()));
        onView(withId(R.id.personal_player_card_score)).check(matches(withText("Ranking: 8")));
    }

    @Test
    public void checkSum() {
        onView(withText("SUM")).perform(ViewActions.click());
        onView(withId(R.id.personal_player_card_username)).check(matches(withText(myUsername)));
        onView(withId(R.id.personal_player_card_score)).check(matches(isDisplayed()));
        onView(withId(R.id.personal_player_card_score)).check(matches(withText("Ranking: 5")));
    }

    @Test
    public void checkHighest() {
        onView(withText("HIGHEST")).perform(ViewActions.click());
        onView(withId(R.id.personal_player_card_username)).check(matches(withText(myUsername)));
        onView(withId(R.id.personal_player_card_score)).check(matches(isDisplayed()));
        onView(withId(R.id.personal_player_card_score)).check(matches(withText("Ranking: 1")));
    }
}
