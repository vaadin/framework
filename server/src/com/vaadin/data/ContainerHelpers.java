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
package com.vaadin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Container.Indexed;

/**
 * Contains helper methods for containers that can be used to ease development
 * of containers in Vaadin.
 * 
 * @since 7.0
 */
public class ContainerHelpers implements Serializable {

    /**
     * Get a range of item ids from the container using
     * {@link Indexed#getIdByIndex(int)}. This is just a helper method to aid
     * developers to quickly add the required functionality to a Container
     * during development. This should not be used in a "finished product"
     * unless fetching an id for an index is very inexpensive because a separate
     * request will be performed for each index in the range.
     * 
     * @param startIndex
     *            index of the first item id to get
     * @param numberOfIds
     *            the number of consecutive items whose ids should be returned
     * @param container
     *            the container from which the items should be fetched
     * @return A list of item ids in the range specified
     */
    public static List<?> getItemIdsUsingGetIdByIndex(int startIndex,
            int numberOfIds, Container.Indexed container) {

        if (container == null) {
            throw new IllegalArgumentException(
                    "The given container cannot be null!");
        }

        if (startIndex < 0) {
            throw new IndexOutOfBoundsException(
                    "Start index cannot be negative! startIndex=" + startIndex);
        }

        if (startIndex > container.size()) {
            throw new IndexOutOfBoundsException(
                    "Start index exceeds container size! startIndex="
                            + startIndex + " containerLastItemIndex="
                            + (container.size() - 1));
        }

        if (numberOfIds < 1) {
            if (numberOfIds == 0) {
                return Collections.emptyList();
            }

            throw new IllegalArgumentException(
                    "Cannot get negative amount of items! numberOfItems="
                            + numberOfIds);
        }

        // not included in the range
        int endIndex = startIndex + numberOfIds;

        if (endIndex > container.size()) {
            endIndex = container.size();
        }

        ArrayList<Object> rangeOfIds = new ArrayList<Object>();
        for (int i = startIndex; i < endIndex; i++) {
            Object idByIndex = container.getIdByIndex(i);
            if (idByIndex == null) {
                throw new RuntimeException(
                        "Unable to get item id for index: "
                                + i
                                + " from container using Container.Indexed#getIdByIndex() "
                                + "even though container.size() > endIndex. "
                                + "Returned item id was null. "
                                + "Check your container implementation!");
            }
            rangeOfIds.add(idByIndex);
        }

        return Collections.unmodifiableList(rangeOfIds);
    }
}
