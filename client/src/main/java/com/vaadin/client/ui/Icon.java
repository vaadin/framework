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

import com.google.gwt.user.client.ui.UIObject;
import com.vaadin.client.ApplicationConnection;

/**
 * An abstract representation of an icon.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public abstract class Icon extends UIObject {

    public static final String CLASSNAME = "v-icon";

    /**
     * Sets the URI for the icon. The URI should be run trough
     * {@link ApplicationConnection#translateVaadinUri(String)} before setting.
     * <p>
     * This might be a URL referencing a image (e.g {@link ImageIcon}) or a
     * custom URI (e.g {@link FontIcon}).
     * </p>
     * 
     * @param uri
     *            the URI for this icon
     */
    public abstract void setUri(String uri);

    /**
     * Gets the current URI for this icon.
     * 
     * @return URI in use
     */
    public abstract String getUri();

    /**
     * Sets the alternate text for the icon.
     * 
     * @param alternateText
     *            with the alternate text.
     */
    public abstract void setAlternateText(String alternateText);

}
