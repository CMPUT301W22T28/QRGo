package com.example.myapplication;

import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import java.util.HashMap;
import java.util.Map;


@RunWith(AndroidJUnit4.class)
public class LoginActivityUITest {
    // Firestore collection names
    private final String USERS_COLLECTION = "Users";
    private final String LOGIN_QRCODE_COLLECTION = "LoginQRCode";

    private final String LOGIN_TAG = "LoginActivity";

    // Firestore db
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Android ID
    String androidId;

    private Solo solo;
    private final String testingUsername = "usernameForIntentTesting";
    // Previous username, to put back at the end of intent testing
    private String previousUsername = null;

    // To synchronize data fetches
    private final Object actualUsernameSyncObject = new Object();
    private boolean actualUsernameNotifyCalled;

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
                            synchronized (actualUsernameSyncObject) {
                                actualUsernameSyncObject.notify();
                            }

                        } else {
                            Log.d(LOGIN_TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    // temporarily remove device from user list
    private void updateUserDeviceList(String loginQRString, boolean addToFields){
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(loginQRString);
        Map<String, Object> map = new HashMap<>();
        if (addToFields) {
            map.put("devices", FieldValue.arrayUnion(androidId));
        }else {
            map.put("devices", FieldValue.arrayRemove(androidId));
        }
        userRef.update(map);
    }

    @Before
    public void setUp() throws Exception {
        // reset boolean value to false before each run.
        actualUsernameNotifyCalled = false;

        // remove device from database
        androidId =  Settings.Secure.getString(InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        removeDeviceFromDatabase(androidId);

        // wait for update to complete
        synchronized (actualUsernameSyncObject) {
            while(!actualUsernameNotifyCalled) {
                actualUsernameSyncObject.wait();
            }
            actualUsernameNotifyCalled = false;
        }

        // Start the app
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    @After
    public void after() throws InterruptedException {
        if (previousUsername != null) {
            // put it back into the database
            updateUserDeviceList(previousUsername, true);

            // wait for update to complete
            synchronized (actualUsernameSyncObject) {
                while(!actualUsernameNotifyCalled) {
                    actualUsernameSyncObject.wait();
                }
                actualUsernameNotifyCalled = false;
            }
        }
    }

    @Test
    public void signupToDatabaseTest() {
        // Assert the right class
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // type the username into the correct field
        solo.enterText((EditText) solo.getView(R.id.username_field_container), testingUsername);
    }

//    @Test
//    public void checkProperLogin() {
//        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
//    }
}
