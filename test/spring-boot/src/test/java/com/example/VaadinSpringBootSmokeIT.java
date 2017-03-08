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
package com.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.Browser;

/**
 * @author Vaadin Ltd
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class VaadinSpringBootSmokeIT extends TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotRule = new ScreenshotOnFailureRule(
            this, true);

    @LocalServerPort
    Integer port;

    @Before
    public void setUp() {
        setDriver(TestBench.createDriver(new PhantomJSDriver(
                Browser.PHANTOMJS.getDesiredCapabilities())));
    }

    @Test
    public void testPageLoadsAndButtonWorks() {
        getDriver().navigate().to("http://localhost:" + port + "");
        $(ButtonElement.class).first().click();
        Assert.assertTrue($(NotificationElement.class).exists());
        Assert.assertEquals(ThankYouService.THANK_YOU_TEXT,
                $(NotificationElement.class).first().getText());
    }
}
