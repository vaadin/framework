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
package com.vaadin.client.data;

import java.util.function.Consumer;

import com.google.gwt.core.client.Scheduler;
import com.vaadin.shared.Range;

/**
 * Helper class for creating a {@link DataChangeHandler} for a Widget that does
 * not support lazy loading.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class SimpleDataChangeHandler implements DataChangeHandler {

    /**
     * Class to request the data source to get the full data set.
     */
    private static class DelayedResetScheduler {

        private final DataSource<?> dataSource;
        private boolean scheduled = false;

        public DelayedResetScheduler(DataSource<?> dataSource) {
            this.dataSource = dataSource;
        }

        public void schedule() {
            if (scheduled) {
                return;
            }
            Scheduler.get().scheduleFinally(() -> {
                dataSource.ensureAvailability(0, dataSource.size());
                scheduled = false;
            });
            scheduled = true;
        }

        public int getExpectedSize() {
            return dataSource.size();
        }

        public boolean isScheduled() {
            return scheduled;
        }
    }

    private final DelayedResetScheduler scheduler;
    private final Consumer<Range> refreshMethod;

    SimpleDataChangeHandler(DataSource<?> dataSource,
            Consumer<Range> refreshMethod) {
        scheduler = new DelayedResetScheduler(dataSource);
        this.refreshMethod = refreshMethod;
    }

    @Override
    public void dataUpdated(int firstRowIndex, int numberOfRows) {
        scheduler.schedule();
    }

    @Override
    public void dataRemoved(int firstRowIndex, int numberOfRows) {
        scheduler.schedule();
    }

    @Override
    public void dataAdded(int firstRowIndex, int numberOfRows) {
        scheduler.schedule();
    }

    @Override
    public void dataAvailable(int firstRowIndex, int numberOfRows) {
        if (!scheduler.isScheduled() && firstRowIndex == 0
                && numberOfRows == scheduler.getExpectedSize()) {
            // All data should now be available.
            refreshMethod.accept(Range.withLength(firstRowIndex, numberOfRows));
        } else {
            scheduler.schedule();
        }
    }

    @Override
    public void resetDataAndSize(int newSize) {
        scheduler.schedule();
    }
}
