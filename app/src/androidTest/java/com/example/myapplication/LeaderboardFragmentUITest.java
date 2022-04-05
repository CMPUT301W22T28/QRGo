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
import android.provider.Settings;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.dataClasses.qrCode.GameStatusQRCode;
import com.example.myapplication.dataClasses.qrCode.LoginQRCode;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Testing everything appears correctly on the leaderboard
 *
 * @author Sankalp Saini, Walter Ostrander
 *
 * March 14, 2022
 *
 */
@RunWith(AndroidJUnit4.class)
public class LeaderboardFragmentUITest {
//    private final String myUsername = "sankalpsankalp";
//    private final String testQrCode = "x7fZHWF2mivfZNBnUZfk";

    private final String testUsername = "testingTestingUsername";
    private final ScoringQRCode scoringQRCode = new ScoringQRCode("FFFFFFFFFFFFFFFFFFFFFFFFFF", true);
    private final String postID = "testPostID";
    private final String USERS_COLLECTION = "Users";
    private final String POST_COLLECTION = "Posts";
    private final String QRCODE_COLLECTION = "ScoringQRCodes";
    private final String GAME_STATUS_QRCODE_COLLECTION = "GameStatusQRCode";
    private final String LOGIN_QRCODE_COLLECTION = "LoginQRCode";
    private GameStatusQRCode gameStatusQRCode;
    private LoginQRCode loginQRCode;

    private final String email = "test@email.com";
    private final String phone = "000000000";

    private String deviceID;

    private final ArrayList<String> priorUsernames = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<MainActivity>(MainActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);
            intent.putExtra("Username", testUsername);
            return intent;
        }
    };

    /**
     * Initializing the test by starting the application and going to the profile fragment
     */
    @Before
    public void setUp() {
        testRule.getActivity().getSupportFragmentManager().beginTransaction();

        deviceID = Settings.Secure.getString(testRule.getActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        addToDatabase();

        // remove the usernames from device lists
        for (String username : priorUsernames) {
            updateUserDeviceList(username, false);
        }

        // assert it moves to profile fragment
        onView(withId(R.id.navigation_leaderboard)).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
    }

    /**
     * puts the device id back where it was in previous user documents
     */
    @After
    public void setDown() {
        removeProfileFromDatabase();

        for (String username : priorUsernames) {
            updateUserDeviceList(username, true);
        }
    }

    /**
     * adds the test qr code to the database before testing
     */
    public void addToDatabase() {
        CountDownLatch done = new CountDownLatch(6);

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

        //region create game status qr code
        gameStatusQRCode = new GameStatusQRCode("gs-"+testUsername);
        Map<String, Object> data = new HashMap<>();
        data.put("username", testUsername);

        db.collection(GAME_STATUS_QRCODE_COLLECTION)
                .document(gameStatusQRCode.getHash())
                .set(data).addOnCompleteListener(unused -> done.countDown());
        //endregion

        //region create login qr code
        loginQRCode = new LoginQRCode(testUsername);
        db.collection(LOGIN_QRCODE_COLLECTION)
                .document(loginQRCode.getHash())
                .set(data).addOnCompleteListener(unused -> done.countDown());
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

    }

    /**
     * removes the test qr code from the database after testing is completed
     */
    private void removeProfileFromDatabase() {
        CountDownLatch done = new CountDownLatch(5+priorUsernames.size());

        //region delete game status qr code;
        db.collection(GAME_STATUS_QRCODE_COLLECTION)
                .document(gameStatusQRCode.getHash())
                .delete().addOnCompleteListener(unused -> done.countDown());
        //endregion

        //region create login qr code
        db.collection(LOGIN_QRCODE_COLLECTION)
                .document(loginQRCode.getHash())
                .delete().addOnCompleteListener(unused -> done.countDown());
        //endregion

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

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * takes a username, and will either update or remove androidId from it depending on addToFields
     *
     * @param username the username to update
     * @param addToFields whether or not to remove android id from it
     */
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
     * Checks to see if all of the tabs are clickable and display the leaderboard_fragment
     */
    @Test
    public void checkButtonsClickable() {
        // checks to make sure all of the buttons can be pressed
        onView(withText("COUNT")).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
        onView(withText("SUM")).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
        onView(withText("HIGHEST")).perform(ViewActions.click());
        onView(withId(R.id.leaderboard_fragment)).check(matches(isDisplayed()));
    }

    /**
     * Checks to see if the "HIGHEST" tab has the username and if the score is displayed
     */
    @Test
    public void checkHighest() {

        onView(withText("HIGHEST")).perform(ViewActions.click());
        onView(withId(R.id.personal_player_card_username)).check(matches(withText(testUsername)));
        onView(withId(R.id.personal_player_card_score)).check(matches(isDisplayed()));
        //System.out.println("PERSONAL PLAYER CARD SCORE HIGHEST: " + R.id.personal_player_card_score);
        //onView(withId(R.id.personal_player_card_score)).check(matches(withText("Ranking: 0")));
    }

    /**
     * Checks to see if the "COUNT" tab has the username and if the score is displayed
     */
    @Test
    public void checkCount() {
        onView(withText("COUNT")).perform(ViewActions.click());
        onView(withId(R.id.personal_player_card_username)).check(matches(withText(testUsername)));
        onView(withId(R.id.personal_player_card_score)).check(matches(isDisplayed()));
        //System.out.println("PERSONAL PLAYER CARD SCORE COUNT: " + R.id.personal_player_card_score);
        //onView(withId(R.id.personal_player_card_score)).check(matches(withText("RANKING: 0")));
    }

    /**
     * Checks to see if the "SUM" tab has the username and if the score is displayed
     */
    @Test
    public void checkSum() {
        onView(withText("SUM")).perform(ViewActions.click());
        onView(withId(R.id.personal_player_card_username)).check(matches(withText(testUsername)));
        onView(withId(R.id.personal_player_card_score)).check(matches(isDisplayed()));
        //System.out.println("PERSONAL PLAYER CARD SCORE SUM: " + R.id.personal_player_card_score);
        //onView(withId(R.id.personal_player_card_score)).check(matches(withText("Ranking: 0")));
    }
}
