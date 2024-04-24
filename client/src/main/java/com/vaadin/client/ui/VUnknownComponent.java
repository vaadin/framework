/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.client.SimpleTree;

/**
 * A placeholder widget class for when a component's connector cannot be
 * determined and a placeholder connector is used instead.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("deprecation")
public class VUnknownComponent extends Composite {

    com.google.gwt.user.client.ui.Label caption = new com.google.gwt.user.client.ui.Label();
    /** Unused. Only here for historical reasons. */
    SimpleTree uidlTree;
    /** The base widget of this composite. */
    protected VerticalPanel panel;

    /**
     * Constructs a placeholder widget.
     */
    public VUnknownComponent() {
        panel = new VerticalPanel();
        panel.add(caption);
        initWidget(panel);
        setStyleName("vaadin-unknown");
        caption.setStyleName("vaadin-unknown-caption");
    }

    /**
     * Sets the content text for this placeholder. Can contain HTML.
     *
     * @param c
     *            the content text to set
     */
    public void setCaption(String c) {
        caption.getElement().setInnerHTML(c);
    }
}
