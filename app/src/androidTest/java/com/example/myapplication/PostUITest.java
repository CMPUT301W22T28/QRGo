package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * UI tests PostFragment and it's inner fragments
 *
 * @author Marc-Andre Haley
 *
 * @see com.example.myapplication.fragments.post.PostFragment
 * @see com.example.myapplication.fragments.post.listfragment.CommentsFragment
 * @see com.example.myapplication.fragments.post.listfragment.ScannedByFragment
 * @see com.example.myapplication.fragments.post.postcontent.PostInfoFragment
 *
 * April 2, 2022
 *
 */
@RunWith(AndroidJUnit4.class)
public class PostUITest {
    private static final String USERS_COLLECTION = "Users";
    private static final String POST_COLLECTION = "Posts";
    private static final String SCORINGQR_COLLECTION = "ScoringQRCodes";
    private static final String testUsername = "testingTestingUsername";
    private static final String testQrHash = "x7fZHWF2mivfZNBnUZfk";
    private static final String testPostId = "testpostid123";
    // Firestore db
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        DocumentReference codeRef = db.collection(SCORINGQR_COLLECTION).document(testQrHash);
        DocumentReference postRef = db.collection(POST_COLLECTION).document(testPostId);
        // add qrcode to user
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("scanned_qrcodes", FieldValue.arrayUnion(testQrHash));
        userRef.update(userMap);
        // add qrcode in database
        Map<String, Object> codeMap = new HashMap<>();
        codeMap.put("score", 100);
        codeMap.put("num_scanned_by", 0);
        codeRef.update(codeMap);
        // add post in database
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("qrcode_hash", testQrHash);
        postMap.put("username", testUsername);
        postRef.update(postMap);
    }

    /**
     * removes the test qr code from the database after testing is completed
     */
    @AfterClass
    public static void removeFromDatabase() {

        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        DocumentReference codeRef = db.collection(SCORINGQR_COLLECTION).document(testQrHash);
        DocumentReference postRef = db.collection(POST_COLLECTION).document(testPostId);
        // remove from user collection
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("scanned_qrcodes", FieldValue.arrayRemove(testQrHash));
        userRef.update(userMap);
        // remove from ScoringQRCode collection
        codeRef.delete();
        // remove post
        postRef.delete();
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
     * test that the right post appears when clicking on it from profile fragment
     *
     * source : https://stackoverflow.com/questions/40140700/testing-recyclerview-if-it-has-data-with-espresso
     */
    @Test
    public void clickOnPostTest() {
        if (getRVcount() > 0) {
            onView(withId(R.id.scoring_qr_code_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        }
        onView(withId(R.id.post_parent_fragment)).check(matches(isDisplayed()));
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

    private int getRVcount(){
        RecyclerView recyclerView = (RecyclerView) mainActivityActivityTestRule.getActivity().findViewById(R.id.scoring_qr_code_list);
        return recyclerView.getAdapter().getItemCount();
    }
}
