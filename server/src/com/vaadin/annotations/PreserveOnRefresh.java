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

package com.vaadin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * Marks a UI that should be retained when the user refreshed the browser
 * window. By default, a new UI instance is created when refreshing, causing any
 * UI state not captured in the URL or the URI fragment to get discarded. By
 * adding this annotation to a UI class, the framework will instead reuse the
 * current UI instance when a reload is detected.
 * <p>
 * Whenever a request is received that reloads a preserved UI, the UI's
 * {@link UI#refresh(com.vaadin.server.VaadinRequest) refresh} method is invoked
 * by the framework.
 * <p>
 * By using
 * {@link UIProvider#isPreservedOnRefresh(com.vaadin.server.UICreateEvent)}, the
 * decision can also be made dynamically based on other parameters than only
 * whether this annotation is present on the UI class.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreserveOnRefresh {
    // Empty marker annotation
}
