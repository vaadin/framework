/*
 * Copyright 2011 Vaadin Ltd.
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

import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

/**
 * Indicates that the init method in a UI class can be called before full
 * browser details ({@link WrappedRequest#getBrowserDetails()}) are available.
 * This will make the UI appear more quickly, as ensuring the availability of
 * this information typically requires an additional round trip to the client.
 * 
 * @see UI#init(com.vaadin.server.WrappedRequest)
 * @see WrappedRequest#getBrowserDetails()
 * 
 * @since 7.0
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EagerInit {
    // No values
}
