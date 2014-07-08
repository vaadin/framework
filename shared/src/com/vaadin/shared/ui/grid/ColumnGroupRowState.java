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

package com.vaadin.shared.ui.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The column group row data shared between the server and client
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ColumnGroupRowState implements Serializable {

    /**
     * The groups that has been added to the row
     */
    public List<ColumnGroupState> groups = new ArrayList<ColumnGroupState>();

    /**
     * Is the header shown
     */
    public boolean headerVisible = true;

    /**
     * Is the footer shown
     */
    public boolean footerVisible = false;

}
