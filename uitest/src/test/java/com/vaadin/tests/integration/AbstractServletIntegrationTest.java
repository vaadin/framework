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
package com.vaadin.tests.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.testbench.customelements.TableElement;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;

/**
 * Base class for servlet integration tests. Automatically prepends "/demo" to
 * the deployment path
 *
 * @author Vaadin Ltd
 */
@RunWith(ParameterizedTB3Runner.class)
public abstract class AbstractServletIntegrationTest
        extends AbstractIntegrationTest {

    private String contextPath = "/demo";

    @Test
    public void runTest() throws IOException, AssertionError {
        openTestURL();
        compareScreen("initial");
        $(TableElement.class).first().getCell(0, 1).click();
        compareScreen("finland");
    }

    @Override
    protected String getDeploymentPath(Class<?> uiClass) {
        return contextPath + super.getDeploymentPath(uiClass);
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Parameters
    public static Collection<String> getContextPaths() {
        if (getServerName().equals("wildfly9-nginx")) {
            ArrayList<String> paths = new ArrayList<>();
            paths.add("/buffering/demo");
            paths.add("/nonbuffering/demo");
            paths.add("/buffering-timeout/demo");
            paths.add("/nonbuffering-timeout/demo");
            return paths;
        } else {
            return Collections.emptyList();
        }
    }

    protected static String getServerName() {
        return System.getProperty("server-name");
    }

}
