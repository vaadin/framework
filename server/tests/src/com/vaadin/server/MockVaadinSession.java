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
package com.vaadin.server;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Vaadin Ltd
 */
public class MockVaadinSession extends VaadinSession {

    public MockVaadinSession(VaadinService service) {
        super(service);
    }

    @Override
    public void close() {
        super.close();
        closeCount++;
    }

    public int getCloseCount() {
        return closeCount;
    }

    @Override
    public Lock getLockInstance() {
        return lock;
    }

    private int closeCount;

    private ReentrantLock lock = new ReentrantLock();
}
