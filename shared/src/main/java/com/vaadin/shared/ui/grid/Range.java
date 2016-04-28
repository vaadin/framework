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

/**
 * An immutable representation of a range, marked by start and end points.
 * <p>
 * The range is treated as inclusive at the start, and exclusive at the end.
 * I.e. the range [0..1[ has the length 1, and represents one integer: 0.
 * <p>
 * The range is considered {@link #isEmpty() empty} if the start is the same as
 * the end.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public final class Range implements Serializable {
    private final int start;
    private final int end;

    /**
     * Creates a range object representing a single integer.
     * 
     * @param integer
     *            the number to represent as a range
     * @return the range represented by <code>integer</code>
     */
    public static Range withOnly(final int integer) {
        return new Range(integer, integer + 1);
    }

    /**
     * Creates a range between two integers.
     * <p>
     * The range start is <em>inclusive</em> and the end is <em>exclusive</em>.
     * So, a range "between" 0 and 5 represents the numbers 0, 1, 2, 3 and 4,
     * but not 5.
     * 
     * @param start
     *            the start of the the range, inclusive
     * @param end
     *            the end of the range, exclusive
     * @return a range representing <code>[start..end[</code>
     * @throws IllegalArgumentException
     *             if <code>start &gt; end</code>
     */
    public static Range between(final int start, final int end)
            throws IllegalArgumentException {
        return new Range(start, end);
    }

    /**
     * Creates a range from a start point, with a given length.
     * 
     * @param start
     *            the first integer to include in the range
     * @param length
     *            the length of the resulting range
     * @return a range starting from <code>start</code>, with
     *         <code>length</code> number of integers following
     * @throws IllegalArgumentException
     *             if length &lt; 0
     */
    public static Range withLength(final int start, final int length)
            throws IllegalArgumentException {
        if (length < 0) {
            /*
             * The constructor of Range will throw an exception if start >
             * start+length (i.e. if length is negative). We're throwing the
             * same exception type, just with a more descriptive message.
             */
            throw new IllegalArgumentException("length must not be negative");
        }
        return new Range(start, start + length);
    }

    /**
     * Creates a new range between two numbers: <code>[start..end[</code>.
     * 
     * @param start
     *            the start integer, inclusive
     * @param end
     *            the end integer, exclusive
     * @throws IllegalArgumentException
     *             if <code>start &gt; end</code>
     */
    private Range(final int start, final int end)
            throws IllegalArgumentException {
        if (start > end) {
            throw new IllegalArgumentException(
                    "start must not be greater than end");
        }

        this.start = start;
        this.end = end;
    }

    /**
     * Returns the <em>inclusive</em> start point of this range.
     * 
     * @return the start point of this range
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the <em>exclusive</em> end point of this range.
     * 
     * @return the end point of this range
     */
    public int getEnd() {
        return end;
    }

    /**
     * The number of integers contained in the range.
     * 
     * @return the number of integers contained in the range
     */
    public int length() {
        return getEnd() - getStart();
    }

    /**
     * Checks whether the range has no elements between the start and end.
     * 
     * @return <code>true</code> iff the range contains no elements.
     */
    public boolean isEmpty() {
        return getStart() >= getEnd();
    }

    /**
     * Checks whether this range and another range are at least partially
     * covering the same values.
     * 
     * @param other
     *            the other range to check against
     * @return <code>true</code> if this and <code>other</code> intersect
     */
    public boolean intersects(final Range other) {
        return getStart() < other.getEnd() && other.getStart() < getEnd();
    }

    /**
     * Checks whether an integer is found within this range.
     * 
     * @param integer
     *            an integer to test for presence in this range
     * @return <code>true</code> iff <code>integer</code> is in this range
     */
    public boolean contains(final int integer) {
        return getStart() <= integer && integer < getEnd();
    }

    /**
     * Checks whether this range is a subset of another range.
     * 
     * @return <code>true</code> iff <code>other</code> completely wraps this
     *         range
     */
    public boolean isSubsetOf(final Range other) {
        if (isEmpty() && other.isEmpty()) {
            return true;
        }

        return other.getStart() <= getStart() && getEnd() <= other.getEnd();
    }

    /**
     * Overlay this range with another one, and partition the ranges according
     * to how they position relative to each other.
     * <p>
     * The three partitions are returned as a three-element Range array:
     * <ul>
     * <li>Elements in this range that occur before elements in
     * <code>other</code>.
     * <li>Elements that are shared between the two ranges.
     * <li>Elements in this range that occur after elements in
     * <code>other</code>.
     * </ul>
     * 
     * @param other
     *            the other range to act as delimiters.
     * @return a three-element Range array of partitions depicting the elements
     *         before (index 0), shared/inside (index 1) and after (index 2).
     */
    public Range[] partitionWith(final Range other) {
        final Range[] splitBefore = splitAt(other.getStart());
        final Range rangeBefore = splitBefore[0];
        final Range[] splitAfter = splitBefore[1].splitAt(other.getEnd());
        final Range rangeInside = splitAfter[0];
        final Range rangeAfter = splitAfter[1];
        return new Range[] { rangeBefore, rangeInside, rangeAfter };
    }

    /**
     * Get a range that is based on this one, but offset by a number.
     * 
     * @param offset
     *            the number to offset by
     * @return a copy of this range, offset by <code>offset</code>
     */
    public Range offsetBy(final int offset) {
        if (offset == 0) {
            return this;
        } else {
            return new Range(start + offset, end + offset);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + getStart() + ".." + getEnd()
                + "[" + (isEmpty() ? " (empty)" : "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + end;
        result = prime * result + start;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Range other = (Range) obj;
        if (end != other.end) {
            return false;
        }
        if (start != other.start) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether this range starts before the start of another range.
     * 
     * @param other
     *            the other range to compare against
     * @return <code>true</code> iff this range starts before the
     *         <code>other</code>
     */
    public boolean startsBefore(final Range other) {
        return getStart() < other.getStart();
    }

    /**
     * Checks whether this range ends before the start of another range.
     * 
     * @param other
     *            the other range to compare against
     * @return <code>true</code> iff this range ends before the
     *         <code>other</code>
     */
    public boolean endsBefore(final Range other) {
        return getEnd() <= other.getStart();
    }

    /**
     * Checks whether this range ends after the end of another range.
     * 
     * @param other
     *            the other range to compare against
     * @return <code>true</code> iff this range ends after the
     *         <code>other</code>
     */
    public boolean endsAfter(final Range other) {
        return getEnd() > other.getEnd();
    }

    /**
     * Checks whether this range starts after the end of another range.
     * 
     * @param other
     *            the other range to compare against
     * @return <code>true</code> iff this range starts after the
     *         <code>other</code>
     */
    public boolean startsAfter(final Range other) {
        return getStart() >= other.getEnd();
    }

    /**
     * Split the range into two at a certain integer.
     * <p>
     * <em>Example:</em> <code>[5..10[.splitAt(7) == [5..7[, [7..10[</code>
     * 
     * @param integer
     *            the integer at which to split the range into two
     * @return an array of two ranges, with <code>[start..integer[</code> in the
     *         first element, and <code>[integer..end[</code> in the second
     *         element.
     *         <p>
     *         If {@code integer} is less than {@code start}, [empty,
     *         {@code this} ] is returned. if <code>integer</code> is equal to
     *         or greater than {@code end}, [{@code this}, empty] is returned
     *         instead.
     */
    public Range[] splitAt(final int integer) {
        if (integer < start) {
            return new Range[] { Range.withLength(start, 0), this };
        } else if (integer >= end) {
            return new Range[] { this, Range.withLength(end, 0) };
        } else {
            return new Range[] { new Range(start, integer),
                    new Range(integer, end) };
        }
    }

    /**
     * Split the range into two after a certain number of integers into the
     * range.
     * <p>
     * Calling this method is equivalent to calling
     * <code>{@link #splitAt(int) splitAt}({@link #getStart()}+length);</code>
     * <p>
     * <em>Example:</em>
     * <code>[5..10[.splitAtFromStart(2) == [5..7[, [7..10[</code>
     * 
     * @param length
     *            the length at which to split this range into two
     * @return an array of two ranges, having the <code>length</code>-first
     *         elements of this range, and the second range having the rest. If
     *         <code>length</code> &leq; 0, the first element will be empty, and
     *         the second element will be this range. If <code>length</code>
     *         &geq; {@link #length()}, the first element will be this range,
     *         and the second element will be empty.
     */
    public Range[] splitAtFromStart(final int length) {
        return splitAt(getStart() + length);
    }

    /**
     * Combines two ranges to create a range containing all values in both
     * ranges, provided there are no gaps between the ranges.
     * 
     * @param other
     *            the range to combine with this range
     * 
     * @return the combined range
     * 
     * @throws IllegalArgumentException
     *             if the two ranges aren't connected
     */
    public Range combineWith(Range other) throws IllegalArgumentException {
        if (getStart() > other.getEnd() || other.getStart() > getEnd()) {
            throw new IllegalArgumentException("There is a gap between " + this
                    + " and " + other);
        }

        return Range.between(Math.min(getStart(), other.getStart()),
                Math.max(getEnd(), other.getEnd()));
    }

    /**
     * Creates a range that is expanded the given amounts in both ends.
     * 
     * @param startDelta
     *            the amount to expand by in the beginning of the range
     * @param endDelta
     *            the amount to expand by in the end of the range
     * 
     * @return an expanded range
     * 
     * @throws IllegalArgumentException
     *             if the new range would have <code>start &gt; end</code>
     */
    public Range expand(int startDelta, int endDelta)
            throws IllegalArgumentException {
        return Range.between(getStart() - startDelta, getEnd() + endDelta);
    }

    /**
     * Limits this range to be within the bounds of the provided range.
     * <p>
     * This is basically an optimized way of calculating
     * <code>{@link #partitionWith(Range)}[1]</code> without the overhead of
     * defining the parts that do not overlap.
     * <p>
     * If the two ranges do not intersect, an empty range is returned. There are
     * no guarantees about the position of that range.
     * 
     * @param bounds
     *            the bounds that the returned range should be limited to
     * @return a bounded range
     */
    public Range restrictTo(Range bounds) {
        boolean startWithin = bounds.contains(getStart());
        boolean endWithin = bounds.contains(getEnd());
        boolean boundsWithin = getStart() < bounds.getStart()
                && getEnd() >= bounds.getEnd();

        if (startWithin) {
            if (endWithin) {
                return this;
            } else {
                return Range.between(getStart(), bounds.getEnd());
            }
        } else {
            if (endWithin) {
                return Range.between(bounds.getStart(), getEnd());
            } else if (boundsWithin) {
                return bounds;
            } else {
                return Range.withLength(getStart(), 0);
            }
        }
    }
}
