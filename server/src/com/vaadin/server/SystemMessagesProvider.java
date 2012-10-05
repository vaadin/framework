/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.server;

import java.io.Serializable;
import java.util.Locale;

import com.vaadin.ui.UI;

/**
 * Gives out system messages based on Locale. Registered using
 * {@link VaadinService#setSystemMessagesProvider(SystemMessagesProvider)}.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface SystemMessagesProvider extends Serializable {
    /**
     * Gets the system messages to use in the given context. Locale is the only
     * piece of information guaranteed to be available, but in most cases some
     * or all of {@link VaadinService#getCurrent()},
     * {@link VaadinService#getCurrentRequest()},
     * {@link VaadinServiceSession#getCurrent()} and {@link UI#getCurrent()} can
     * also be used to find more information to help the decision.
     * 
     * @param locale
     *            the desired locale of the system messages
     * @return a system messages object
     */
    public SystemMessages getSystemMessages(Locale locale);
}
