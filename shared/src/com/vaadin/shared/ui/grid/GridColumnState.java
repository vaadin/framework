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

import com.vaadin.shared.Connector;

/**
 * Column state DTO for transferring column properties from the server to the
 * client
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class GridColumnState implements Serializable {

    /**
     * Id used by grid connector to map server side column with client side
     * column
     */
    public String id;

    /**
     * Header caption for the column
     */
    public String header;

    /**
     * Footer caption for the column
     */
    public String footer;

    /**
     * Has the column been hidden. By default the column is visible.
     */
    public boolean visible = true;

    /**
     * Column width in pixels. Default column width is 100px.
     */
    public int width = 100;

    public Connector rendererConnector;

    /**
     * Are sorting indicators shown for a column. Default is false.
     */
    public boolean sortable = false;
}
