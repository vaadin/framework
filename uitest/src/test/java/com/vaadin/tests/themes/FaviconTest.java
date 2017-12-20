package com.vaadin.tests.themes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

//Extending SingleBrowserTest just to include the test into our test suites.
public class FaviconTest extends SingleBrowserTest {

    @Test
    public void chameleonHasFavicon() {
        assertThatThemeHasFavicon("chameleon");
    }

    @Test
    public void runoHasFavicon() {
        assertThatThemeHasFavicon("runo");
    }

    @Test
    public void reindeerHasFavicon() {
        assertThatThemeHasFavicon("reindeer");
    }

    @Test
    public void valoHasFavicon() {
        assertThatThemeHasFavicon("valo");
    }

    private void assertThatThemeHasFavicon(String theme) {
        assertEquals(200, getResponseCode(theme));
    }

    private int getResponseCode(String theme) {
        try {
            URL url = new URL(String.format("%s/VAADIN/themes/%s/favicon.ico",
                    getBaseURL(), theme));
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            return connection.getResponseCode();

        } catch (Exception e) {
            fail(e.getMessage());
        }

        return 0;
    }
}
