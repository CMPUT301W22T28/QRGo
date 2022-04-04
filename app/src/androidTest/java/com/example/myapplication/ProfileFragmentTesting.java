package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.myapplication.SearchFragmentUITest.atPosition;

import android.content.Intent;
import android.provider.Settings;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.activity.QRShowActivity;
import com.example.myapplication.dataClasses.qrCode.GameStatusQRCode;
import com.example.myapplication.dataClasses.qrCode.LoginQRCode;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
 * Testing everything appears correctly on the profile
 *
 * @author Walter Ostrander
 *
 * March 14th, 2022
 *
 */
@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTesting {
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
     */
    @Test
    public void addQrCodeTest() {

        // check the view for proper updated text
        onView(withId(R.id.profile_total_score)).check(matches(withText(String.valueOf(scoringQRCode.getScore()))));
        onView(withId(R.id.profile_top_qr_code)).check(matches(withText(String.valueOf(scoringQRCode.getScore()))));
        onView(withId(R.id.profile_qr_code_count)).check(matches(withText("1")));
    }

    /**
     * test that the contact info shows up when you click the contact info button
     */
    @Test
    public void testContactInfo() {
        onView(withId(R.id.profile_contact_button)).perform(ViewActions.click());
        onView(withId(R.id.profile_email)).check(matches(withText(email)));
        onView(withId(R.id.profile_phone)).check(matches(withText(phone)));
    }

    /**
     * test that the qr code shows up on the profile
     */
    @Test
    public void testQrCodeShowsUp() {
        onView(withId(R.id.scoring_qr_code_list)).check(matches(atPosition(0, hasDescendant(withText(String.valueOf(scoringQRCode.getScore()))))));
    }

    /**
     * test that the login qr code shows up
     */
    @Test
    public void testLoginQRCode() {
        Intents.init();
        onView(withId(R.id.show_login_qrcode_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(QRShowActivity.class.getName()));
        onView(withId(R.id.go_back_button)).check(matches(isDisplayed()));
        Intents.release();
    }

    /**
     * test that the game status qr code shows up
     */
    @Test
    public void testGameStatusQRCode() {
        Intents.init();
        onView(withId(R.id.show_gamestatus_qrcode_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(QRShowActivity.class.getName()));
        onView(withId(R.id.go_back_button)).check(matches(isDisplayed()));
        Intents.release();
    }
}
