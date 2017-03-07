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
package com.vaadin.tests.util;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.Query;

/**
 * A data provider for tests that creates "Item n" strings on the fly.
 *
 * @author Vaadin Ltd
 */
public class ItemDataProvider extends CallbackDataProvider<String, String> {

    public ItemDataProvider(int size) {
        super(q -> itemStream(q, size).skip(q.getOffset()).limit(q.getLimit()),
                q -> size(q, size));
    }

    private static Stream<String> itemStream(Query<String, String> q,
            int size) {
        Stream<String> stream = IntStream.range(0, size)
                .mapToObj(i -> "Item " + i);
        String filterText = q.getFilter().orElse("").toLowerCase(Locale.US);

        if (filterText.isEmpty()) {
            return stream;
        }

        return stream.filter(
                text -> text.toLowerCase(Locale.US).contains(filterText));
    }

    private static int size(Query<String, String> q, int size) {
        if (!q.getFilter().orElse("").isEmpty()) {
            return (int) itemStream(q, size).count();
        }
        return size;
    }
}
