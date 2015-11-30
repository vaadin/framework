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

/**
 * {@link DetailsGenerator} that is aware of content heights.
 * <p>
 * <b>FOR INTERNAL USE ONLY!</b> This class exists only for the sake of a
 * temporary workaround and might be removed or renamed at any time.
 * </p>
 * 
 * @since
 * @author Vaadin Ltd
 */
@Deprecated
public interface HeightAwareDetailsGenerator extends DetailsGenerator {

    /**
     * This method is called for whenever a details row's height needs to be
     * calculated.
     * <p>
     * <b>FOR INTERNAL USE ONLY!</b> This method exists only for the sake of a
     * temporary workaround and might be removed or renamed at any time.
     * </p>
     * 
     * @since
     * @param rowIndex
     *            the index of the row for which to calculate details row height
     * @return height of the details row
     */
    public double getDetailsHeight(int rowIndex);
}
