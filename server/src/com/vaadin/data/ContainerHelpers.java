package com.vaadin.data;

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
public class ContainerHelpers {

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
            throw new RangeOutOfContainerBoundsException(
                    "Cannot get all requested item ids from container. "
                            + "Container size might have changed, recalculate numberOfIds "
                            + "based on the actual container size!",
                    startIndex, numberOfIds, container.size());
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
