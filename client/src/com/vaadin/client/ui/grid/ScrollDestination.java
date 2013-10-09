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
package com.vaadin.client.ui.grid;

/**
 * The destinations that are supported in an Escalator when scrolling rows or
 * columns into view.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public enum ScrollDestination {
    /**
     * "scrollIntoView" i.e. scroll as little as possible to show the target
     * element. If the element fits into view, this works as START or END
     * depending on the current scroll position. If the element does not fit
     * into view, this works as START.
     */
    ANY,
    /**
     * Scrolls so that the element is shown at the start of the view port.
     */
    START,
    /**
     * Scrolls so that the element is shown in the middle of the view port.
     */
    MIDDLE,
    /**
     * Scrolls so that the element is shown at the end of the view port.
     */
    END
}