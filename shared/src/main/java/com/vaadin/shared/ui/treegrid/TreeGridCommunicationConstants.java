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
package com.vaadin.shared.ui.treegrid;

import java.io.Serializable;

import com.vaadin.shared.ui.grid.GridState;

/**
 * Set of contants used by TreeGrid. These are commonly used JsonObject keys
 * which are considered to be reserved for internal use.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class TreeGridCommunicationConstants implements Serializable {
    public static final String ROW_HIERARCHY_DESCRIPTION = "rhd";
    public static final String ROW_DEPTH = "d";
    public static final String ROW_COLLAPSED = "c";
    public static final String ROW_LEAF = "l";

    /**
     * This key is close to the {@link GridState#JSONKEY_CELLDESCRIPTION}. Upper
     * case character used to make a difference.
     */
    public static final String ROW_COLLAPSE_DISABLED = "cD";
}
