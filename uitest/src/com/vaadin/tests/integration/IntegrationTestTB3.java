/*
 * Copyright 2000-2013 Vaadin Ltd.
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

/**
 * 
 */
package com.vaadin.tests.integration;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.PrivateTB3Configuration;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
@RunWith(IntegrationTestRunner.class)
public class IntegrationTestTB3 extends PrivateTB3Configuration {
    @Test
    public void runTest() throws IOException, AssertionError {
        compareScreen("initial");

        WebElement cell = vaadinElement(getTableCell(getTable(), 0, 1));
        testBenchElement(cell).click(51, 13);

        compareScreen("finland");
    }

    private String getTableCell(String tableLocator, int row, int col) {
        return tableLocator
                + "/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild["
                + row + "]/domChild[" + col + "]/domChild[0]";
    }

    protected String getTable() {
        return "/VVerticalLayout[0]/ChildComponentContainer[0]/VScrollTable[0]";
    }

    @Override
    protected String getPath() {
        return "/demo" + super.getPath();
    }

    @Override
    protected String getBaseURL() {
        return System.getProperty("deployment.url");
    }

    @Override
    protected String getTestName() {
        // Note that this is used for screenshots. The test name shown in the
        // build server and when running the JUnit test is produced by
        // IntegrationTestRunner
        return super.getTestName() + "-" + System.getProperty("server-name");
    }

}
