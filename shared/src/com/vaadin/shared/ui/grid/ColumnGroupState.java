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

package com.vaadin.shared.ui.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The column group data shared between the server and the client
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ColumnGroupState implements Serializable {

    /**
     * The columns that is included in the group
     */
    public List<String> columns = new ArrayList<String>();

    /**
     * The header text of the group
     */
    public String header;

    /**
     * The footer text of the group
     */
    public String footer;
}
