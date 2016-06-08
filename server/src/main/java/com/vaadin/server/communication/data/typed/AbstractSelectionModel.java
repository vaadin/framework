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
package com.vaadin.server.communication.data.typed;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Extension;
import com.vaadin.shared.data.typed.DataProviderConstants;
import com.vaadin.ui.Component;
import com.vaadin.ui.components.Listing;

import elemental.json.JsonObject;

/**
 * Abstract base class for {@link SelectionModel}s.
 * 
 * @param <T>
 *            type of selected data
 */
public abstract class AbstractSelectionModel<T> extends AbstractExtension
        implements SelectionModel<T>, TypedDataGenerator<T> {

    private Listing<T> parent;

    @Override
    public void setParentListing(Listing<T> target) {
        if (target instanceof Component && target instanceof Listing
                && target instanceof AbstractClientConnector) {
            super.extend((AbstractClientConnector) target);
            parent = target;
        } else {
            throw new IllegalArgumentException(
                    "Extended object was not suitable for a SelectionModel");
        }
    }

    // TODO: Following methods should be somewhere else being less weird.

    protected final Listing<T> getParentListing() {
        return parent;
    }

    protected final DataProvider<T> getDataProvider() {
        for (Extension e : ((Component) parent).getExtensions()) {
            if (e instanceof DataProvider) {
                return ((DataProvider<T>) e);
            }
        }
        return null;
    }

    protected final DataKeyMapper<T> getKeyMapper() {
        for (Extension e : ((Component) parent).getExtensions()) {
            if (e instanceof DataProvider) {
                return ((DataProvider<T>) e).getKeyMapper();
            }
        }
        return null;
    }

    protected final T getData(String key) {
        DataKeyMapper<T> keyMapper = getKeyMapper();
        if (keyMapper != null) {
            return keyMapper.get(key);
        }
        return null;
    }

    protected final void refresh(T value) {
        getDataProvider().refresh(value);
    }

    @Override
    public void generateData(T data, JsonObject jsonObject) {
        if (getSelected().contains(data)) {
            jsonObject.put(DataProviderConstants.SELECTED, true);
        }
    }

    @Override
    public void destroyData(T data) {
    }
}
