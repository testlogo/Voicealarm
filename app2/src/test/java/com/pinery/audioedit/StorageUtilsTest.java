package com.pinery.audioedit;


import android.os.Environment;

import com.pinery.audioedit.util.FileUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;

// this static methods to be mocked are on Environment so that must be 'prepared'
public class StorageUtilsTest {

    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();

    private File nonExistentDirectory;
    private File existentDirectory;

    @Test
    public void setup() {

        existentDirectory = storageDirectory.getRoot();
        System.out.println(existentDirectory);
        System.out.println(FileUtils.getRootDirectory());

    }


}