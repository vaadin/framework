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

import java.util.Date;

import com.vaadin.data.Property;

/**
 * <p>
 * A date entry component, which displays the actual date selector inline.
 * 
 * </p>
 * 
 * @see DateField
 * @see PopupDateField
 * @author Vaadin Ltd.
 * @since 5.0
 */
public class InlineDateField extends DateField {

    public InlineDateField() {
        super();
    }

    public InlineDateField(Property dataSource) throws IllegalArgumentException {
        super(dataSource);
    }

    public InlineDateField(String caption, Date value) {
        super(caption, value);
    }

    public InlineDateField(String caption, Property dataSource) {
        super(caption, dataSource);
    }

    public InlineDateField(String caption) {
        super(caption);
    }

}
