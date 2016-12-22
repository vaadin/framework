/*
 * Copyright 2000-2016 Vaadin Ltd.
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
 * The modes for height calculation that are supported by Grid (
 * client ({@code com.vaadin.client.widgets.Grid}),
 * server ({@code com.vaadin.ui.Grid server}), and
 * Escalator ({@code com.vaadin.client.widgets.Escalator Escalator}).
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public enum HeightMode {
    /**
     * The height of the Component or Widget is defined by a CSS-like value.
     * (e.g. "100px", "50em" or "25%")
     */
    CSS,

    /**
     * The height of the Component or Widget in question is defined by a number
     * of rows.
     */
    ROW,

    /**
     * The height of the Component or Widget in question is defined by its
     * contents.
     */
    UNDEFINED;
}
