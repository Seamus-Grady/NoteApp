package com.example.noteapptest;

import android.content.Context;

import androidx.test.espresso.action.ViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<NoteBookActivity> activityRule
            = new ActivityTestRule<>(NoteBookActivity.class);

    @Rule
    public ActivityTestRule<NoteBookPages> activityRule2
            = new ActivityTestRule<>(NoteBookPages.class);

    @Rule
    public ActivityTestRule<PageActivity> activityRule3
            = new ActivityTestRule<>(PageActivity.class);

    @Test
    public void selectActivitySendsProperIntent()
    {
        int initialCount = NoteBookActivity.noteBooks.size();
        onView(withId(R.id.Add_NoteBook)).perform(click());
        onView(withId(R.id.noteBookName)).perform(click());
        onView(withId(R.id.noteBookName)).perform(typeText("newNotebook"));
        onView(withId(R.id.Save)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        int newCount = NoteBookActivity.noteBooks.size();
        assertTrue(initialCount + 1 == newCount);
    }
}
