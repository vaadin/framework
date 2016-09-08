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
package com.vaadin.data.selection;

import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.ui.AbstractListing.AbstractListingExtension;

import elemental.json.JsonObject;

/**
 * An astract base class for {@code SelectionModel}s.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            type of selected data
 *
 * @since 8.0
 */
public abstract class AbstractSelectionModel<T> extends
        AbstractListingExtension<T> implements SelectionModel<T> {

    @Override
    public void generateData(T data, JsonObject jsonObject) {
        if (isSelected(data)) {
            jsonObject.put(DataCommunicatorConstants.SELECTED, true);
        }
    }

    @Override
    public void destroyData(T data) {
    }
}
