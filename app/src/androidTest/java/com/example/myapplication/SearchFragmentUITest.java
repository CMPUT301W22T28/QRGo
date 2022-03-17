package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing search functions correctly
 *
 * @author Ervin Binu Joseph
 *
 * March 14, 2022
 *
 */

@RunWith(AndroidJUnit4.class)
public class SearchFragmentUITest {

    private final String myUsername = "jusrandom123";

    /**
     * The activity rule, passing in a username since we are not running the login activity
     */
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);
            intent.putExtra("Username", myUsername);
            return intent;
        }
    };

    /**
     * Initializing the test by starting the application
     */
    @Before
    public void init() {
        mainActivityActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction();

        // assert it moves to the search fragment
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.search_fragment)).check(matches(isDisplayed()));
    }

    /**
     * tests to see if querying search function reproduces required results
     */
    @Test
    public void searchTest() {
        onView(withId(R.id.search_bar)).perform(click());
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(myUsername));
        onView(withId(R.id.search_list))
                .check(matches(atPosition(0, hasDescendant(withText(myUsername)))));
    }

    // function to type into search view
    public static ViewAction typeSearchViewText(final String text){
        return new ViewAction(){

            @Override
            public Matcher<View> getConstraints() {
                //Ensure that only apply if it is a SearchView and if it is visible.
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text,true);
            }
        };
    }

    // function to check textview in recyclerview
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}

