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
package com.vaadin.v7.shared.ui.datefield;

import java.util.Date;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.v7.shared.AbstractFieldState;

public class TextualDateFieldState extends AbstractFieldState {
    {
        primaryStyleName = "v-datefield";
    }

    /*
     * Start range that has been cleared, depending on the resolution of the
     * date field
     */
    @NoLayout
    public Date rangeStart = null;

    /*
     * End range that has been cleared, depending on the resolution of the date
     * field
     */
    @NoLayout
    public Date rangeEnd = null;
}
