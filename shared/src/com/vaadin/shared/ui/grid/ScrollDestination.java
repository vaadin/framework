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
package com.vaadin.shared.ui.grid;

/**
 * Enumeration, specifying the destinations that are supported when scrolling
 * rows or columns into view.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public enum ScrollDestination {

    /**
     * Scroll as little as possible to show the target element. If the element
     * fits into view, this works as START or END depending on the current
     * scroll position. If the element does not fit into view, this works as
     * START.
     */
    ANY,

    /**
     * Scrolls so that the element is shown at the start of the viewport. The
     * viewport will, however, not scroll beyond its contents.
     */
    START,

    /**
     * Scrolls so that the element is shown in the middle of the viewport. The
     * viewport will, however, not scroll beyond its contents, given more
     * elements than what the viewport is able to show at once. Under no
     * circumstances will the viewport scroll before its first element.
     */
    MIDDLE,

    /**
     * Scrolls so that the element is shown at the end of the viewport. The
     * viewport will, however, not scroll before its first element.
     */
    END

}
