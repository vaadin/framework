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

import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;

/**
 * Configures server push for a {@link UI}. Adding <code>@Push</code> to a UI
 * class configures the UI for automatic push. If some other push mode is
 * desired, it can be passed as a parameter, e.g.
 * <code>@Push(PushMode.MANUAL)</code>.
 * 
 * @see PushMode
 * 
 * @author Vaadin Ltd.
 * @since 7.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Push {
    /**
     * Returns the {@link PushMode} to use for the annotated UI. The default
     * push mode when this annotation is present is {@link PushMode#AUTOMATIC}.
     * 
     * @return the push mode to use
     */
    public PushMode value() default PushMode.AUTOMATIC;

    /**
     * Returns the transport type used for the push for the annotated UI. The
     * default transport type when this annotation is present is
     * {@link Transport#WEBSOCKET}.
     * 
     * @return the transport type to use
     */
    public Transport transport() default Transport.WEBSOCKET;

}
