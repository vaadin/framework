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
package com.vaadin.shared.ui.tabsheet;

import java.io.Serializable;

/**
 * Shared state of a single tab in a Tabsheet or an Accordion.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class TabState implements Serializable {

    public String caption = "";
    public boolean enabled = true;
    public boolean visible = true;
    public boolean closable = false;
    public String description = null;
    public String styleName;
    public String key;
    public String componentError;
    public String id;
    public String iconAltText;

}
