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
package com.vaadin.data.provider;

/**
 * Data provider wrapper with a configurable filter. Changing the filter makes
 * all using components refresh their data based on the new filter.
 *
 * @since
 *
 * @param <T>
 *            the type of data items provided by this data provider
 * @param <F>
 *            filer type of this data provider. Please note that this is the
 *            type that is used for further filtering, whereas the type used by
 *            {@link #searchBy(Object)} is defined separately.
 * @param <S>
 *            searcher type. Please note that this is only the type of
 *            {@link #searchBy(Object)}, whereas the regular filter type of this
 *            data provider might something else.
 */
public interface Searcher<T, F, S> extends DataProvider<T, F> {

    /**
     * Sets the filter to search by. Makes all using components refresh their
     * data with the new filter.
     *
     * @param filter
     *            the filter to search by, or <code>null</code> to clear a
     *            previously set filter.
     */
    void searchBy(S filter);

}