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
package com.vaadin.shared.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation used to mark server RPC methods for which it isn't necessary to
 * show the loading indicator. The framework will show a loading indicator when
 * sending requests for RPC methods that are not marked with this annotation.
 * The loading indicator is hidden once a response is received.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
@Target(ElementType.METHOD)
@Documented
public @interface NoLoadingIndicator {
    // Just an empty marker annotation
}
