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

package com.vaadin.tests.tb3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.runners.model.RunnerScheduler;

/**
 * JUnit scheduler capable of running multiple tets in parallel. Each test is
 * run in its own thread. Uses an {@link ExecutorService} to manage the threads.
 * 
 * @author Vaadin Ltd
 */
public class ParallelScheduler implements RunnerScheduler {
    private final List<Future<Object>> fResults = new ArrayList<Future<Object>>();
    private ExecutorService fService;

    /**
     * Creates a parallel scheduler which will use the given executor service
     * when submitting test jobs.
     * 
     * @param service
     *            The service to use for tests
     */
    public ParallelScheduler(ExecutorService service) {
        fService = service;
    }

    @Override
    public void schedule(final Runnable childStatement) {
        fResults.add(fService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                childStatement.run();
                return null;
            }
        }));
    }

    @Override
    public void finished() {
        for (Future<Object> each : fResults) {
            try {
                each.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
