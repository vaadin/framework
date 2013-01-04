/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.client.ui.customfield;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.ui.customcomponent.CustomComponentConnector;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.CustomField;

@Connect(value = CustomField.class)
public class CustomFieldConnector extends CustomComponentConnector {
    @Override
    protected SharedState createState() {
        // Workaround as CustomFieldConnector does not extend
        // AbstractFieldConnector.
        return GWT.create(AbstractFieldState.class);
    }

}
