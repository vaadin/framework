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
package com.vaadin.tests.tb3;

public abstract class PrivateTB3Configuration extends ScreenshotTB3Test {

    @Override
    protected String getScreenshotDirectory() {
        return "C:/Vaadin/workspace/vaadin7/vaadin-screenshots";
    }

    @Override
    protected String getHubHostname() {
        return "tb3-hub.intra.itmill.com";
    }

    @Override
    protected String getDeploymentHostname() {
        return "192.168.2.75";
    }

}
