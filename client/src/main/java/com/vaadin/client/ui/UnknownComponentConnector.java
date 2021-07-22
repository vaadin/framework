/*
 * Copyright 2000-2021 Vaadin Ltd.
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

package com.vaadin.client.ui;

import com.google.gwt.core.client.GWT;

/**
 * A placeholder connector class for when a component's connector cannot be
 * determined.
 *
 * @author Vaadin Ltd
 */
public class UnknownComponentConnector extends AbstractComponentConnector {

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public VUnknownComponent getWidget() {
        return (VUnknownComponent) super.getWidget();
    }

    /**
     * Updates the placeholder widget's caption to mention the component whose
     * connector cannot be determined.
     *
     * @param serverClassName
     *            the class name of the component
     */
    public void setServerSideClassName(String serverClassName) {
        getWidget().setCaption(createMessage(serverClassName));
    }

    /**
     * Creates a message that warns about the issue with the named component and
     * gives debugging hints.
     *
     * @param serverClassName
     *            the class name of the component whose connector cannot be
     *            determined.
     * @return the warning message
     */
    public static String createMessage(String serverClassName) {
        return "Widgetset '" + GWT.getModuleName()
                + "' does not contain an implementation for " + serverClassName
                + ". Check the connector's @Connect mapping, the widgetset's "
                + "GWT module description file and re-compile your"
                + " widgetset. In case you have downloaded a vaadin"
                + " add-on package, you might want to refer to "
                + "<a href='http://vaadin.com/using-addons'>add-on "
                + "instructions</a>.";
    }
}
