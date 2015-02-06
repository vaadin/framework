package com.vaadin.tests.themes;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ButtonTextOverflowTest extends MultiBrowserTest {

    @Test
    public void captionOverflowIsHiddenInReindeer() throws IOException {
        openTestURL("theme=reindeer");

        compareScreen("reindeer");
    }

    @Test
    public void captionOverflowIsHiddenInValo() throws IOException {
        openTestURL("theme=valo");

        compareScreen("valo");
    }

}