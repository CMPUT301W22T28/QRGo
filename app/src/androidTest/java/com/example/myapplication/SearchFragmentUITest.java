package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
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

    private Solo solo;
    private final String testUsername = "jusrandom123";

    /**
     * The activity rule, passing in a username since we are not running the login activity
     */
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
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

        // assert it moves to the search fragment
        onView(withId(R.id.navigation_search)).perform(ViewActions.click());
        onView(withId(R.id.search_fragment)).check(matches(isDisplayed()));
    }

    /**
     * adds the test qr code to the database before testing
     */
    @BeforeClass
    public static void addToDatabase() {
        final String USERS_COLLECTION = "Users";
        final String testUsername = "testingUsername";
        final String testQrCode = "x7fZHWF2mivfZNBnUZfk";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        Map<String, Object> map = new HashMap<>();
        map.put("scanned_qrcodes", FieldValue.arrayUnion(testQrCode));
        userRef.update(map);
    }

    /**
     * removes the test qr code from the database after testing is completed
     */
    @AfterClass
    public static void removeFromDatabase() {
        final String USERS_COLLECTION = "Users";
        final String testUsername = "testingUsername";
        final String testQrCode = "x7fZHWF2mivfZNBnUZfk";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        Map<String, Object> map = new HashMap<>();
        map.put("scanned_qrcodes", FieldValue.arrayRemove(testQrCode));
        userRef.update(map);
    }

    @Test
    public void searchTest() {

    }
}

