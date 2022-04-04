package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.provider.Settings;

import androidx.test.espresso.action.ViewActions;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Testing the main activity navigation, that it moves to new fragments when you click on the navication
 *
 * @author Walter Ostrander
 *
 * March 14, 2022
 */
public class MainActivityTest {
    private final String testUsername = "testingTestingUsername";
    private final ScoringQRCode scoringQRCode = new ScoringQRCode("FFFFFFFFFFFFFFFFFFFFFFFFFF", true);
    private final String postID = "testPostID";
    private final String USERS_COLLECTION = "Users";
    private final String POST_COLLECTION = "Posts";
    private final String QRCODE_COLLECTION = "ScoringQRCodes";

    private final String email = "test@email.com";
    private final String phone = "000000000";

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
        postMap.put("url", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/funny-dog-captions-1563456605.jpg");
        postMap.put("username", testUsername);
        postRef.set(postMap).addOnCompleteListener(task -> done.countDown());
        //endregion

        //region create user
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(testUsername);
        Map<String, Object> user = new HashMap<>();
        user.put("admin", false);
        user.put("devices", Collections.singletonList(deviceID));
        user.put("email",email);
        user.put("phone",phone);
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
     * Starting the activity
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
    }


    /**
     * setting up the phone to be able to run as a regular user.
     */
    @After
    public void setDown() {
        removeProfileFromDatabase();

        for (String username : priorUsernames) {
            updateUserDeviceList(username, true);
        }
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
