package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing everything appears correctly on the profile
 *
 * @author Walter Ostrander
 *
 * March 14th, 2022
 *
 */
@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTesting {
    private final String USERS_COLLECTION = "Users";
    private final String testUsername = "testingTestingUsername";
    private final String testQrCode = "x7fZHWF2mivfZNBnUZfk";

    // Firestore db
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
     * adds the test qr code to the database before testing
     */
    @BeforeClass
    public static void addToDatabase() {
        final String USERS_COLLECTION = "Users";
        final String testUsername = "testingTestingUsername";
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
        final String testUsername = "testingTestingUsername";
        final String testQrCode = "x7fZHWF2mivfZNBnUZfk";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        Map<String, Object> map = new HashMap<>();
        map.put("scanned_qrcodes", FieldValue.arrayRemove(testQrCode));
        userRef.update(map);
    }

    /**
     * Initializing the test by starting the application and going to the profile fragment
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

    /**
     * Tests whether adding to the database updates the profile fragment with the qrcode values.
     *
     * @throws InterruptedException
     */
    @Test
    public void addQrCodeTest() throws InterruptedException {

        // check the view for proper updated text
        onView(withId(R.id.profile_total_score)).check(matches(withText("100000")));
        onView(withId(R.id.profile_top_qr_code)).check(matches(withText("100000")));
        onView(withId(R.id.profile_qr_code_count)).check(matches(withText("1")));
    }

    /**
     * method to add a test qr code to the database
     */
    private void addTestQRCodeToDatabase() {
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        Map<String, Object> map = new HashMap<>();
        map.put("scanned_qrcodes", FieldValue.arrayUnion(testQrCode));
        userRef.update(map);
    }

    /**
     * removes the qr code from the test user so that the test can be repeated.
     */
    private void removeTestQRCodeFromDatabase() {
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        Map<String, Object> map = new HashMap<>();
        map.put("scanned_qrcodes", FieldValue.arrayRemove(testQrCode));
        userRef.update(map);
    }
}
