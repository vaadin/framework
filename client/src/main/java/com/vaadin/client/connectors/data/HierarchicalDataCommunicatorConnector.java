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
package com.vaadin.client.connectors.data;

import com.vaadin.data.provider.HierarchicalDataCommunicator;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.treegrid.TreeGridCommunicationConstants;

import elemental.json.JsonObject;

/**
 * A connector for HierarchicalDataCommunicator class.
 *
 * @since 8.0
 */
@Connect(HierarchicalDataCommunicator.class)
public class HierarchicalDataCommunicatorConnector
        extends DataCommunicatorConnector {

    @Override
    protected void onRowDataUpdate(JsonObject newRowData,
            JsonObject oldRowData) {
        assert newRowData.hasKey(
                TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION);
        assert oldRowData.hasKey(
                TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION);

        /*
         * Since server side can't know the index a random item, any
         * refreshItem(..) cannot know the depth. Thus need to copy it from
         * previous item.
         */
        JsonObject hierarchyData = newRowData.getObject(
                TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION);
        if (!hierarchyData.hasKey(TreeGridCommunicationConstants.ROW_DEPTH)) {
            hierarchyData.put(TreeGridCommunicationConstants.ROW_DEPTH,
                    oldRowData
                            .getObject(
                                    TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION)
                            .getNumber(
                                    TreeGridCommunicationConstants.ROW_DEPTH));
        }
    }

}
