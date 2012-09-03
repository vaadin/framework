package com.vaadin.data;

/**
 * A exception that indicates that the container is unable to return all of the
 * consecutive item ids requested by the caller. This can happen if the
 * container size has changed since the input parameters for
 * {@link Container.Indexed#getItemIds(int, int)} were computed or if the
 * requested range exceeds the containers size due to some other factor.<br>
 * <br>
 * 
 * The exception can contain additional parameters for easier debugging. The
 * additional parameters are the <code>startIndex</code> and
 * <code>numberOfIds</code> which were given to
 * {@link Container.Indexed#getItemIds(int, int)} as well as the size of the
 * container when the fetch was executed. The container size can be retrieved
 * with {@link #getContainerCurrentSize()}. <br>
 * <br>
 * 
 * The additional parameters are optional but the party that received the
 * exception can check whether or not these were set by calling
 * {@link #isAdditionalParametersSet()}.
 * 
 * @since 7.0
 */
public class RangeOutOfContainerBoundsException extends RuntimeException {

    private final int startIndex;
    private final int numberOfIds;
    private final int containerCurrentSize;
    private final boolean additionalParametersSet;

    // Discourage users to create exceptions without at least some kind of
    // message...
    private RangeOutOfContainerBoundsException() {
        super();
        startIndex = -1;
        numberOfIds = -1;
        containerCurrentSize = -1;
        additionalParametersSet = false;
    }

    public RangeOutOfContainerBoundsException(String message) {
        super(message);
        startIndex = -1;
        numberOfIds = -1;
        containerCurrentSize = -1;
        additionalParametersSet = false;
    }

    public RangeOutOfContainerBoundsException(String message,
            Throwable throwable) {
        super(message, throwable);
        startIndex = -1;
        numberOfIds = -1;
        containerCurrentSize = -1;
        additionalParametersSet = false;
    }

    public RangeOutOfContainerBoundsException(Throwable throwable) {
        super(throwable);
        startIndex = -1;
        numberOfIds = -1;
        containerCurrentSize = -1;
        additionalParametersSet = false;
    }

    /**
     * Create a new {@link RangeOutOfContainerBoundsException} with the
     * additional parameters:
     * <ul>
     * <li>startIndex - start index for the query</li>
     * <li>numberOfIds - the number of consecutive item ids to get</li>
     * <li>containerCurrentSize - the size of the container during the execution
     * of the query</li>
     * </ul>
     * given.
     * 
     * @param message
     * @param startIndex
     *            the given startIndex for the query
     * @param numberOfIds
     *            the number of item ids requested
     * @param containerCurrentSize
     *            the current size of the container
     */
    public RangeOutOfContainerBoundsException(String message, int startIndex,
            int numberOfIds, int containerCurrentSize) {
        super(message);

        this.startIndex = startIndex;
        this.numberOfIds = numberOfIds;
        this.containerCurrentSize = containerCurrentSize;
        additionalParametersSet = true;
    }

    /**
     * Create a new {@link RangeOutOfContainerBoundsException} with the given
     * query parameters set in the exception along with the containers current
     * size and a @link {@link Throwable}.
     * 
     * @param message
     * @param startIndex
     *            the given startIndex for the query
     * @param numberOfIds
     *            the number of item ids queried for
     * @param containerCurrentSize
     *            the current size of the container
     * @param throwable
     */
    public RangeOutOfContainerBoundsException(String message, int startIndex,
            int numberOfIds, int containerCurrentSize, Throwable throwable) {
        super(message, throwable);

        this.startIndex = startIndex;
        this.numberOfIds = numberOfIds;
        this.containerCurrentSize = containerCurrentSize;
        additionalParametersSet = true;
    }

    /**
     * Get the given startIndex for the query. Remember to check if this
     * parameter is set by calling {@link #isAdditionalParametersSet()}
     * 
     * @return the startIndex given to the container
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Get the number of item ids requested. Remember to check if this parameter
     * is set with {@link #isAdditionalParametersSet()}
     * 
     * @return the number of item ids the container was ordered to fetch
     */
    public int getNumberOfIds() {
        return numberOfIds;
    }

    /**
     * Get the container size when the query was actually executed. Remember to
     * check if this parameter is set with {@link #isAdditionalParametersSet()}
     */
    public int getContainerCurrentSize() {
        return containerCurrentSize;
    }

    /**
     * Check whether or not the additional parameters for the exception were set
     * during creation or not.
     * 
     * The additional parameters can be retrieved with: <br>
     * <ul>
     * <li> {@link #getStartIndex()}</li>
     * <li> {@link #getNumberOfIds()}</li>
     * <li> {@link #getContainerCurrentSize()}</li>
     * </ul>
     * 
     * @return true if parameters are set, false otherwise.
     * 
     * @see #RangeOutOfContainerBoundsException(String, int, int, int)
     *      RangeOutOfContainerBoundsException(String, int, int, int) for more
     *      details on the additional parameters
     */
    public boolean isAdditionalParametersSet() {
        return additionalParametersSet;
    }

}
