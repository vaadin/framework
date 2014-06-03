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
package com.vaadin.ui;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.browserframe.BrowserFrameState;

/**
 * A component displaying an embedded web page. Implemented as a HTML
 * <code>iframe</code> element.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
public class BrowserFrame extends AbstractEmbedded {

    /**
     * Creates a new empty browser frame.
     */
    public BrowserFrame() {

    }

    /**
     * Creates a new empty browser frame with the given caption.
     * 
     * @param caption
     *            The caption for the component
     */
    public BrowserFrame(String caption) {
        setCaption(caption);
    }

    /**
     * Creates a new browser frame with the given caption and content.
     * 
     * @param caption
     *            The caption for the component.
     * @param source
     *            A Resource representing the Web page that should be displayed.
     */
    public BrowserFrame(String caption, Resource source) {
        this(caption);
        setSource(source);
    }

    @Override
    protected BrowserFrameState getState() {
        return (BrowserFrameState) super.getState();
    }
}
