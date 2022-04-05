package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.myapplication.SearchFragmentUITest.atPosition;

import static org.hamcrest.Matchers.anything;

import android.content.Intent;
import android.provider.Settings;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
    private final String testUsername = "testingTestingUsername";
    private final ScoringQRCode scoringQRCode = new ScoringQRCode("FFFFFFFFFFFFFFFFFFFFFFFFFF", true);
    private final String postID = "testPostID";
    private final String USERS_COLLECTION = "Users";
    private final String POST_COLLECTION = "Posts";
    private final String QRCODE_COLLECTION = "ScoringQRCodes";

    private String deviceID;

    private final ArrayList<String> priorUsernames = new ArrayList<>();

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

    private void addProfileToDatabase() {
        CountDownLatch done = new CountDownLatch(4);

        //region remove and store usernames used
        CollectionReference usersRef = db.collection(USERS_COLLECTION);
        usersRef.whereArrayContains("devices",deviceID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String oldUsername = document.getId();
                    priorUsernames.add(oldUsername);
                }
            }
            done.countDown();
        });
        //endregion


        //region create qrcode
        DocumentReference qrCodeRef = db.collection(QRCODE_COLLECTION).document(scoringQRCode.getHash());
        scoringQRCode.setLatitude(55.0);
        scoringQRCode.setLongitude(55.0);
        Map<String, Object> qrCodeMap = new HashMap<>();
        qrCodeMap.put("comment_ids", Collections.emptyList());
        qrCodeMap.put("last_scanned", null);
        qrCodeMap.put("latitude", scoringQRCode.getLatitude());
        qrCodeMap.put("longitude", scoringQRCode.getLongitude());
        qrCodeMap.put("num_scanned_by", 1);
        qrCodeMap.put("scanned_by", Collections.singletonList(testUsername));
        qrCodeMap.put("score", scoringQRCode.getScore());

        qrCodeRef.set(qrCodeMap).addOnCompleteListener(unused -> done.countDown());
        //endregion

        //region create post
        DocumentReference postRef = db.collection(POST_COLLECTION).document(postID);
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("qrcode_hash", scoringQRCode.getHash());
        postMap.put("url", "https://firebasestorage.googleapis.com/v0/b/qrgo-e62ee.appspot.com/o/images%2F5c8dac31-3068-48f3-bd30-404acdd98ae3?alt=media&token=3728037f-eeda-4f2e-92ec-fb741804dd00");
        postMap.put("username", testUsername);
        postRef.set(postMap).addOnCompleteListener(task -> done.countDown());
        //endregion

        //region create user
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        Map<String, Object> user = new HashMap<>();
        user.put("admin", false);
        user.put("devices", Collections.singletonList(deviceID));
        String email = "test@email.com";
        user.put("email", email);
        String phone = "000000000";
        user.put("phone", phone);
        user.put("scanned_count",0);
        user.put("scanned_highest",0);
        user.put("scanned_qrcodes",Collections.singletonList(scoringQRCode.getHash()));
        user.put("scanned_sum",0);
        userRef.set(user).addOnCompleteListener(unused -> done.countDown());
        //endregion

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * removes the test qr code from the database after testing is completed
     */
    private void removeProfileFromDatabase() {
        CountDownLatch done = new CountDownLatch(3+priorUsernames.size());

        //region deletePost
        db.collection(POST_COLLECTION).document(postID).delete().addOnCompleteListener(task -> done.countDown());
        //endregion

        //region delete qr code
        db.collection(QRCODE_COLLECTION).document(scoringQRCode.getHash()).delete().addOnCompleteListener(task -> done.countDown());
        //endregion

        //region delete user
        db.collection(USERS_COLLECTION).document(testUsername).delete().addOnCompleteListener(task -> done.countDown());
        //endregion

        //region put back all the device ids
        for (String priorUsername : priorUsernames) {
            DocumentReference userRef = db.collection(USERS_COLLECTION).document(priorUsername);
            Map<String, Object> map = new HashMap<>();
            map.put("devices", FieldValue.arrayUnion(deviceID));
            userRef.update(map).addOnCompleteListener(task -> done.countDown());
        }
        //endregion
    }

    private void updateUserDeviceList(String username, boolean addToFields){
        CountDownLatch done = new CountDownLatch(1);
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(username);
        Map<String, Object> map = new HashMap<>();
        if (addToFields) {
            map.put("devices", FieldValue.arrayUnion(deviceID));
        }else {
            map.put("devices", FieldValue.arrayRemove(deviceID));
        }
        userRef.update(map).addOnCompleteListener(unused -> done.countDown());
        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializing the test by starting the application and going to the profile fragment
     */
    @Before
    public void setUp() {
        mainActivityActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction();

        deviceID = Settings.Secure.getString(mainActivityActivityTestRule.getActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        addProfileToDatabase();

        // remove the usernames from device lists
        for (String username : priorUsernames) {
            updateUserDeviceList(username, false);
        }

        // assert it moves to profile fragment
        onView(withId(R.id.navigation_profile)).perform(ViewActions.click());
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()));
    }


    @After
    public void setDown() {
        removeProfileFromDatabase();

        for (String username : priorUsernames) {
            updateUserDeviceList(username, true);
        }
    }

    /**
     * Tests whether adding to the database updates the profile fragment with the qrcode values.
     *
     */
    @Test
    public void addQrCodeTest() {

        // check the view for proper updated text
        onView(withId(R.id.profile_total_score)).check(matches(withText(String.valueOf(scoringQRCode.getScore()))));
        onView(withId(R.id.profile_top_qr_code)).check(matches(withText(String.valueOf(scoringQRCode.getScore()))));
        onView(withId(R.id.profile_qr_code_count)).check(matches(withText("1")));
    }

    /**
     * Test to see if clicking on a code takes you to its post
     */
    @Test
    public void clickOnPostTest() {
        onView(withId(R.id.scoring_qr_code_list)).check(matches(atPosition(0, hasDescendant(withText(String.valueOf(scoringQRCode.getScore()))))));
        onView(withId(R.id.scoring_qr_code_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.post_parent_fragment)).check(matches(isDisplayed()));
    }

    /**
     * test tab navigation
     */
    @Test
    public void tabNavigationTest(){
        onView(withId(R.id.scoring_qr_code_list)).check(matches(atPosition(0, hasDescendant(withText(String.valueOf(scoringQRCode.getScore()))))));
        onView(withId(R.id.scoring_qr_code_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.post_parent_fragment)).check(matches(isDisplayed()));
        // navigate to comments
        onView(withText("Comments")).perform(ViewActions.click());
        onView(withId(R.id.comment_fragment)).check(matches(isDisplayed()));
        // navigate to scanned by
        onView(withText("Scanned by")).perform(ViewActions.click());
        onView(withId(R.id.scanned_by_fragment)).check(matches(isDisplayed()));
    }

    /**
     * test adding comments
     */
    @Test
    public void addCommentTest(){
        onView(withId(R.id.scoring_qr_code_list)).check(matches(atPosition(0, hasDescendant(withText(String.valueOf(scoringQRCode.getScore()))))));
        onView(withId(R.id.scoring_qr_code_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.post_parent_fragment)).check(matches(isDisplayed()));
        // navigate to comments
        onView(withText("Comments")).perform(ViewActions.click());
        onView(withId(R.id.comment_fragment)).check(matches(isDisplayed()));
        // click on add comment button
        onView(withId(R.id.floatingActionButton)).perform((ViewActions.click()));
        onView(withId(R.id.comment_edit)).perform(ViewActions.typeText("test comment"));
        onView(withText("OK")).perform(ViewActions.click());
        // check comment was added and is displayed
        onView(withId(R.id.comment_text)).check(matches(withText("test comment")));
    }

    /**
     * test to see if the scanned by list displays the correct info
     */
    @Test
    public void scannedByListTest(){
        onView(withId(R.id.scoring_qr_code_list)).check(matches(atPosition(0, hasDescendant(withText(String.valueOf(scoringQRCode.getScore()))))));
        onView(withId(R.id.scoring_qr_code_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.post_parent_fragment)).check(matches(isDisplayed()));

        // navigate to scanned by
        onView(withText("Scanned by")).perform(ViewActions.click());
        onView(withId(R.id.scanned_by_fragment)).check(matches(isDisplayed()));
        // check to see that current user is displayed in scanned by list
        // source:
        // https://stackoverflow.com/questions/37825896/how-to-select-a-listview-in-espresso
        // answer by jitinsharma
        Espresso.onData(anything()).inAdapterView(withId(R.id.user_list)).atPosition(0).
                check(matches(withText(testUsername)));
    }

    /**
     * test removing a post
     */
    @Test
    public void removePostTest(){
        onView(withId(R.id.scoring_qr_code_list)).check(matches(atPosition(0, hasDescendant(withText(String.valueOf(scoringQRCode.getScore()))))));
        onView(withId(R.id.scoring_qr_code_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.post_parent_fragment)).check(matches(isDisplayed()));

        // click remove post button
        onView(withId(R.id.delete_post_button)).perform(ViewActions.click());
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()));
    }
}
