/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.client.componentlocator;

import com.google.gwt.user.client.Element;

/**
 * This interface should be implemented by all locator strategies. A locator
 * strategy is responsible for generating and decoding a string that identifies
 * an element in the DOM. A strategy can implement its own syntax for the
 * locator string, which may be completely different from any other strategy's
 * syntax.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface LocatorStrategy {
    String getPathForElement(Element targetElement);

    Element getElementByPath(String path);
}
