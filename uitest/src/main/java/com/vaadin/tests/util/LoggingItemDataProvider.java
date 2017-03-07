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
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * CallbackDataProvider that logs info in UI on Queries it receives.
 */
public class LoggingItemDataProvider
        extends CallbackDataProvider<String, String> {

    private int counter;

    public LoggingItemDataProvider(int size, VerticalLayout logContainer) {
        super(q -> fetch(logContainer, q, size),
                q -> size(logContainer, q, size));
    }

    private static Stream<String> fetch(VerticalLayout logContainer,
            Query<String, String> query, int size) {
        log("FETCH", query, logContainer);
        return itemStream(query, size).skip(query.getOffset())
                .limit(query.getLimit());
    }

    private static int size(VerticalLayout logContainer,
            Query<String, String> query, int size) {
        log("SIZE", query, logContainer);
        return size(query, size);
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

    private static Label log(String queryType, Query<String, String> query,
            VerticalLayout logContainer) {
        int componentCount = logContainer.getComponentCount();
        Label label = new Label(componentCount + ": " + queryType + " "
                + query.getOffset() + " " + query.getLimit() + " "
                + query.getFilter().orElse(""));
        logContainer.addComponentAsFirst(label);
        label.setId(queryType + "-" + componentCount);
        label.setStyleName("log");
        return label;
    }

}
