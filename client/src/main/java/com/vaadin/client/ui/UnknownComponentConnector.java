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

package com.vaadin.client.ui;

import com.google.gwt.core.client.GWT;

public class UnknownComponentConnector extends AbstractComponentConnector {

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public VUnknownComponent getWidget() {
        return (VUnknownComponent) super.getWidget();
    }

    public void setServerSideClassName(String serverClassName) {
        getWidget()
                .setCaption(
                        "Widgetset '"
                                + GWT.getModuleName()
                                + "' does not contain implementation for "
                                + serverClassName
                                + ". Check its component connector's @Connect mapping, widgetsets "
                                + "GWT module description file and re-compile your"
                                + " widgetset. In case you have downloaded a vaadin"
                                + " add-on package, you might want to refer to "
                                + "<a href='http://vaadin.com/using-addons'>add-on "
                                + "instructions</a>.");
    }
}
