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

package com.vaadin.navigator;

import java.io.Serializable;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;

/**
 * Interface for all views controlled by the navigator.
 * 
 * Each view added to the navigator must implement this interface. Typically, a
 * view is a {@link Component}.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface View extends Serializable {

    /**
     * This view is navigated to.
     * 
     * This method is always called before the view is shown on screen.
     * {@link ViewChangeEvent#getParameters() event.getParameters()} may contain
     * extra parameters relevant to the view.
     * 
     * @param event
     *            ViewChangeEvent representing the view change that is
     *            occurring. {@link ViewChangeEvent#getNewView()
     *            event.getNewView()} returns <code>this</code>.
     * 
     */
    public void enter(ViewChangeEvent event);
}
