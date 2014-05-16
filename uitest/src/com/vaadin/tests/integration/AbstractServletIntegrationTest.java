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
package com.vaadin.tests.integration;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;

/**
 * Base class for servlet integration tests. Automatically prepends "/demo" to
 * the deployment path
 * 
 * @author Vaadin Ltd
 */
public abstract class AbstractServletIntegrationTest extends
        AbstractIntegrationTest {

    @Test
    public void runTest() throws IOException, AssertionError {
        openTestURL();
        compareScreen("initial");
        $(TableElement.class).first().getCell(0, 1).click();
        compareScreen("finland");
    }

    @Override
    protected String getDeploymentPath() {
        return "/demo" + super.getDeploymentPath();
    }

}
