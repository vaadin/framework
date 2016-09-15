package com.vaadin.tests.util;

import java.util.stream.IntStream;

import com.vaadin.server.data.BackEndDataSource;

/**
 * A data source for tests that creates "Item n" strings on the fly.
 *
 * @author Vaadin Ltd
 */
public class ItemDataSource extends BackEndDataSource<String> {

    public ItemDataSource(int size) {
        super(q -> IntStream
                .range(q.getOffset(),
                        Math.max(q.getOffset() + q.getLimit() + 1, size))
                .mapToObj(i -> "Item " + i), q -> size);
    }

}
