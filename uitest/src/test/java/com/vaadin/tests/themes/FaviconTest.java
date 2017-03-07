/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.themes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
        assertThat(getResponseCode(theme), is(200));
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
