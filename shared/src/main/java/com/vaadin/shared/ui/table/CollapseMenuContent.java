/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.shared.ui.table;

/**
 * Defines whether only collapsible columns should be shown to the user in the
 * column collapse menu.
 *
 * @see com.vaadin.ui.Table#setCollapseMenuContent(CollapseMenuContent)
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public enum CollapseMenuContent {
    /**
     * All columns are shown in the collapse menu. Columns that are not
     * collapsible are shown as disabled in the menu. This is the default
     * setting.
     */
    ALL_COLUMNS,

    /**
     * Only collapsible columns are shown in the collapse menu.
     */
    COLLAPSIBLE_COLUMNS;
}
