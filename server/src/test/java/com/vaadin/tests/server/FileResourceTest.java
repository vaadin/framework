package com.vaadin.tests.server;

import java.io.File;

import org.junit.Test;

import com.vaadin.server.FileResource;

public class FileResourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullFile() {
        new FileResource(null);
    }

    @Test(expected = RuntimeException.class)
    public void nonExistingFile() {
        new FileResource(new File("nonexisting")).getStream();
    }

}
