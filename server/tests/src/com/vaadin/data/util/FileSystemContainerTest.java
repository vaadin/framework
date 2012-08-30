package com.vaadin.data.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class FileSystemContainerTest {

    @Test
    public void nonExistingDirectory() {
        FilesystemContainer fsc = new FilesystemContainer(new File(
                "/non/existing"));
        Assert.assertTrue(fsc.getItemIds().isEmpty());
    }
}
