/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class VerifyJreVersionTest extends SingleBrowserTest {

    @Test
    public void verifyJreVersion() {
        openTestURL();

        WebElement jreVersionLabel = vaadinElementById("jreVersionLabel");

        assertThat(jreVersionLabel.getText(),
                is("Using Java 1.6.0_45 by Sun Microsystems Inc."));
    }

}
