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
package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.RenderInformation.Size;

/**
 * Contains information about render area.
 */
public class RenderSpace extends Size {

    private int scrollBarSize = 0;

    public RenderSpace(int width, int height) {
        super(width, height);
    }

    public RenderSpace() {
    }

    public RenderSpace(int width, int height, boolean useNativeScrollbarSize) {
        super(width, height);
        if (useNativeScrollbarSize) {
            scrollBarSize = Util.getNativeScrollbarSize();
        }
    }

    /**
     * Returns pixels available vertically for contained widget, including
     * possible scrollbars.
     */
    @Override
    public int getHeight() {
        return super.getHeight();
    }

    /**
     * Returns pixels available horizontally for contained widget, including
     * possible scrollbars.
     */
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    /**
     * In case containing block has oveflow: auto, this method must return
     * number of pixels used by scrollbar. Returning zero means either that no
     * scrollbar will be visible.
     */
    public int getScrollbarSize() {
        return scrollBarSize;
    }

}
