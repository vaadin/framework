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
package com.vaadin.client.ui.dd;

import com.google.gwt.dom.client.Element;

/**
 * Interface implemented by widgets if the drag image used for drag'n'drop
 * requires additional initialization/configuration. The method
 * {@link #modifyDragImage(Element)} is called for each element in the
 * automatically generated drag image.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface DragImageModifier {

    /**
     * This method is called for cloned <code>element</code> which corresponds
     * to the widget element. One could modify/correct this <code>element</code>
     * for drag image.
     * 
     * @param element
     *            cloned element of drag image
     */
    void modifyDragImage(Element element);
}
