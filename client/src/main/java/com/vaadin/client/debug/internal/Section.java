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
package com.vaadin.client.debug.internal;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ValueMap;

/**
 * A Section is displayed as a tab in the {@link VDebugWindow}.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public interface Section {

    /**
     * Returns a button that will be used to activate this section, displayed as
     * a tab in {@link VDebugWindow}.
     * <p>
     * <em>The same instance <b>must</b> be returned each time this method is called.</em>
     * </p>
     * <p>
     * The button should preferably only have an icon (no caption), and should
     * have a longer description as title (tooltip).
     * </p>
     * 
     * @return section id
     */
    public DebugButton getTabButton();

    /**
     * Returns a widget that is placed on top of the Section content when the
     * Section (tab) is active in the {@link VDebugWindow}.
     * 
     * @return section controls
     */
    public Widget getControls();

    /**
     * Returns a widget that is the main content of the section, displayed when
     * the section is active in the {@link VDebugWindow}.
     * 
     * @return
     */
    public Widget getContent();

    /**
     * Called when the section is activated in {@link VDebugWindow}. Provides an
     * opportunity to e.g start timers, add listeners etc.
     */
    public void show();

    /**
     * Called when the section is deactivated in {@link VDebugWindow}. Provides
     * an opportunity to e.g stop timers, remove listeners etc.
     */
    public void hide();

    public void meta(ApplicationConnection ac, ValueMap meta);

    public void uidl(ApplicationConnection ac, ValueMap uidl);
}
