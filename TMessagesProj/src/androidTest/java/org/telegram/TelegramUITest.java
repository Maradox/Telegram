package org.telegram;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.json.JSONException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Example for UI-Test cases for Telegram
 *
 * Created by Martin Perebner on 20/01/16.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SdkSuppress(minSdkVersion = 18)
public class TelegramUITest {

    private static final String BASIC_SAMPLE_PACKAGE = "org.telegram.messenger.beta";
    private static final int TIMEOUT = 5000;

    private static final String PHONE_NUMBER = ""; // TODO: Enter Phone Number
    private static final String FRIEND_USER_NAME = ""; // TODO: Enter Friend User Name

    private UiDevice mDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), TIMEOUT);
    }

    @Test
    public void test1CheckPreconditions() {
        // Check if device is not null
        assertThat(mDevice, notNullValue());
    }

    @Test
    public void test2SkipOnboarding() throws UiObjectNotFoundException {
        // Check if we are on the "Telegram" Introduction screen
        assertTrue(mDevice.hasObject(By.text("Telegram")));

        // Tap the "START MESSAGING" Button to skip the onboarding
        mDevice.findObject(new UiSelector().text("START MESSAGING")).click();

        // Check that we are on the Sign-Up Screen
        assertTrue(mDevice.hasObject(By.text("Your phone")));
    }

    @Test
    public void test3Registration() throws UiObjectNotFoundException {
        // Check if we are on the "Telegram" Introduction screen
        assertTrue(mDevice.hasObject(By.text("Telegram")));

        // Tap the "START MESSAGING" Button to skip the onboarding
        mDevice.findObject(new UiSelector().text("START MESSAGING")).click();

        // Open Country Selection
        mDevice.findObject(new UiSelector().text("USA")).click();

        // Scroll to "Austria" and click it
        UiScrollable listView = new UiScrollable(new UiSelector());
        listView.setMaxSearchSwipes(100);
        listView.scrollTextIntoView("Austria");
        mDevice.findObject(new UiSelector().text("Austria")).click();

        // Enter phone number
        mDevice.findObject(new UiSelector().focused(true)).setText(PHONE_NUMBER);

        // Click "Ok" Button
        mDevice.findObject(new UiSelector().className("android.widget.ImageView")).clickAndWaitForNewWindow(TIMEOUT);

        // Check if we are on the "Your code" screen
        assertTrue(mDevice.wait(Until.hasObject(By.text("Your code")), TIMEOUT));

        // Get Confirmation Code from Local Server
        String confirmationCode = null;
        try {
            confirmationCode = TelegramHelper.getLastConfirmationCode();
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        } catch (JSONException e) {
            fail(e.getLocalizedMessage());
        }
        assertNotNull(confirmationCode);

        // Enter Confirmation Code
        mDevice.findObject(new UiSelector().focused(true)).setText(confirmationCode);

        // Check if we are in the Telegram screen
        assertTrue(mDevice.wait(Until.hasObject(By.text("Telegram")), TIMEOUT));
    }

    @Test
    public void test4ReceiveMessage() throws UiObjectNotFoundException {
        // Check if we are in the Telegram screen
        assertTrue(mDevice.wait(Until.hasObject(By.text("Telegram")), TIMEOUT));

        // Create and send a test-message
        String message = "Test: " + System.currentTimeMillis();
        try {
            TelegramHelper.sendMessage(message);
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        } catch (JSONException e) {
            fail(e.getLocalizedMessage());
        }

        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click on the first chat - which should be one the the message was sent to
        UiScrollable chatListView = new UiScrollable(new UiSelector().className("org.telegram.messenger.support.widget.RecyclerView"));
        chatListView.getChild(new UiSelector().index(0)).clickAndWaitForNewWindow(TIMEOUT);

        // Click on the last message to open the context-menu
        UiScrollable messageListView = new UiScrollable(new UiSelector().className("org.telegram.messenger.support.widget.RecyclerView"));
        chatListView.getChild(new UiSelector().index(messageListView.getChildCount() - 1)).clickAndWaitForNewWindow(TIMEOUT);

        // Click reply so that a TextField with our message appears
        mDevice.findObject(new UiSelector().text("Reply")).click();

        // Check if the TextField contains our sent message
        assertTrue(mDevice.wait(Until.hasObject(By.text(message)), TIMEOUT));

        // Hide Keyboard
        mDevice.pressBack();

        // Click cancel icon for reply
        mDevice.findObject(new UiSelector().className("android.widget.ImageView").instance(3)).clickAndWaitForNewWindow(TIMEOUT);

        // Go back to the Telegram screen. Required since clearing the task doesn't work.
        mDevice.pressBack();
    }

    @Test
    public void test5SendMessage() throws UiObjectNotFoundException {
        // Check if we are in the Telegram screen
        assertTrue(mDevice.wait(Until.hasObject(By.text("Telegram")), TIMEOUT));

        // Click on the first chat - which should be one the the message was sent to
        UiScrollable chatListView = new UiScrollable(new UiSelector().className("org.telegram.messenger.support.widget.RecyclerView"));
        chatListView.getChild(new UiSelector().index(0)).clickAndWaitForNewWindow(TIMEOUT);

        // Create a message and set it for the EditText
        String message = "Test: " + System.currentTimeMillis();
        mDevice.findObject(new UiSelector().focused(true)).setText(message);

        // Click the send-button
        mDevice.findObject(new UiSelector().className("android.widget.ImageView").index(1)).click();

        // Retrieve last message from server and check if it equals the sent message
        try {
            String serverMessage = TelegramHelper.getLastMessage();
            assertEquals(message, serverMessage);
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        } catch (JSONException e) {
            fail(e.getLocalizedMessage());
        }

        // Go back to the Telegram screen. Required since clearing the task doesn't work.
        mDevice.pressBack();
    }

    @Test
    public void test6CreateGroupChat() throws UiObjectNotFoundException {
        // Check if we are in the Telegram screen
        assertTrue(mDevice.wait(Until.hasObject(By.text("Telegram")), TIMEOUT));

        // Click the drawer-button
        mDevice.findObject(new UiSelector().className("android.widget.ImageView")).click();

        // Click the New-Group button
        mDevice.findObject(new UiSelector().text("New Group")).click();

        mDevice.findObject(new UiSelector().focused(true)).setText(FRIEND_USER_NAME);

        UiScrollable chatListView = new UiScrollable(new UiSelector().className("android.widget.ListView"));
        chatListView.getChild(new UiSelector().index(0)).click();

        // Click the done-button
        mDevice.findObject(new UiSelector().className("android.widget.ImageView").instance(1)).clickAndWaitForNewWindow(TIMEOUT);

        // Create a group-name and set it for the EditText
        String groupName = "Group: " + System.currentTimeMillis();
        mDevice.findObject(new UiSelector().focused(true)).setText(groupName);

        // Click the done-button
        mDevice.findObject(new UiSelector().className("android.widget.ImageView").instance(1)).clickAndWaitForNewWindow(TIMEOUT);

        // Check if we are in the group-chat
        assertTrue(mDevice.wait(Until.hasObject(By.text(groupName)), TIMEOUT));
        assertTrue(mDevice.hasObject(By.text("2 members")));

        // Go back to the Telegram screen. Required since clearing the task doesn't work.
        mDevice.pressBack();
    }
}
