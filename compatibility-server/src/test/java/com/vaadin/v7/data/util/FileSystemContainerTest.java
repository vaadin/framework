package com.vaadin.v7.data.util;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class FileSystemContainerTest {

    @Test
    public void nonExistingDirectory() {
        FilesystemContainer fsc = new FilesystemContainer(
                new File("/non/existing"));
        assertTrue(fsc.getItemIds().isEmpty());
    }
}
