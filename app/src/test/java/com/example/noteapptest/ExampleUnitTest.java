package com.example.noteapptest;

import android.content.Context;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void distanceTest1()
    {
        assertEquals(NoteBookActivity.distance(0.0, 0.0, 0.0, 0.0), 0.0, 0.0);
    }

    @Test
    public void distanceTest2()
    {
        assertTrue(NoteBookActivity.distance(0.000000001, 0.000000001, 0.0, 0.0) < 1.0);
    }

    @Test
    public void distanceTest3()
    {
        assertTrue(NoteBookActivity.distance(1.0, 1.0, 0.0, 0.0) > 1.0);
    }
}