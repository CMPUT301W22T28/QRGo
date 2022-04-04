package com.example.myapplication;

import static org.junit.Assert.assertFalse;

import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Testing the login activity
 *
 * @author Walter Ostrander
 * @author Amro Amanuddein
 *
 * March 13, 2022
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityUITest {
    // Firestore collection names
    private final String USERS_COLLECTION = "Users";
    private final String LOGIN_QRCODE_COLLECTION = "LoginQRCode";

    private final String LOGIN_TEST_TAG = "LoginUITest";

    // Firestore db
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Android ID
    String deviceID;

    private Solo solo;
    private final String testUsername = "usrForIntentTesting";
    private final String testUsrTooLong = "usernameForIntentTesting";
    // Previous username, to put back at the end of intent testing
    private String previousUsername = null;

    private final ArrayList<String> priorUsernames = new ArrayList<>();

    // To synchronize data fetches
    private final Object actualUsernameSyncObject = new Object();
    private boolean actualUsernameNotifyCalled;


    private void getUsernames() {
        CountDownLatch done = new CountDownLatch(1);

        //region remove and store usernames used
        CollectionReference usersRef = db.collection(USERS_COLLECTION);
        usersRef.whereArrayContains("devices", deviceID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String oldUsername = document.getId();
                    priorUsernames.add(oldUsername);
                }
            }
            done.countDown();
        });
        //endregion

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addUsernames() {
        CountDownLatch done = new CountDownLatch(priorUsernames.size());

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
     * this functions sets up the device for a new sign up. Getting the android id which starts
     * the activity in the process. It then removes that device id if its present. It waits for these
     * database actions to complete. It then restarts the app and starts the tests.
     *
     * @throws Throwable will throw an error if synchronized doesn't work.
     */
    @Before
    public void setUp() throws Throwable {
        Log.d(LOGIN_TEST_TAG, "starting the thing");
        // Start the app
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // reset boolean value to false before each run.
        actualUsernameNotifyCalled = false;

        // remove device from database
        deviceID =  Settings.Secure.getString(solo.getCurrentActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        getUsernames();

        Log.d("testStuff", "here here");

        // remove the usernames from device lists
        for (String username : priorUsernames) {
            updateUserDeviceList(username, false);
        }

        Log.d("testStuff", "here");

        // restart so database can update
        solo.finishOpenedActivities();

        rule.launchActivity(rule.getActivity().getIntent());
    }

    /**
     * the activity rule for this test suite
     */
    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    /**
     * testing whether someone can sign up and get redirected to the profile
     */
    @Test
    public void signupTest() {
        Log.d(LOGIN_TEST_TAG, "started sign up test");

        // make sure current activity is right.
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // type the username into the correct field
        solo.enterText((TextInputEditText) solo.getView(R.id.username_field), testUsername);
        Assert.assertTrue(solo.waitForText(testUsername, 1, 2000));

        solo.clickOnView((Button) solo.getView(R.id.btn_sign_up));
        Assert.assertTrue(solo.waitForText("Total Score:", 1, 2000));

        Log.d(LOGIN_TEST_TAG, "signed up");
    }

    /**
     * Testing whether a username is accepted if there are too many characters.
     */
    @Test
    public void testTooManyCharacters() {
        // make sure current activity is right.
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // type the username into the correct field
        solo.enterText((TextInputEditText) solo.getView(R.id.username_field), testUsrTooLong);
        Assert.assertTrue(solo.waitForText(testUsrTooLong, 1, 2000));

        // click on the button and assert we stay in the login activity
        solo.clickOnView((Button) solo.getView(R.id.btn_sign_up));
        Assert.assertFalse(solo.waitForText("Total Score:", 1, 2000));

        Assert.assertTrue(solo.waitForText("Enter a valid username!", 1, 2000));
    }

    /**
     * TO
     */
    @Test
    public void TestAutomaticLogin() {
        Log.d(LOGIN_TEST_TAG, "started sign up test");

        // make sure current activity is right.
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // type the username into the correct field
        solo.enterText((TextInputEditText) solo.getView(R.id.username_field), testUsername);
        Assert.assertTrue(solo.waitForText(testUsername, 1, 2000));

        solo.clickOnView((Button) solo.getView(R.id.btn_sign_up));
        Assert.assertTrue(solo.waitForText("Total Score:", 1, 2000));

        // restart after signing up
        solo.finishOpenedActivities();
        rule.launchActivity(rule.getActivity().getIntent());

        // wait for profile screen
        Assert.assertTrue(solo.waitForText("Total Score:", 1, 2000));
    }

    /**
     * Runs after every test, puts the username back in the database then finishes open activities.
     */
    @After
    public void after() {
        CountDownLatch done = new CountDownLatch(1);

        addUsernames();
        solo.finishOpenedActivities();
        //region delete user
        db.collection(USERS_COLLECTION).document(testUsername).delete().addOnCompleteListener(task -> done.countDown());
        //endregion
        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
