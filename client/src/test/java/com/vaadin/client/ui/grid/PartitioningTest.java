package com.vaadin.client.ui.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.shared.Range;

@SuppressWarnings("static-method")
public class PartitioningTest {

    @Test
    public void selfRangeTest() {
        final Range range = Range.between(0, 10);
        final Range[] partitioning = range.partitionWith(range);

        assertTrue("before is empty", partitioning[0].isEmpty());
        assertTrue("inside is self", partitioning[1].equals(range));
        assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void beforeRangeTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(10, 20);
        final Range[] partitioning = beforeRange.partitionWith(afterRange);

        assertTrue("before is self", partitioning[0].equals(beforeRange));
        assertTrue("inside is empty", partitioning[1].isEmpty());
        assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void afterRangeTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(10, 20);
        final Range[] partitioning = afterRange.partitionWith(beforeRange);

        assertTrue("before is empty", partitioning[0].isEmpty());
        assertTrue("inside is empty", partitioning[1].isEmpty());
        assertTrue("after is self", partitioning[2].equals(afterRange));
    }

    @Test
    public void beforeAndInsideRangeTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(5, 15);
        final Range[] partitioning = beforeRange.partitionWith(afterRange);

        assertEquals("before", Range.between(0, 5), partitioning[0]);
        assertEquals("inside", Range.between(5, 10), partitioning[1]);
        assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void insideRangeTest() {
        final Range fullRange = Range.between(0, 20);
        final Range insideRange = Range.between(5, 15);
        final Range[] partitioning = insideRange.partitionWith(fullRange);

        assertTrue("before is empty", partitioning[0].isEmpty());
        assertEquals("inside", Range.between(5, 15), partitioning[1]);
        assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void insideAndBelowTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(5, 15);
        final Range[] partitioning = afterRange.partitionWith(beforeRange);

        assertTrue("before is empty", partitioning[0].isEmpty());
        assertEquals("inside", Range.between(5, 10), partitioning[1]);
        assertEquals("after", Range.between(10, 15), partitioning[2]);
    }

    @Test
    public void aboveAndBelowTest() {
        final Range fullRange = Range.between(0, 20);
        final Range insideRange = Range.between(5, 15);
        final Range[] partitioning = fullRange.partitionWith(insideRange);

        assertEquals("before", Range.between(0, 5), partitioning[0]);
        assertEquals("inside", Range.between(5, 15), partitioning[1]);
        assertEquals("after", Range.between(15, 20), partitioning[2]);
    }
}
