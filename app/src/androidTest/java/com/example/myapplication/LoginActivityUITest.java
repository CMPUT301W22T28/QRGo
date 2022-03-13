package com.example.myapplication;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.activity.LoginActivity;
import com.robotium.solo.Solo;

@RunWith(AndroidJUnit4.class)
public class LoginActivityUITest {
    private Solo solo;
    private final String testingUsername = "usernameForIntentTesting";

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() {
        rule.getActivity();
    }

    @Test
    public void checkProperLogin() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }
}
