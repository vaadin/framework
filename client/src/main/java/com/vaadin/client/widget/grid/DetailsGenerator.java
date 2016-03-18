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
package com.vaadin.client.widget.grid;

import com.google.gwt.user.client.ui.Widget;

/**
 * A callback interface for generating details for a particular row in Grid.
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public interface DetailsGenerator {

    /** A details generator that provides no details */
    public static final DetailsGenerator NULL = new DetailsGenerator() {
        @Override
        public Widget getDetails(int rowIndex) {
            return null;
        }
    };

    /**
     * This method is called for whenever a new details row needs to be
     * generated.
     * 
     * @param rowIndex
     *            the index of the row for which to generate details
     * @return the details for the given row, or <code>null</code> to leave the
     *         details empty.
     */
    Widget getDetails(int rowIndex);
}
