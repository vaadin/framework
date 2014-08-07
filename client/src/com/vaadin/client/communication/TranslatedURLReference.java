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
package com.vaadin.client.communication;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.shared.communication.URLReference;

/**
 * A URLReference implementation which does late URL translation to be able to
 * re-translate URLs if e.g. the theme changes
 *
 * @since 7.3
 * @author Vaadin Ltd
 */
public class TranslatedURLReference extends URLReference {

    private ApplicationConnection connection;

    /**
     * @param connection
     *            the connection to set
     */
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    @Override
    public String getURL() {
        return connection.translateVaadinUri(super.getURL());
    }

}
