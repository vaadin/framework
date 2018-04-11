/*
 * Copyright 2000-2018 Vaadin Ltd.
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
 * Collection of modes used for resizing columns in the Grid.
 *
 * @since 7.7.5
 */
public enum ColumnResizeMode {

    /**
     * When column resize mode is set to Animated, columns are resized as they
     * are dragged.
     */
    ANIMATED,

    /**
     * When column resize mode is set to Simple, dragging to resize a column
     * will show a marker, and the column will resize only after the mouse
     * button or touch is released.
     */
    SIMPLE

}
