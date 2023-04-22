package com.pinery.audioedit;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.pinery.audioedit.util.FileUtils;

import java.io.File;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class) public class ExampleInstrumentedTest {

  @Test public void useAppContext() {
    // Context of the app under test.
    Context appContext = ApplicationProvider.getApplicationContext();
    System.out.println(appContext.getExternalFilesDir(null));
    assertEquals("com.pinery.audioedit", appContext.getPackageName());
  }
  @Test public void testCut() {

      System.out.println("aaaaaaaaaaaaaaaaaaaaaa");

      System.out.println(FileUtils.getRootDirectory());
      System.out.println(Environment.MEDIA_MOUNTED);
//      System.out.println("get: "+);

  }
}
