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

package com.vaadin.client.ui.aria;

/**
 * Some Widgets need to handle the caption handling for WAI-ARIA themselfs, as
 * for example the required ids need to be set in a specific way. In such a
 * case, the Widget needs to implement this interface.
 */
public interface HandlesAriaCaption {

    /**
     * Called to bind the provided caption (label in HTML speak) element to the
     * main input element of the Widget.
     * 
     * Binding should be removed from the main input field when captionElement
     * is null.
     * 
     * @param captionElement
     *            Element of the caption
     */
    void bindAriaCaption(com.google.gwt.user.client.Element captionElement);
}
