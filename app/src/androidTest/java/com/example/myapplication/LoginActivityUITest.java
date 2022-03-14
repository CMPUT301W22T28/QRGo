package com.example.myapplication;

import static org.junit.Assert.assertFalse;

import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import java.util.HashMap;
import java.util.Map;


/**
 * Testing the login activity
 *
 * @author Walter Ostrander
 *
 * May 13, 2022
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
    String androidId;

    private Solo solo;
    private final String testUsername = "usrForIntentTesting";
    private final String testUsrTooLong = "usernameForIntentTesting";
    // Previous username, to put back at the end of intent testing
    private String previousUsername = null;

    // To synchronize data fetches
    private final Object actualUsernameSyncObject = new Object();
    private boolean actualUsernameNotifyCalled;

    /**
     * This function runs and removes the current user from the database if it exists
     *
     * @param androidId the id of the current android
     */
    private void removeDeviceFromDatabase(String androidId) {
        db.collection(USERS_COLLECTION)
                .whereArrayContains("devices",androidId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // set previous username
                                previousUsername = document.getId();

                                // update document by removing device id from fields
                                updateUserDeviceList(previousUsername, false);
                            }
                            Log.d(LOGIN_TEST_TAG, "before notify");
                            synchronized (actualUsernameSyncObject) {
                                actualUsernameSyncObject.notify();
                                actualUsernameNotifyCalled = true;
                            }
                            Log.d(LOGIN_TEST_TAG, "after notify");

                        } else {
                            Log.d(LOGIN_TEST_TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    /**
     * takes a username, and will either update or remove androidId from it depending on addToFields
     *
     * @param username the username to update
     * @param addToFields whether or not to remove android id from it
     */
    private void updateUserDeviceList(String username, boolean addToFields){
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(username);
        Map<String, Object> map = new HashMap<>();
        if (addToFields) {
            map.put("devices", FieldValue.arrayUnion(androidId));
        }else {
            map.put("devices", FieldValue.arrayRemove(androidId));
        }
        userRef.update(map);
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

        // delete test username from db.
        db.collection(USERS_COLLECTION).document(testUsername).delete();

        // reset boolean value to false before each run.
        actualUsernameNotifyCalled = false;

        // remove device from database
        androidId =  Settings.Secure.getString(solo.getCurrentActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        removeDeviceFromDatabase(androidId);

        Log.d(LOGIN_TEST_TAG, "got the id, waiting on delete");

        // wait for update to complete
        synchronized (actualUsernameSyncObject) {
            while(!actualUsernameNotifyCalled) {
                actualUsernameSyncObject.wait();
            }
            actualUsernameNotifyCalled = false;
        }
        Log.d(LOGIN_TEST_TAG, "finished waiting");

        // restart so database can update
        solo.finishOpenedActivities();
        Log.d(LOGIN_TEST_TAG, "finish open activities done");

        rule.launchActivity(rule.getActivity().getIntent());
        Log.d(LOGIN_TEST_TAG, "launched activity lol");
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
        Log.d(LOGIN_TEST_TAG, "Starting after");

        // delete test username from db.
        db.collection(USERS_COLLECTION).document(testUsername).delete();

        if (previousUsername != null) {
            Log.d(LOGIN_TEST_TAG, "running put back in list");

            // put it back into the database
            updateUserDeviceList(previousUsername, true);

            Log.d(LOGIN_TEST_TAG, "done put back in list");
        }
        Log.d(LOGIN_TEST_TAG, "finished after");
        solo.finishOpenedActivities();
    }
}
