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
import java.util.Comparator;

import com.vaadin.shared.Connector;

/**
 * A description of an indexing modification for a connector. This is used by
 * Grid for internal bookkeeping updates.
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public class DetailsConnectorChange implements Serializable {

    public static final Comparator<DetailsConnectorChange> REMOVED_FIRST_COMPARATOR = new Comparator<DetailsConnectorChange>() {
        @Override
        public int compare(DetailsConnectorChange a, DetailsConnectorChange b) {
            boolean deleteA = a.getNewIndex() == null;
            boolean deleteB = b.getNewIndex() == null;
            if (deleteA && !deleteB) {
                return -1;
            } else if (!deleteA && deleteB) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    private Connector connector;
    private Integer oldIndex;
    private Integer newIndex;
    private boolean shouldStillBeVisible;

    /** Create a new connector index change */
    public DetailsConnectorChange() {
    }

    /**
     * Convenience constructor for setting all the fields in one line.
     * <p>
     * Calling this constructor will also assert that the state of the pojo is
     * consistent by internal assumptions.
     * 
     * @param connector
     *            the changed connector
     * @param oldIndex
     *            the old index
     * @param newIndex
     *            the new index
     * @param shouldStillBeVisible
     *            details should be visible regardless of {@code connector}
     */
    public DetailsConnectorChange(Connector connector, Integer oldIndex,
            Integer newIndex, boolean shouldStillBeVisible) {
        this.connector = connector;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
        this.shouldStillBeVisible = shouldStillBeVisible;

        assert assertStateIsOk();
    }

    private boolean assertStateIsOk() {
        boolean connectorAndNewIndexIsNotNull = connector != null
                && newIndex != null;
        boolean connectorAndNewIndexIsNullThenOldIndexIsSet = connector == null
                && newIndex == null && oldIndex != null;

        assert (connectorAndNewIndexIsNotNull || connectorAndNewIndexIsNullThenOldIndexIsSet) : "connector: "
                + nullityString(connector)
                + ", oldIndex: "
                + nullityString(oldIndex)
                + ", newIndex: "
                + nullityString(newIndex);
        return true;
    }

    private static String nullityString(Object object) {
        return object == null ? "null" : "non-null";
    }

    /**
     * Gets the old index for the connector.
     * <p>
     * If <code>null</code>, the connector is recently added. This means that
     * {@link #getConnector()} is expected not to return <code>null</code>.
     * 
     * @return the old index for the connector
     */
    public Integer getOldIndex() {
        assert assertStateIsOk();
        return oldIndex;
    }

    /**
     * Gets the new index for the connector.
     * <p>
     * If <code>null</code>, the connector should be removed. This means that
     * {@link #getConnector()} is expected to return <code>null</code> as well.
     * 
     * @return the new index for the connector
     */
    public Integer getNewIndex() {
        assert assertStateIsOk();
        return newIndex;
    }

    /**
     * Gets the changed connector.
     * 
     * @return the changed connector. Might be <code>null</code>
     */
    public Connector getConnector() {
        assert assertStateIsOk();
        return connector;
    }

    /**
     * Sets the changed connector.
     * 
     * @param connector
     *            the changed connector. May be <code>null</code>
     */
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    /**
     * Sets the old index
     * 
     * @param oldIndex
     *            the old index. May be <code>null</code> if a new connector is
     *            being inserted
     */
    public void setOldIndex(Integer oldIndex) {
        this.oldIndex = oldIndex;
    }

    /**
     * Sets the new index
     * 
     * @param newIndex
     *            the new index. May be <code>null</code> if a connector is
     *            being removed
     */
    public void setNewIndex(Integer newIndex) {
        this.newIndex = newIndex;
    }

    /**
     * Checks whether whether the details should remain open, even if connector
     * might be <code>null</code>.
     * 
     * @return <code>true</code> iff the details should remain open, even if
     *         connector might be <code>null</code>
     */
    public boolean isShouldStillBeVisible() {
        return shouldStillBeVisible;
    }

    /**
     * Sets whether the details should remain open, even if connector might be
     * <code>null</code>.
     * 
     * @param shouldStillBeVisible
     *            <code>true</code> iff the details should remain open, even if
     *            connector might be <code>null</code>
     */
    public void setShouldStillBeVisible(boolean shouldStillBeVisible) {
        this.shouldStillBeVisible = shouldStillBeVisible;
    }
}
