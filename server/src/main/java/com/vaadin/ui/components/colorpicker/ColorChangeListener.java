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
package com.vaadin.ui.components.colorpicker;

import java.io.Serializable;

/**
 * The listener interface for receiving colorChange events. The class that is
 * interested in processing a {@link ColorChangeEvent} implements this
 * interface, and the object created with that class is registered with a
 * component using the component's <code>addColorChangeListener</code> method.
 * When the colorChange event occurs, that object's appropriate method is
 * invoked.
 * 
 * @since 7.0.0
 * 
 * @see ColorChangeEvent
 */
public interface ColorChangeListener extends Serializable {

    /**
     * Called when a new color has been selected.
     * 
     * @param event
     *            An event containing information about the color change.
     */
    void colorChanged(ColorChangeEvent event);

}
