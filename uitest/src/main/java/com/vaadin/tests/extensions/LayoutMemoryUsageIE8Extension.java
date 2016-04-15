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
package com.vaadin.tests.extensions;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.UI;

/**
 * Test extension for finding out the size of the measuredSizes map of
 * LayoutManagerIE8.
 * 
 * This UI extension uses JSNI to register a JavaScript method
 * window.vaadin.getMeasuredSizesCount() that can be used to query the size of
 * the internal map of the layout manager. It should only be used on IE8.
 * 
 * @since 7.1.13
 * @author Vaadin Ltd
 */
public class LayoutMemoryUsageIE8Extension extends AbstractExtension {
    public void extend(UI target) {
        super.extend(target);
    }
}
