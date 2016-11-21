package com.vaadin.tests.util;

import java.util.stream.IntStream;

import com.vaadin.server.data.BackEndDataProvider;

/**
 * A data provider for tests that creates "Item n" strings on the fly.
 *
 * @author Vaadin Ltd
 */
public class ItemDataProvider extends BackEndDataProvider<String, Object> {

    public ItemDataProvider(int size) {
        super(q -> IntStream
                .range(q.getOffset(),
                        Math.max(q.getOffset() + q.getLimit() + 1, size))
                .mapToObj(i -> "Item " + i), q -> size);
    }

}
